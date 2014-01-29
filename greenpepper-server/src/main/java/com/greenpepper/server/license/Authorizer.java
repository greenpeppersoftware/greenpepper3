package com.greenpepper.server.license;

import java.util.Date;

import com.greenpepper.server.domain.Repository;

public interface Authorizer {

	void initialize(Date versionDate)
			throws Exception;

	/**
	 * ReInitializes the Authorizer with the new persisted license. </p>
	 *
	 * @param newLicence
	 * @throws Exception
	 */
	void reInitialize(String newLicence)
			throws Exception;

	/**
	 * Verifies that the license supports the repository has the rgiht permission. </p>
	 *
	 * @param repository
	 * @param permission
	 * @throws GreenPepperLicenceException
	 */
	void verify(Repository repository, Permission permission)
			throws GreenPepperLicenceException;

	LicenseBean getLicenseBean();

	boolean isCommercialLicense();
}