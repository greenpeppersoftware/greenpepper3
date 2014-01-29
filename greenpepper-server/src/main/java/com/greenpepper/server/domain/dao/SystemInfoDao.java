package com.greenpepper.server.domain.dao;

import com.greenpepper.server.domain.SystemInfo;

public interface SystemInfoDao
{
    /**
     * @return The SystemInfo
     */
    public SystemInfo getSystemInfo();
    
    /**
     * Stores the SystemInfo.
     * </p>
     * @param systemInfo
     */
    public void store(SystemInfo systemInfo);
}
