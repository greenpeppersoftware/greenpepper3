package com.greenpepper.util.cmdline;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.greenpepper.util.StringUtil;

/**
 * Basic stream manager for a process.
 * <p/>
 * @author JCHUET
 */
public class StreamGobblerImpl implements StreamGobbler
{
    private String input = "";
    private OutputStream stdin;
    private InputStream stdout;
    private InputStream stderr;
    private StringBuffer outBuffer = new StringBuffer();
    private StringBuffer errBuffer = new StringBuffer();
    private List<Exception> exceptions = new ArrayList<Exception>();
    
    public StreamGobblerImpl(Process process)
    {
        stdin = process.getOutputStream();
        stdout = process.getInputStream();
        stderr = process.getErrorStream();
    }

    public void run()
    {
        new Thread(new OuputReadingRunnable(stdout, outBuffer), "Process standard out").start();
        new Thread(new OuputReadingRunnable(stderr, errBuffer), "Process error").start();
        sendInput();
    }

    public String getOutput()
    {
        return outBuffer.toString();
    }

    public String getError()
    {
        return errBuffer.toString();
    }

    public boolean hasErrors()
    {
        return !StringUtil.isEmpty(errBuffer.toString());
    }

    public List getExceptions()
    {
        return exceptions;
    }

    public boolean hasExceptions()
    {
        return exceptions.size() > 0;
    }

    public void exceptionCaught(Exception e)
    {
        exceptions.add(e);
    }

    protected void sendInput()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    stdin.write(input.getBytes("UTF-8"));
                    stdin.flush();
                    stdin.close();
                }
                catch (Exception e)
                {
                    exceptionCaught(e);
                }
            }
        };

        try
        {
            thread.start();
            thread.join();
        }
        catch (Exception e)
        {
            exceptionCaught(e);
        }
    }

    private void readOutput(InputStream input, StringBuffer buffer)
    {
        try
        {
            int c;
            while ((c = input.read()) != -1)
            {
                buffer.append((char) c);
            }
        }
        catch (Exception e)
        {
            exceptionCaught(e);
        }
    }

    private class OuputReadingRunnable implements Runnable
    {
        public InputStream input;
        public StringBuffer buffer;
        public OuputReadingRunnable(InputStream input, StringBuffer buffer)
        {
            this.input = input;
            this.buffer = buffer;
        }

        public void run()
        {
            readOutput(input, buffer);
        }
    }
}