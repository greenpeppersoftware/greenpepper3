package com.greenpepper.server.license;

import com.greenpepper.server.GreenPepperServerException;

@SuppressWarnings("serial")
public class GreenPepperLicenceException extends GreenPepperServerException
{

    public GreenPepperLicenceException()
    {
        super();
    }

    public GreenPepperLicenceException(Throwable th)
    {
        super(th);
    }

    public GreenPepperLicenceException(String id, String msg)
    {
        super(id, msg);
    }

    public GreenPepperLicenceException(String id, String msg, Throwable th)
    {
        super(id, msg, th);
    }
}