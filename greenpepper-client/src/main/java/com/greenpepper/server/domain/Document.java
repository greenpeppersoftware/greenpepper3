package com.greenpepper.server.domain;

import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.DOCUMENT_NAME_IDX;
import static com.greenpepper.server.rpc.xmlrpc.XmlRpcDataMarshaller.DOCUMENT_REPOSITORY_IDX;

import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.greenpepper.server.GreenPepperServerException;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Document extends AbstractUniqueEntity implements Comparable
{
    private String name;
    private String resolvedName;
    private Repository repository;

    @Basic
    @Column(name = "NAME", nullable = false, length=255)
    public String getName()
    {
        return this.name;
    }
    
    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="REPOSITORY_ID")
    public Repository getRepository()
    {
        return this.repository;
    }

    /**
     * For UI purposes
     * @param resolvedName
     */
    public void setResolvedName(String resolvedName)
    {
        this.resolvedName = resolvedName;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }

//    @Transient
//    public String getResolvedUri() throws GreenPepperServerException
//    {
//        return repository.resolveUri(this);
//    }

    @Transient
    public String getResolvedName() throws GreenPepperServerException
    {
        if(resolvedName != null){return resolvedName;}
        return repository.resolveName(this);
    }

    public Vector<Object> marshallize()
    {
        Vector<Object> parameters = new Vector<Object>();
        parameters.add(DOCUMENT_NAME_IDX, name);
        parameters.add(DOCUMENT_REPOSITORY_IDX, repository.marshallize());
        return parameters;
    }
    
    public boolean equalsTo(Object o)
    {
        if (o == null || !(o instanceof Document))
        {
            return false;
        }
        
        Document docCompared = (Document)o;
        if(getName() == null || !getName().equals(docCompared.getName())) return false;
        if(getRepository() == null || !getRepository().equals(docCompared.getRepository())) return false;
        
        return true;
    }
    
    public int compareTo(Object o)
    {
        return name.compareTo(((Document)o).name);
    }
    
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof Document))
        {
            return false;
        }

        return super.equals(o);
    }
}
