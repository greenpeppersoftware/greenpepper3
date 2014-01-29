package com.greenpepper.server.domain.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;

import com.greenpepper.server.database.SessionService;
import com.greenpepper.server.domain.SystemInfo;
import com.greenpepper.server.domain.dao.SystemInfoDao;

public class HibernateSystemInfoDao implements SystemInfoDao
{
    private static final long SYSTEM_INFO = 1l;
    private SessionService sessionService;
    
    public HibernateSystemInfoDao(SessionService sessionService)
    {
        this.sessionService = sessionService;
    }
    
    public SystemInfo getSystemInfo()
    {
        final Criteria crit = sessionService.getSession().createCriteria(SystemInfo.class);
        crit.add(Property.forName("id").eq(SYSTEM_INFO));
        SystemInfo systemInfo = (SystemInfo) crit.uniqueResult();
//        crit.addOrder(Order.desc("id"));
//        List list = crit.list();
//        SystemInfo systemInfo = null;
//        if (list.size() > 0) {
//			systemInfo = (SystemInfo) list.get(0);
//        }
        HibernateLazyInitializer.init(systemInfo);
		return systemInfo;
	}

    public void store(SystemInfo systemInfo)
    {
        sessionService.getSession().saveOrUpdate(systemInfo);
    }
}
