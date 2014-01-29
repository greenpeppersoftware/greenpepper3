package com.greenpepper.server.database.hibernate.upgrades;

import com.greenpepper.server.database.SessionService;

public interface ServerVersionUpgrader 
{
	public void upgrade(SessionService service) throws Exception;

	public String upgradedTo();
}
