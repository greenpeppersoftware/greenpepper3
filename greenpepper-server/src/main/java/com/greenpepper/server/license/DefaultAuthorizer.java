/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package com.greenpepper.server.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;
import com.greenpepper.util.FormatedDate;
import com.greenpepper.util.IOUtil;
import com.greenpepper.util.StringUtil;
import com.greenpepper.util.URIUtil;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class DefaultAuthorizer
		implements Authorizer {

	private static Logger log = LoggerFactory.getLogger(DefaultAuthorizer.class);
	
	private LicenseManager licenseManager;
	private SecurityContext securityContext;
	private SessionService sessionService;
	private SystemInfoDao systDao;
	private Properties properties;
	private License license;

	public DefaultAuthorizer(SessionService sessionService, Properties properties)
	{
		this.systDao = new HibernateSystemInfoDao(sessionService);
		this.sessionService = sessionService;
		this.properties = properties;
	}

	public void initialize(Date versionDate) throws Exception
	{
		File licenseFile = null;

		final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		try
		{
			licenseFile = getLicenseFile();
			securityContext = new SecurityContext();
			licenseManager = new LicenseManager(getLicenseParam());
			license = licenseFile == null ? License.invalid("gh.license.notlicensed") : (License) licenseManager.install(licenseFile);
		}
		catch (GreenPepperLicenceException ex)
		{
			license = License.invalid(ex.getId());
			log.error("Initializing license fail", ex);
		}
		catch (Exception ex)
		{
			license = License.invalid("Invalid license");
			log.error("Invalid license", ex);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(previousClassLoader);
			IOUtil.deleteFile(licenseFile);
		}
	}

	/**
	 * ReInitializes the Authorizer with the new persisted license.
	 * </p>
	 * @param newLicence
	 * @throws Exception
	 */
	public void reInitialize(String newLicence) throws Exception
	{
		File licenseFile = null;

		final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		try
		{
			licenseManager.uninstall();

			licenseFile = buildLicenceFile(newLicence);
			License newlicense = (License) licenseManager.install(licenseFile);
			if(newlicense.getLicenseType().equals(LicenseType.EVALUATION) && (license.getLicenseType().equals(LicenseType.EVALUATION) || license.getLicenseType().equals(LicenseType.EVALUATION_EXT)))
				throw new GreenPepperLicenceException(LicenseErrorKey.TRIAL_LOCKED, "Trial locked");

			license = newlicense;
			license.verify(GreenPepperServer.versionDate());
			registerLicense(licenseFile);
		}
		catch (GreenPepperLicenceException e)
		{
			initialize(null);
			throw e;
		}
		catch (Exception e)
		{
			initialize(null);
			throw new GreenPepperLicenceException("Invalid license", "Invalid license");
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(previousClassLoader);
			IOUtil.deleteFile(licenseFile);
		}
	}

	/**
	 * Verifies that the license supports the repository has the rgiht permission.
	 * </p>
	 * @param repository
	 * @param permission
	 * @throws GreenPepperLicenceException
	 */
	public void verify(Repository repository, Permission permission) throws GreenPepperLicenceException
	{
		license.verify(repository, securityContext, permission);
	}

	public LicenseBean getLicenseBean()
	{
		LicenseBean bean = new LicenseBean();
		bean.setInfo(license.getInfo());
		bean.setLicenseType(license.getLicenseType());

		// version < 2.0 license was using the extra as the GPusers, if extra is empty >= version 2.0
		if (license.getExtra() == null)
		{
			bean.setMaxUsers(license.getMaxUsers());
		}
		else if (license.getExtra() instanceof Integer)
		{
			bean.setMaxUsers((Integer)(license.getExtra()));
		}

		bean.setNotBefore(new FormatedDate(license.getNotBefore()).getFormatedDate());
		bean.setNoSupportAfter(new FormatedDate(license.getNoSupportAfter()).getFormatedDate());
		bean.setNotAfter(new FormatedDate(license.getNotAfter()).getFormatedDate());
		bean.setVersion(GreenPepperServer.VERSION);
		bean.setHolderName(license.getHolderName());
		return bean;
	}

	public boolean isCommercialLicense()
	{
		return LicenseType.COMMERCIAL.equals(license.getLicenseType());
	}

	private File getLicenseFile() throws Exception
	{
		SystemInfo systemInfo = systDao.getSystemInfo();
		File licenseFile = buildLicenceFile(systemInfo.getLicense());
		return licenseFile != null ? licenseFile : getDefaultLicenseFile();
	}

	private File getDefaultLicenseFile() throws Exception
	{
		URL url = Authorizer.class.getResource("greenpepper.lic");
		if(url == null) throw new GreenPepperLicenceException(LicenseErrorKey.LIC_NOT_FOUND, "");
		return new File(URIUtil.decoded(url.getPath()));
	}

	private File buildLicenceFile(String licenseAsString) throws Exception
	{
		FileOutputStream os = null;

		try
		{
			if(!StringUtil.isBlank(licenseAsString))
			{
				File licenseFile = File.createTempFile("greenpepper", "lic");
				os = new FileOutputStream(licenseFile);
				os.write(Base64.decodeBase64(licenseAsString.getBytes()));
				return licenseFile;
			}
		}
		finally
		{
			IOUtil.closeQuietly(os);
		}

		return null;
	}

	private void registerLicense(File licenseFile)
	{
		sessionService.startSession();
		sessionService.beginTransaction();

		try
		{
			SystemInfo systemInfo = systDao.getSystemInfo();
			systemInfo.setLicense(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(licenseFile))));
			sessionService.commitTransaction();
		}
		catch (Exception ex)
		{
			sessionService.rollbackTransaction();
			log.warn(ex.getMessage());
			log.debug("Register license", ex);
		}
		finally
		{
			sessionService.closeSession();
			IOUtil.deleteFile(licenseFile);
		}
	}

	private LicenseParam getLicenseParam()
	{
		final KeyStoreParam publicKeyStoreParam = new KeyStoreParam()
		{
			public InputStream getStream() throws IOException
			{
				final String resourceName = properties.getProperty("licence.keystore");
				final InputStream in = getClass().getResourceAsStream(resourceName);
				if (in == null) throw new FileNotFoundException(resourceName);
				return in;
			}

			public String getAlias()
			{
				return properties.getProperty("licence.key.alias");
			}

			public String getStorePwd()
			{
				return properties.getProperty("licence.keystore.pwd");
			}

			public String getKeyPwd() { return null; }
		};


		final CipherParam cipherParam = new CipherParam()
		{
			public String getKeyPwd()
			{
				return properties.getProperty("licence.cipher.key");
			}
		};

		final LicenseParam licenceParam = new LicenseParam()
		{
			public String getSubject()
			{
				return properties.getProperty("licence.subject");
			}

			@SuppressWarnings("static-access")
			public java.util.prefs.Preferences getPreferences()
			{
				return Preferences.instance().userNodeForPackage(Authorizer.class);
			}

			public KeyStoreParam getKeyStoreParam()
			{
				return publicKeyStoreParam;
			}

			public CipherParam getCipherParam()
			{
				return cipherParam;
			}
		};

		return licenceParam;
	}
}
