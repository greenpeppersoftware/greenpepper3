package com.greenpepper.util.cmdline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenpepper.util.StringUtil;

public class CommandLineExecutor
{
    private static Logger log = LoggerFactory.getLogger(CommandLineExecutor.class);
    private static final int SUCCESS = 0;
	private StreamGobbler gobbler;
    private String[] cmdLine;
    
    public CommandLineExecutor(String[] cmdLine)
    {
        this.cmdLine = cmdLine;
    }
    
    public void executeAndWait() throws Exception
    {
        Process p = launchProcess();
        checkForErrors(p);

		if (log.isDebugEnabled())
		{
			// GP-551 : Keep trace of outputs
			if (!StringUtil.isEmpty(getOutput()))
			{
				log.debug("System Output during execution : \n" + getOutput());
			}
			
			if (gobbler.hasErrors())
			{
				log.debug("System Error Output during execution : \n" + gobbler.getError());
			}
		}
    }

    private Process launchProcess() throws Exception
    {
		if (log.isDebugEnabled())
		{
        	log.debug("Launching cmd: " + getCmdLineToString());
		}

        Process p = Runtime.getRuntime().exec(cmdLine);
        gobbler = new StreamGobblerImpl(p);
        Thread reader = new Thread(gobbler);
        reader.start();
        p.waitFor();
        return p;
    }

    public String getOutput()
    {
        return gobbler.getOutput();
    }

    private void checkForErrors(Process p) throws Exception
    {
    	if(p.exitValue() != SUCCESS)
    	{
			if(gobbler.hasErrors())
			{
				throw new Exception(gobbler.getError());
			}
			
			throw new Exception("Process was terminated abnormally");
    	}
    }
    
    private String getCmdLineToString()
    {
        StringBuilder sb = new StringBuilder();
        for(String cmd : cmdLine)
        {
            sb.append(cmd).append(" ");
        }
        
        return sb.toString();
    }
}
