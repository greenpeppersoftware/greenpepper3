package com.greenpepper.server.license;

public interface LicenseErrorKey
{
    /**
     * License group error.
     */
    String LICENSE = "greenpepper.server.license";
    
    /**
     * License expired.
     */
    String EXPIRED = LICENSE + ".expired";

    /**
     * Max users exceeded.
     */
    String MAX_USER_EXCEEDS = LICENSE + ".maxusers";

    /**
     * Updates not allowed.
     */
    String UPDATES_NOT_SUPPORTED = LICENSE + ".updatesnotsupported";

    /**
     * Licence not found.
     */
    String LIC_NOT_FOUND = LICENSE + ".notfound";

    /**
     * Licence not updated.
     */
    String FAILED_TO_UPDATE_LIC = LICENSE + ".updatefailed";

    /**
     * Trial locked.
     */
    String TRIAL_LOCKED =  LICENSE + ".triallocked";
}
