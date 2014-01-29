package com.greenpepper.server.domain;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractUniqueEntity extends AbstractVersionedEntity
{
    protected String uuid;
    
    public AbstractUniqueEntity()
    {
        uuid = UUID.randomUUID().toString();
    }
    
    @Basic
    @Column(name="UUID", nullable = false)
    public String getUUID()
    {
        return this.uuid;
    }
    
    public void setUUID(String uuid)
    {
        this.uuid = uuid;
    }
    
    public boolean equals(Object o)
    {
        return getUUID().equals(((AbstractUniqueEntity)o).getUUID());
    }
    
    public int hashCode()
    {
        return getUUID() == null ? 0 : getUUID().hashCode();
    }
}