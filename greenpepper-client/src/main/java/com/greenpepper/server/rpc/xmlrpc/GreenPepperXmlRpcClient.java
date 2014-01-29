package com.greenpepper.server.rpc.xmlrpc;

import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.ServerPropertiesManager;
import com.greenpepper.server.domain.DocumentNode;
import com.greenpepper.server.domain.EnvironmentType;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.RequirementSummary;
import com.greenpepper.server.domain.Runner;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.license.LicenseBean;
import com.greenpepper.server.rpc.RpcClientService;
import com.greenpepper.server.rpc.xmlrpc.client.XmlRpcClientExecutor;
import com.greenpepper.server.rpc.xmlrpc.client.XmlRpcClientExecutorFactory;
import com.greenpepper.util.CollectionUtil;
import com.greenpepper.util.StringUtil;

public class GreenPepperXmlRpcClient implements RpcClientService
{
    private static Logger log = LoggerFactory.getLogger(GreenPepperXmlRpcClient.class);

    public static final String XML_RPC = "rpc/xmlrpc";
    public static final String HANDLER_SEPARTOR = ".";
    public static final String PATH_SEPARTOR = "/";
    public static final String PORT_SEPARTOR = ":";

    private ServerPropertiesManager propertiesManager;

    public GreenPepperXmlRpcClient(ServerPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public LicenseBean license(String identifier) throws GreenPepperServerException
    {
        log.debug("Retrieving license info");
        Vector<Object> licenseParams = (Vector<Object>)execute(XmlRpcMethodName.license, identifier);

        return XmlRpcDataMarshaller.toLicense(licenseParams);
    }

    /**
     * @inheritDoc
     */
    public void uploadLicense(String newLicence, String identifier) throws GreenPepperServerException
    {
        log.debug("Uploading new license" + newLicence);
        execute(XmlRpcMethodName.uploadNewLicense, CollectionUtil.toVector(newLicence), identifier);
    }

    /**
     * @inheritDoc
     */
    public boolean testConnection(String hostName, String handler) throws GreenPepperServerException
    {
        try
        {
            log.debug("PINGING : HostName => " + hostName + "  &  Handler => " + handler);
            XmlRpcClientExecutor xmlrpc = XmlRpcClientExecutorFactory.newExecutor(getXmlRpcUrl(hostName, XML_RPC));
            String cmdLine = new StringBuffer(handler).append(HANDLER_SEPARTOR).append(XmlRpcMethodName.testConnection).toString();
            XmlRpcDataMarshaller.checkForErrors(xmlrpc.execute(cmdLine, new Vector()));

            return true;
        }
//        catch (FileNotFoundException e)
//        {
//            log.error(e.getMessage());
//            throw new GreenPepperServerException(GreenPepperServerErrorKey.XML_RPC_URL_NOTFOUND, e.getMessage());
//        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            throw new GreenPepperServerException(GreenPepperServerErrorKey.XML_RPC_HANDLER_NOTFOUND, e.getMessage());
        }
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public boolean ping(Repository repository, String identifier) throws GreenPepperServerException
    {
        String hostName = propertiesManager.getProperty(ServerPropertiesManager.URL, identifier);
        log.debug("PINGING : HostName => " + hostName + "  &  Handler => " + getLocalHandler(identifier));
        execute(XmlRpcMethodName.ping, CollectionUtil.toVector(repository.marshallize()), identifier);

        return true;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<EnvironmentType> getAllEnvironmentTypes(String identifier) throws GreenPepperServerException
    {
        log.debug("Retreiving all Environment Types");
        Vector<Object> envTypesParams = (Vector<Object>)execute(XmlRpcMethodName.getAllEnvironmentTypes, new Vector(), identifier);

		return XmlRpcDataMarshaller.toEnvironmentTypeList(envTypesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Runner getRunner(String name, String identifier) throws GreenPepperServerException
    {
        log.debug("Retreiving all runners");
        Vector<Object> runnerParams = (Vector<Object>)execute(XmlRpcMethodName.getRunner, CollectionUtil.toVector(name), identifier);

        return XmlRpcDataMarshaller.toRunner(runnerParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Runner> getAllRunners(String identifier) throws GreenPepperServerException
    {
        log.debug("Retreiving all runners");
        Vector<Object> runnersParams = (Vector<Object>)execute(XmlRpcMethodName.getAllRunners, new Vector(), identifier);

		return XmlRpcDataMarshaller.toRunnerList(runnersParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void createRunner(Runner runner, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(runner.marshallize());
        
        log.debug("Creating runner: " + runner.getName());
        execute(XmlRpcMethodName.createRunner, params, identifier);
    }
    

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void updateRunner(String oldRunnerName, Runner runner, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(oldRunnerName, runner.marshallize());
        
        log.debug("Updating runner: " + oldRunnerName);
        execute(XmlRpcMethodName.updateRunner, params, identifier);
    }

    /**
     * @inheritDoc
     */
    public void removeRunner(String name, String identifier) throws GreenPepperServerException
    {
        log.debug("Removing runner: " + name);
        execute(XmlRpcMethodName.removeRunner, CollectionUtil.toVector(name), identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Repository getRegisteredRepository(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Retrieving Registered Repository: " + repository.getUid());
        Vector<Object> repositoryParams = (Vector<Object>)execute(XmlRpcMethodName.getRegisteredRepository, params, identifier);

        return XmlRpcDataMarshaller.toRepository(repositoryParams);

    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Repository registerRepository(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Registering Repository: " + repository.getUid());
        Vector<Object> repositoryParams = (Vector<Object>)execute(XmlRpcMethodName.registerRepository, params, identifier);

        return XmlRpcDataMarshaller.toRepository(repositoryParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void updateRepositoryRegistration(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Updating Repository registration: " + repository.getUid());
        execute(XmlRpcMethodName.updateRepositoryRegistration, params, identifier);
    }

    /**
     * @inheritDoc
     */
    public void removeRepository(String repositoryUid, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repositoryUid);

        log.debug("Removing Repository " + repositoryUid);
        execute(XmlRpcMethodName.removeRepository, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Project> getAllProjects(String identifier) throws GreenPepperServerException
    {
        log.debug("Retrieving All Projects");
        Vector<Object> projectsParams = (Vector<Object>)execute(XmlRpcMethodName.getAllProjects, identifier);

		return XmlRpcDataMarshaller.toProjectList(projectsParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Repository> getAllSpecificationRepositories(String identifier) throws GreenPepperServerException
    {
        log.debug("Retrieving all specification repositories.");
        Vector<Object> repositoriesParams = (Vector<Object>)execute(XmlRpcMethodName.getAllSpecificationRepositories, identifier);

		return XmlRpcDataMarshaller.toRepositoryList(repositoriesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize());

        log.debug("Retrieving repositories for Associated project. (SystemUnderTest : " + systemUnderTest.getName() + ")");
        Vector<Object> repositoriesParams = (Vector<Object>)execute(XmlRpcMethodName.getAllRepositoriesForSystemUnderTest, params, identifier);

		return XmlRpcDataMarshaller.toRepositoryList(repositoriesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Repository> getSpecificationRepositoriesOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Retrieving Specification repositories for Associated project. (Repo UID: " + repository.getUid() + ")");
        Vector<Object> repositoriesParams = (Vector<Object>)execute(XmlRpcMethodName.getSpecificationRepositoriesOfAssociatedProject, params, identifier);

		return XmlRpcDataMarshaller.toRepositoryList(repositoriesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Repository> getSpecificationRepositoriesOfAssociatedProject(SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize());

        log.debug("Retrieving Specification repositories for Associated project. (SystemUnderTest : " + systemUnderTest.getName() + ")");
        Vector<Object> repositoriesParams = (Vector<Object>)execute(XmlRpcMethodName.getSpecificationRepositoriesForSystemUnderTest, params, identifier);

		return XmlRpcDataMarshaller.toRepositoryList(repositoriesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Repository> getRequirementRepositoriesOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Retrieving Requirement repositories for Associated project. (Repo UID: " + repository.getUid() + ")");
        Vector<Object> repositoriesParams = (Vector<Object>)execute(XmlRpcMethodName.getRequirementRepositoriesOfAssociatedProject, params, identifier);

		return XmlRpcDataMarshaller.toRepositoryList(repositoriesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<SystemUnderTest> getSystemUnderTestsOfAssociatedProject(Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize());

        log.debug("Retrieving SUT list for Associated repository: " + repository.getName());
        Vector<Object> sutsParams = (Vector<Object>)execute(XmlRpcMethodName.getSystemUnderTestsOfAssociatedProject, params, identifier);

		return XmlRpcDataMarshaller.toSystemUnderTestList(sutsParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<SystemUnderTest> getSystemUnderTestsOfProject(String projectName, String identifier) throws GreenPepperServerException
    {
        log.debug("Retrieving SUT list for Project: " + projectName);
        Vector<Object> sutsParams = (Vector<Object>)execute(XmlRpcMethodName.getSystemUnderTestsOfProject, CollectionUtil.toVector(projectName), identifier);

		return XmlRpcDataMarshaller.toSystemUnderTestList(sutsParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void addSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), specification.marshallize());

        log.debug("Adding SUT " + systemUnderTest.getName() + " to SUT list of specification: " + specification.getName());
        execute(XmlRpcMethodName.addSpecificationSystemUnderTest, params, identifier);
     }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), specification.marshallize());

        log.debug("Adding SUT " + systemUnderTest.getName() + " to SUT list of specification: " + specification.getName());
        execute(XmlRpcMethodName.removeSpecificationSystemUnderTest, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public boolean hasReferences(Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(specification.marshallize());

        log.debug("Does specification " + specification.getName() + " Has References");
        String hasReferences = (String)execute(XmlRpcMethodName.doesSpecificationHasReferences, params, identifier);

        return Boolean.valueOf(hasReferences);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Reference> getReferences(Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(specification.marshallize());

        log.debug("Retrieving Specification " + specification.getName() + " References");
        Vector<Object> referencesParams = (Vector<Object>)execute(XmlRpcMethodName.getSpecificationReferences, params, identifier);

		return XmlRpcDataMarshaller.toReferencesList(referencesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public boolean hasReferences(Requirement requirement, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(requirement.marshallize());

        log.debug("Does Requirement " + requirement.getName() + " Has References");
        String hasReferences = (String)execute(XmlRpcMethodName.doesRequirementHasReferences, params, identifier);

        return Boolean.valueOf(hasReferences);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Set<Reference> getReferences(Requirement requirement, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(requirement.marshallize());

        log.debug("Retrieving Requirement " + requirement.getName() + " References");
        Vector<Object> referencesParams = (Vector<Object>)execute(XmlRpcMethodName.getRequirementReferences, params, identifier);

		return XmlRpcDataMarshaller.toReferencesList(referencesParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Reference getReference(Reference reference, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(reference.marshallize());

        log.debug("Retrieving Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName());
        Vector<Object> referenceParams = (Vector<Object>)execute(XmlRpcMethodName.getReference, params, identifier);

        return XmlRpcDataMarshaller.toReference(referenceParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public SystemUnderTest getSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), repository.marshallize());

        log.debug("Retrieving SystemUnderTest: " + systemUnderTest.getName());
        Vector<Object> sutParams = (Vector<Object>)execute(XmlRpcMethodName.getSystemUnderTest, params, identifier);

        return XmlRpcDataMarshaller.toSystemUnderTest(sutParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void createSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), repository.marshallize());

        log.debug("Creating SystemUnderTest: " + systemUnderTest.getName());
        execute(XmlRpcMethodName.createSystemUnderTest, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void updateSystemUnderTest(String oldSystemUnderTestName, SystemUnderTest newSystemUnderTest, Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(oldSystemUnderTestName, newSystemUnderTest.marshallize(), repository.marshallize());

        log.debug("Updating SystemUnderTest: " + oldSystemUnderTestName);
        execute(XmlRpcMethodName.updateSystemUnderTest, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void removeSystemUnderTest(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), repository.marshallize());

        log.debug("Removing SystemUnderTest: " + systemUnderTest.getName());
        execute(XmlRpcMethodName.removeSystemUnderTest, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void setSystemUnderTestAsDefault(SystemUnderTest systemUnderTest, Repository repository, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), repository.marshallize());

        log.debug("Setting as default the SystemUnderTest: " + systemUnderTest.getName());
        execute(XmlRpcMethodName.setSystemUnderTestAsDefault, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void removeRequirement(Requirement requirement, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(requirement.marshallize());

        log.debug("Removing Requirement: " + requirement.getName());
        execute(XmlRpcMethodName.removeRequirement, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Specification getSpecification(Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(specification.marshallize());

        log.debug("Retrieving Specification: " + specification.getName());
        Vector<Object> specificationParams = (Vector<Object>)execute(XmlRpcMethodName.getSpecification, params, identifier);

        return XmlRpcDataMarshaller.toSpecification(specificationParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Specification createSpecification(Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(specification.marshallize());

        log.debug("Creating Specification: " + specification.getName());
        Vector<Object> specificationParams = (Vector<Object>)execute(XmlRpcMethodName.createSpecification, params, identifier);

        return XmlRpcDataMarshaller.toSpecification(specificationParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void updateSpecification(Specification oldSpecification, Specification newSpecification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(oldSpecification.marshallize(), newSpecification.marshallize());

        log.debug("Updating Specification: " + oldSpecification.getName());
        execute(XmlRpcMethodName.updateSpecification, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void removeSpecification(Specification specification, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(specification.marshallize());

        log.debug("Removing Specification: " + specification.getName());
        execute(XmlRpcMethodName.removeSpecification, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void createReference(Reference reference, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(reference.marshallize());

        log.debug("Creating Test Case: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName());
        execute(XmlRpcMethodName.createReference, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Reference updateReference(Reference oldReference, Reference newReference, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(oldReference.marshallize(), newReference.marshallize());

        log.debug("Updating Reference: " + newReference.getRequirement().getName() + "," + newReference.getSpecification().getName());
        Vector<Object> referenceParams = (Vector<Object>)execute(XmlRpcMethodName.updateReference, params, identifier);

        return XmlRpcDataMarshaller.toReference(referenceParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public void removeReference(Reference reference, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(reference.marshallize());

        log.debug("Removing Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName());
        execute(XmlRpcMethodName.removeReference, params, identifier);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Execution runSpecification(SystemUnderTest systemUnderTest, Specification specification, boolean implementedVersion, String locale, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(systemUnderTest.marshallize(), specification.marshallize(), implementedVersion, locale);

        log.debug("Running Specification: " + specification.getName() + " ON System:" + systemUnderTest.getName());
        Vector<Object> executionParams = (Vector<Object>)execute(XmlRpcMethodName.runSpecification, params, identifier);

        return XmlRpcDataMarshaller.toExecution(executionParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Reference runReference(Reference reference, String locale, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(reference.marshallize(), locale);

        log.debug("Running Reference: " + reference.getRequirement().getName() + "," + reference.getSpecification().getName());
        Vector<Object> referenceParams = (Vector<Object>)execute(XmlRpcMethodName.runReference, params, identifier);

        return XmlRpcDataMarshaller.toReference(referenceParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public RequirementSummary getSummary(Requirement requirement, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(requirement.marshallize());
        Vector<Object> compilParams = (Vector<Object>)execute(XmlRpcMethodName.getRequirementSummary, params, identifier);

        log.debug("Getting Requirement " + requirement.getName() + " summary");

		return XmlRpcDataMarshaller.toRequirementSummary(compilParams);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest, String identifier) throws GreenPepperServerException
    {
        Vector params = CollectionUtil.toVector(repository.marshallize(), systemUnderTest.marshallize());
        
        log.debug("Get Specification Hierarchy: " + repository.getName() + " & " + systemUnderTest.getName());
        Vector<Object> vector = (Vector<Object>)execute(XmlRpcMethodName.getSpecificationHierarchy, params, identifier);

        return XmlRpcDataMarshaller.toDocumentNode(vector);
    }

    /**
     * @inheritDoc
     */
    public ServerPropertiesManager getServerPropertiesManager()
    {
        return propertiesManager;
    }


    /*************************** PRIVATE SECTION ******************************/

    private Object execute(XmlRpcMethodName methodName, String identifier) throws GreenPepperServerException
    {
        return execute(methodName, new Vector(), identifier);
    }

    private Object execute(XmlRpcMethodName methodName, Vector params, String identifier) throws GreenPepperServerException
    {
		String handler = getLocalHandler(identifier);

		XmlRpcClientExecutor xmlrpc = XmlRpcClientExecutorFactory.newExecutor(getLocalXmlRpcUrl(identifier));

		String cmdLine = new StringBuffer(handler).append(HANDLER_SEPARTOR).append(methodName).toString();

		Object response = xmlrpc.execute(cmdLine, params);

        XmlRpcDataMarshaller.checkForErrors(response);

        return response;
    }

	private String getLocalXmlRpcUrl(String identifier) throws GreenPepperServerException
    {
        String hostName = propertiesManager.getProperty(ServerPropertiesManager.URL, identifier);
        return getXmlRpcUrl(hostName, XML_RPC);
    }

    private String getXmlRpcUrl(String hostName, String rpcContext) throws GreenPepperServerException
    {
        if (StringUtil.isEmpty(hostName) || StringUtil.isEmpty(rpcContext))
        {
            throw new GreenPepperServerException(GreenPepperServerErrorKey.NO_CONFIGURATION, "No config");
        }

        StringBuffer buffer = new StringBuffer(hostName);

        if (!hostName.endsWith(PATH_SEPARTOR) && !rpcContext.startsWith(PATH_SEPARTOR))
        {
            buffer.append(PATH_SEPARTOR);
        }

        buffer.append(rpcContext);

        return buffer.toString();
    }

    private String getLocalHandler(String identifier) throws GreenPepperServerException
    {
        String handlerName = propertiesManager.getProperty(ServerPropertiesManager.HANDLER, identifier);

        if (StringUtil.isEmpty(handlerName))
        {
            throw new GreenPepperServerException(GreenPepperServerErrorKey.NO_CONFIGURATION, "No config");
        }

        return handlerName;
    }
}
