package com.greenpepper.server.database.hibernate.hsqldb;


public abstract class AbstractHibernateMemoryTest extends HibernateMemoryTestCase
{
    private String dataFile;

    protected void setUp() throws Exception
    {
        super.setUp();
        startSession();
    }

    protected void tearDown() throws Exception
    {
        rollbackIfNecessary();
        closeSession();
        super.tearDown();
    }

    private void rollbackIfNecessary()
    {
        if (transaction == null) return;
        if (!transaction.wasCommitted()) transaction.rollback();
    }

    public String getDataFile()
    {
        return dataFile;
    }

    public void setDataFile(String dataFile)
    {
        this.dataFile = dataFile;
    }
}
