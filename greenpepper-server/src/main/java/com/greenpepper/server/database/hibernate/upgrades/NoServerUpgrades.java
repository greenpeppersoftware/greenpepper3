package com.greenpepper.server.database.hibernate.upgrades;

import com.greenpepper.server.database.SessionService;

public class NoServerUpgrades implements ServerVersionUpgrader
{
	private String currentVersion;
	
	public NoServerUpgrades(String currentVersion)
	{
		this.currentVersion = currentVersion;
	}

	public String upgradedTo() 
	{
		return currentVersion;
	}

	public void upgrade(SessionService service) 
	{
		// NO UPGRADES TO PERFORM
	}
}
