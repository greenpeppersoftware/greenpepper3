package com.greenpepper.server.database.hibernate;

import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.database.hibernate.upgrades.NoServerUpgrades;
import com.greenpepper.server.database.hibernate.upgrades.ServerVersionUpgrader;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class ServerUpgrader 
{
	private final SessionService sessionService;
    private final SystemInfoDao systDao;
    private final SystemInfo systemInfo;
    
	public ServerUpgrader(SessionService sessionService)
	{
		this(sessionService, new HibernateSystemInfoDao(sessionService));
	}

	public ServerUpgrader(SessionService sessionService, SystemInfoDao systemInfoDao) {
		this.sessionService = sessionService;
		this.systDao = systemInfoDao;
		this.systemInfo = systDao.getSystemInfo();
	}
	
	public void upgradeTo(String version) throws Exception
    {
    	String installedVersion = systemInfo.getGpVersion();
    	if(!installedVersion.equals(version))
    	{
    		ServerVersionUpgrader upgrader = versionUpgrader(installedVersion, version);
    		upgrader.upgrade(sessionService);
    		
    		String newInstalledVersion = upgrader.upgradedTo() != null ? upgrader.upgradedTo() : version;
    		systemInfo.setGpVersion(newInstalledVersion);
			upgradeTo(version);
    	}
    	
		systDao.store(systemInfo);
    }
	
	private ServerVersionUpgrader versionUpgrader(String fromVersion, String toVersion) throws Exception
	{
		fromVersion = fromVersion.replaceAll( "\\.", "_" );
		fromVersion = fromVersion.replaceAll( "\\-", "_" );
    	String className = "com.greenpepper.server.database.hibernate.upgrades.UpgradeOf_" + fromVersion;
		try 
		{
			return (ServerVersionUpgrader)Class.forName( className ).newInstance();
		} 
		catch (ClassNotFoundException e) 
		{
			return new NoServerUpgrades(toVersion);
		}
	}
}
