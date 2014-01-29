package com.greenpepper.server.database.hibernate;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenpepper.runner.Main;
import com.greenpepper.server.GreenPepperServer;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.ClasspathSet;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.dao.SystemUnderTestDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemUnderTestDao;
import com.greenpepper.util.URIUtil;

public class DefaultRunners {

	private static Logger log = LoggerFactory.getLogger(DefaultRunners.class);

	private final SystemUnderTestDao sutDao;
	private final Properties properties;

	private String version;
	private String jarFile;

	public DefaultRunners(SessionService sessionService, Properties properties) {
		this(new HibernateSystemUnderTestDao(sessionService), properties);
	}

	public DefaultRunners(SystemUnderTestDao systemUnderTestDao, Properties properties) {
		this.sutDao = systemUnderTestDao;
		this.properties = properties;
	}

	public void insert()
			throws Exception {
		insertJavaRunner();
		insertDotNetRunner();
	}

	private void insertJavaRunner() {
		String greenPepperHome = properties.getProperty("greenpepper.home");
		if (greenPepperHome != null) {
			File greenPepperHomeDir = new File(greenPepperHome);
			insertJavaRunnerFromHome(greenPepperHomeDir);
		}

		String basePath = properties.getProperty("baseUrl", null);
		if (basePath != null) {
			File libDir = new File(basePath, "WEB-INF/lib");
			insertJavaRunnerFromDir(libDir);
		}

		String confluenceHome = properties.getProperty("confluence.home", null);
		if (confluenceHome != null) {
			File pluginCacheDir = new File(confluenceHome, "plugin-cache");
			insertJavaRunnerFromDir(pluginCacheDir);
		}
	}

	private boolean shouldCreateJavaRunner() {
		return version != null && sutDao.getRunnerByName("GPCore JAVA v. " + version) == null;
	}

	private void insertJavaRunnerFromDir(File dir) {
		try {
			detect(dir, "^greenpepper\\-(confluence|xwiki)4??\\-plugin\\-(.+)\\-complete\\.jar$");

			if (shouldCreateJavaRunner()) {
				createJavaRunner(getJavaRunnerClassPathsFromDir(dir), jarFile);
			}
		}
		catch (Exception e) {
			log.warn("Runner registration failed: " + e.getMessage());
		}
	}

	private ClasspathSet getJavaRunnerClassPathsFromDir(File directory)
			throws IOException {

		ClasspathSet paths = new ClasspathSet();
		paths.add(String.format("%s/%s", normalize(directory), jarFile));
		return paths;
	}

	private void detect(File srcDir, String regExLib)
			throws Exception {
		version = null;
		jarFile = null;

		Pattern pattern = Pattern.compile(regExLib);

		String[] files = srcDir.list();
		if (files != null) {

			for (String file : files) {
				Matcher matcher = pattern.matcher(file);

				if (matcher.find()) {
					int groupCount = matcher.groupCount();
					version = matcher.group(groupCount); //always use the last group
					jarFile = file;
					break;
				}
			}
		}
	}

	private void createJavaRunner(ClasspathSet classpaths, String hint)
			throws IOException, GreenPepperServerException {

		log.info(String.format("Registrating Runner: GPCore JAVA v. %s (%s)", version, hint));
		Runner runner = Runner.newInstance("GPCore JAVA v. " + version);
		runner.setCmdLineTemplate("java -mx252m -cp ${classpaths} ${mainClass} ${inputPath} ${outputPath} " +
								  "-l ${locale} -r ${repository} -f ${fixtureFactory} --xml");
		runner.setMainClass(Main.class.getName());
		runner.setClasspaths(classpaths);
		runner.setEnvironmentType(sutDao.getEnvironmentTypeByName("JAVA"));
		sutDao.create(runner);
	}

	private void insertJavaRunnerFromHome(File homeDir) {

		try {
			File runnerDir = new File(homeDir, "java/runner");
			detect(runnerDir, "^greenpepper\\-core\\-(.+)\\.jar$");
			
			if (shouldCreateJavaRunner()) {
				ClasspathSet classpaths = new ClasspathSet();

				File coreFile = new File(runnerDir, String.format("greenpepper-core-%s.jar", version));
				File extFile = new File(runnerDir, String.format("greenpepper-extensions-java-%s.jar", version));
				File codecFile = new File(runnerDir, "commons-codec-1.3.jar");
				File xmlrpcFile = new File(runnerDir, "xmlrpc-2.0.1.jar");

				if (extFile.exists() && codecFile.exists() && xmlrpcFile.exists()) {
					classpaths.add(normalize(coreFile));
				    classpaths.add(normalize(extFile));
					classpaths.add(normalize(codecFile));
					classpaths.add(normalize(xmlrpcFile));
					createJavaRunner(classpaths, normalize(runnerDir));
				}
			}
		}
		catch (Exception e) {
			log.warn("Runner registration failed: " + e.getMessage());
		}
	}

	private void insertDotNetRunner() {
		String greenPepperHome = properties.getProperty("greenpepper.home");
		if (greenPepperHome != null) {
			File greenPepperHomeDir = new File(greenPepperHome);
			insertDotNetRunnerFromHome(greenPepperHomeDir);
		}
	}

	private void insertDotNetRunnerFromHome(File homeDir) {

		try {
			File runnerDir = new File(homeDir, "dotnet/runner");

			if (shouldCreateDotNetRunner()) {
				ClasspathSet classpaths = new ClasspathSet();

				File coreFile = new File(runnerDir, "GreenPepper.Core.dll");
				File extFile = new File(runnerDir, "GreenPepper.Extensions.dll");
				File xmlrpcFile = new File(runnerDir, "CookComputing.XmlRpc.dll");

				if (coreFile.exists() && extFile.exists() && xmlrpcFile.exists()) {
					classpaths.add(normalize(coreFile));
					classpaths.add(normalize(extFile));
					classpaths.add(normalize(xmlrpcFile));
					createDotNetRunner(classpaths, normalize(runnerDir));
				}
			}
		}
		catch (Exception e) {
			log.warn("Runner registration failed: " + e.getMessage());
		}
	}

	private boolean shouldCreateDotNetRunner() {
		return version != null && sutDao.getRunnerByName("GPCore .NET v. " + GreenPepperServer.VERSION) == null;
	}

	private void createDotNetRunner(ClasspathSet classpaths, String hint)
			throws IOException, GreenPepperServerException {

		log.info(String.format("Registrating Runner: GPCore .NET v. %s (%s)", GreenPepperServer.VERSION, hint));
		Runner runner = Runner.newInstance("GPCore .NET v. " + GreenPepperServer.VERSION);
		runner.setCmdLineTemplate(String.format("%s/GreenPepper.exe ${inputPath} ${outputPath} -a ${classpaths} " +
												"-r ${repository} -f ${fixtureFactory} --xml", hint));
		runner.setClasspaths(classpaths);
		runner.setEnvironmentType(sutDao.getEnvironmentTypeByName(".NET"));
		sutDao.create(runner);
	}

	private String normalize(File file)
			throws IOException {
		return URIUtil.decoded(file.getCanonicalPath());
	}
}