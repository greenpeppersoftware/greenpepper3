package com.greenpepper.server.domain.dao;

import java.util.List;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.RepositoryType;
import com.greenpepper.server.domain.component.ContentType;

public interface RepositoryDao
{
    /**
     * Retrieves the Repository.
     * If none found an GreenPepperServerException is thrown.
     * </p>
     * @param repositoryUid
     * @return the Repository.
     */
    public Repository getByUID(String repositoryUID);

    /**
     * Retrieves the Repository.
     * If none found an GreenPepperServerException is thrown.
     * </p>
     * @param projectName
     * @param repositoryName
     * @return the Repository.
     */
    public Repository getByName(String projectName, String repositoryName);

    /**
     * Retrieves all the registered Repositories.
     * </p>
     * @return the repositories
     */
    public List<Repository> getAll();

    /**
     * Retrieves all the registered Repositories for a project.
     * </p>
     * @param projectName
     * @return all the registered Repositories for a project.
     */
    public List<Repository> getAll(String projectName);

    /**
     * Retrieves all the registered Test Repositories for  project.
     * </p>
     * @param projectName
     * @return the Tests repositories
     */
    public List<Repository> getAllTestRepositories(String projectName);

    /**
     * Retrieves all the registered Requirement Repositories for a project.
     * </p>
     * @param projectName
     * @return the Requirements repositories
     */

    public List<Repository> getAllRequirementRepositories(String projectName);


    /**
     * Retrieve all the repository of a certain type.
     *
     * @param contentType
     * @return
     */
    public List<Repository> getAllRepositories(ContentType contentType);

    /**
     * Retrieves the repository type by name.
     * </p>
     * @param repositoryTypeName
     * @return the repository type.
     */
    public RepositoryType getTypeByName(String repositoryTypeName);

    /**
     * Creates a new Repository.
     * </p>
     * @param newRepository
     * @return the created repository
     * @throws GreenPepperServerException
     */
    public Repository create(Repository newRepository) throws GreenPepperServerException;

    /**
     * Updates the Repository.
     * </p>
     * @param repository
     * @throws GreenPepperServerException
     */
    public void update(Repository repository) throws GreenPepperServerException;
    
    /**
     * Removes the repository if this one doesnt hold any specifications
     * @param repositoryUid
     * @throws GreenPepperServerException
     */
    public void remove(String repositoryUid) throws GreenPepperServerException;

    /**
     * Retrieves all available RepositoryTypes.
     * <p> 
     * @return
     */
    public List<RepositoryType> getAllTypes();
    
    /**
     * Create a new Repository Type
     * </p>
     * @param Repository type
     * @return the Requirement type created
     */
    public RepositoryType create(RepositoryType repositoryType);

}
