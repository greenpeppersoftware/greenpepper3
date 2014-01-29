package com.greenpepper.server.domain.dao;

import java.util.List;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;

public interface SystemUnderTestDao
{
    /**
     * Retrieves the EnvironmentType for the specified name.
     * </p>
     * @param name of the EnvironmentType
     * @return the EnvironmentType for the specified name.
     */
	public EnvironmentType getEnvironmentTypeByName(String name);
	
	/**
     * Retrieves all the Environment Types available.
     * </p>
	 * @return all the Environment Types available.
	 */
	public List<EnvironmentType> getAllEnvironmentTypes();
	
	/**
     * Creates the EnvironmentType
     * </p>
	 * @param environmentType
     * @return the new environmentType.
	 */
	public EnvironmentType create(EnvironmentType environmentType);
	
    /**
     * Retrieves the Runner for the specified name.
     * </p>
     * @param name of the runner
     * @return the Runner for the specified name.
     */
    public Runner getRunnerByName(String name);
    
    /**
     * Retrieves All available runners.
     * </p>
     * @return All available runners.
     */
    public List<Runner> getAllRunners();
    
    /**
     * Creates the Runner
     * </p>
     * @param runner
     * @return the new runner.
     * @throws GreenPepperServerException
     */
    public Runner create(Runner runner) throws GreenPepperServerException;
    
    /**
     * Updates the runner.
     * </p>
     * @param oldRunnerName
     * @param runner
     * @return the updated runner.
     * @throws GreenPepperServerException
     */
    public Runner update(String oldRunnerName, Runner runner) throws GreenPepperServerException;
    
    /**
     * Removes the runner.
     * </p>
     * @param runnerName
     * @throws GreenPepperServerException
     */
    public void removeRunner(String runnerName) throws GreenPepperServerException;
    
    /**
     * Retrieves the SystemUnderTest for the specified name.
     * </p>
     * @param name of the project
     * @param name of the SUT
     * @return the SystemUnderTest for the specified name.
     */
    public SystemUnderTest getByName(String projectName, String sutName);
    
    /**
     * Retrieves all the SystemUnderTest for the registered Project.
     * </p>
     * @param projectName
     * @return all the SystemUnderTest for the registered Project.
     */
    public List<SystemUnderTest> getAllForProject(String projectName);
    
    /**
     * Retrieves all the SystemUnderTest for the registered Runner.
     * </p>
     * @param runnerName
     * @return all the SystemUnderTest for the registered Runner.
     */
    public List<SystemUnderTest> getAllForRunner(String runnerName);
    
    /**
     * Saves the specified SystemUnderTest.
     * </p>
     * @param newSystemUnderTest
     * @return the new SystemUnderTest.
     * @throws GreenPepperServerException
     */
    public SystemUnderTest create(SystemUnderTest newSystemUnderTest) throws GreenPepperServerException;
    
    /**
     * Updates the specified SystemUnderTest.
     * </p>
     * @param oldSutName
     * @param updatedSystemUnderTest
     * @return the updated SystemUnderTest.
     * @throws GreenPepperServerException
     */
    public SystemUnderTest update(String oldSutName, SystemUnderTest updatedSystemUnderTest) throws GreenPepperServerException;
    
    /**
     * Deletes the specified SystemUnderTest.
     * </p>
     * @param projectName.
     * @param sutName.
     * @throws GreenPepperServerException
     */
    public void remove(String projectName, String sutName) throws GreenPepperServerException;
    
    /**
     * Set the specified SystemUnderTest as the new project default.
     * </p>
     * @param systemUnderTest.
     * @throws GreenPepperServerException
     */
    public void setAsDefault(SystemUnderTest systemUnderTest) throws GreenPepperServerException;
    
    /**
     * Retrieves all references that depends on the SystemUnderTest
     * </p>
     * @param sut
     * @return all references that depends on the SystemUnderTest
     */
    public List<Reference> getAllReferences(SystemUnderTest sut);
    
    /**
     * Retrieves all specifications that depends on the SystemUnderTest
     * </p>
     * @param sut
     * @return all specifications that depends on the SystemUnderTest
     */
    public List<Specification> getAllSpecifications(SystemUnderTest sut);
}