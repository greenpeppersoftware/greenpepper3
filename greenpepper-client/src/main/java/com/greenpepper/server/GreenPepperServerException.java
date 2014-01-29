package com.greenpepper.server;

public class GreenPepperServerException extends Exception
{
    private static final long serialVersionUID = 1L;
    private String id = "";

    public GreenPepperServerException()
    {
        super();
    }

    public GreenPepperServerException(Throwable th)
    {
        super(th);
    }

    public GreenPepperServerException(String id, String msg)
    {
        super(msg);
        this.id = id;
    }

    public GreenPepperServerException(String id, String msg, Throwable th)
    {
        super(msg, th);
        this.id = id;
    }

	public GreenPepperServerException(String id, Throwable th)
	{
		super(th);
		this.id = id;
	}

	public String getId()
    {
        return this.id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
}
