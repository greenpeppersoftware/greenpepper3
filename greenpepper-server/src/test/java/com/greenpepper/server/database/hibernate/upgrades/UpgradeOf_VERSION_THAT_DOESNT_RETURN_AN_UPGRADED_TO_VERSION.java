package com.greenpepper.server.database.hibernate.upgrades;

import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;
import com.greenpepper.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class UpgradeOf_VERSION_THAT_DOESNT_RETURN_AN_UPGRADED_TO_VERSION implements ServerVersionUpgrader
{

	public String upgradedTo()
	{
		return null;
	}

	public void upgrade(SessionService service) throws Exception
	{
	    SystemInfoDao systemDao = new HibernateSystemInfoDao(service);
	    SystemInfo systemInfo = systemDao.getSystemInfo();
	    systemInfo.setLicense("A");
	}
}