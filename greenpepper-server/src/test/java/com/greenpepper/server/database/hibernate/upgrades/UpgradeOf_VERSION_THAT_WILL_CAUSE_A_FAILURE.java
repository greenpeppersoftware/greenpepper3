package com.greenpepper.server.database.hibernate.upgrades;

import com.greenpepper.server.database.SessionService;

public class UpgradeOf_VERSION_THAT_WILL_CAUSE_A_FAILURE implements ServerVersionUpgrader
{

	public String upgradedTo()
	{
		return "VERSION.THAT.NEEDS.MORE.UPGRADES";
	}

	public void upgrade(SessionService service) throws Exception
	{
	    throw new Exception("FAILURE");
	}
}