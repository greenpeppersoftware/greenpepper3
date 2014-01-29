package com.greenpepper.server;

public interface GreenPepperServerErrorKey
{
    public static final String SUCCESS = "<success>";
    public static final String ERROR = "<exception>";

    /**
     * General errors
     */
    public static final String GENERAL_ERROR = "greenpepper.server.generalerror";
    public static final String CONFIGURATION_ERROR = "greenpepper.server.configerror";
    public static final String XML_RPC_URL_NOTFOUND = "greenpepper.server.xmlrpcurlinvalid";
    public static final String XML_RPC_HANDLER_NOTFOUND = "greenpepper.server.xmlrpchandlerinvalid";
    public static final String NO_CONFIGURATION = "greenpepper.server.noconfiguration";
    public static final String CALL_FAILED = "greenpepper.server.callfailed";
    public static final String MARSHALL_NOT_SUPPORTED = "greenpepper.server.marshallingnotsupported";

    /**
     * Failed to retrieve.
     */
    public static final String RETRIEVE_PROJECTS = "greenpepper.server.retrieveprojects";
    public static final String RETRIEVE_REPOSITORIES= "greenpepper.server.retrieverepos";
    public static final String RETRIEVE_SPECIFICATION_REPOS = "greenpepper.server.retrievespecrepos";
    public static final String RETRIEVE_REQUIREMENT_REPOS = "greenpepper.server.retrieverequirementrepos";
    public static final String RETRIEVE_SUTS = "greenpepper.server.retrievesuts";
    public static final String RETRIEVE_COMPILATION = "greenpepper.server.retrievecompil";
    public static final String RETRIEVE_REFERENCES = "greenpepper.server.retrievereferences";
	public static final String RETRIEVE_EXECUTIONS = "greenpepper.server.retrieveexecutions";
    public static final String RETRIEVE_REFERENCE = "greenpepper.server.retrievereference";
    public static final String RETRIEVE_FILE_FAILED = "greenpepper.server.filefailed";

    /**
     * Project's errors.
     */
    public static final String PROJECT_NOT_FOUND = "greenpepper.server.projectnotfound";
	public static final String PROJECT_ALREADY_EXISTS = "greenpepper.server.projectalreadyexist";
    public static final String PROJECT_DEFAULT_SUT_NOT_FOUND = "greenpepper.server.defaultsutnotfound";
	public static final String PROJECT_REMOVE_FAILED = "greenpepper.server.removeprojectfailed";
	public static final String PROJECT_REPOSITORY_ASSOCIATED = "greenpepper.server.projectrepoassociated";
	public static final String PROJECT_SUTS_ASSOCIATED = "greenpepper.server.projectsutsassociated";

	/**
     * Repository's errors.
     */
    public static final String REPOSITORY_CLASS_NOT_FOUND = "greenpepper.server.repoclassnotfound";
    public static final String REPOSITORY_DOC_ASSOCIATED = "greenpepper.server.repodocassociated";
    public static final String REPOSITORY_NOT_FOUND = "greenpepper.server.repositorynotfound";
    public static final String REPOSITORY_UPDATE_FAILED = "greenpepper.server.repoupdatefailed";
    public static final String REPOSITORY_TYPE_NOT_FOUND = "greenpepper.server.rtypenotfound";
    public static final String PROJECT_CREATE_FAILED = "greenpepper.server.createprojectfailed";
	public static final String PROJECT_UPDATE_FAILED = "greenpepper.server.projectupdatefailed";
    public static final String REPOSITORY_ALREADY_EXISTS = "greenpepper.server.repoalreadyexists";
    public static final String REPOSITORY_REMOVE_FAILED = "greenpepper.server.removerepofailed";
    public static final String REPOSITORY_DOES_NOT_CONTAINS_SPECIFICATION = "greenpepper.repositorynotspecification";
    public static final String REPOSITORY_GET_REGISTERED = "greenpepper.server.retrieverepository";
    public static final String REPOSITORY_REGISTRATION_FAILED = "greenpepper.server.registrationfailed";
    public static final String REPOSITORY_UNREGISTRATION_FAILED = "greenpepper.server.unregistrationfailed";

    /**
     * Requirement's errors.
     */
    public static final String REQUIREMENT_NOT_FOUND = "greenpepper.server.requirementnotfound";
    public static final String REQUIREMENT_ALREADY_EXISTS = "greenpepper.server.requirementalreadyexists";
    public static final String REQUIREMENT_REMOVE_FAILED = "greenpepper.server.removerequirementfailed";
    
    /**
     * Specification's errors
     */
    public static final String SPECIFICATION_NOT_FOUND = "greenpepper.server.specificationnotfound";
    public static final String SPECIFICATIONS_NOT_FOUND = "greenpepper.server.specificationsnotfound";
    public static final String SPECIFICATION_CREATE_FAILED = "greenpepper.server.createspecificationfailed";
    public static final String SPECIFICATION_REFERENCED = "greenpepper.server.removereferencedspecification";
    public static final String SPECIFICATION_ALREADY_EXISTS = "greenpepper.server.specificationalreadyexists";
    public static final String SPECIFICATION_ADD_SUT_FAILED = "greenpepper.server.addsutspecificational";
    public static final String SPECIFICATION_REMOVE_SUT_FAILED = "greenpepper.server.removesutspecificational";
    public static final String SPECIFICATION_UPDATE_FAILED = "greenpepper.server.updatespecificationfailed";
    public static final String SPECIFICATION_REMOVE_FAILED = "greenpepper.server.removespecificationfailed";
    public static final String SPECIFICATION_RUN_FAILED = "greenpepper.server.runspecificationfailed";
    public static final String SPECIFICATION_IMPLEMENTED_FAILED = "greenpepper.server.implementedfailed";
        
    /**
     * Runners errors
     */
    public static final String RUNNER_ALREADY_EXISTS = "greenpepper.server.runneralreadyexists";
    public static final String RUNNERS_NOT_FOUND = "greenpepper.server.runnersnotfound";
    public static final String RUNNER_NOT_FOUND = "greenpepper.server.runnernotfound";
    public static final String RUNNER_CREATE_FAILED = "greenpepper.server.runnercreatefailed";
    public static final String RUNNER_UPDATE_FAILED = "greenpepper.server.runnerupdatefailed";
    public static final String RUNNER_REMOVE_FAILED = "greenpepper.server.runnerremovefailed";
    public static final String RUNNER_SUT_ASSOCIATED = "greenpepper.server.runnersutassociated";
    public static final String ENVTYPES_NOT_FOUND =  "greenpepper.server.envtypesnotfound";
    public static final String ENVTYPE_NOT_FOUND =  "greenpepper.server.envtypenotfound";
    
    /**
     * System under test's error.
     */
    public static final String SUT_NOT_FOUND = "greenpepper.server.sutnotfound";
    public static final String SUT_REFERENCE_ASSOCIATED = "greenpepper.server.sutwithreferences";
    public static final String SUT_SPECIFICATION_ASSOCIATED = "greenpepper.server.sutwithspecifications";
    public static final String SUT_EXECUTION_ASSOCIATED = "greenpepper.server.sutwithexecutions";
    public static final String SUT_CREATE_FAILED = "greenpepper.server.createsutfailed";
    public static final String SUT_SET_DEFAULT_FAILED = "greenpepper.server.setdefaultsutfailed";
    public static final String SUT_ALREADY_EXISTS = "greenpepper.server.sutalreadyexists";
    public static final String SUT_UPDATE_FAILED = "greenpepper.server.updatesutfailed";
    public static final String SUT_DELETE_FAILED = "greenpepper.server.deletesutfailed";

    /**
     * Reference's errors.
     */
    public static final String REFERENCE_NOT_FOUND = "greenpepper.server.referencenotfound";
    public static final String REFERENCE_CREATE_FAILED = "greenpepper.server.createreferencefailed";
    public static final String REFERENCE_UPDATE_FAILED = "greenpepper.server.updatereferencefailed";
    public static final String REFERENCE_REMOVE_FAILED = "greenpepper.server.removereferencefailed";
    public static final String RUN_REFERENCE_FAILED = "greenpepper.server.runreferencefailed";
	public static final String REFERENCE_CREATE_ALREADYEXIST = "greenpepper.server.createreferencealreadyexist";

	/**
	 * Execution's errors
	 */
	public static final String EXECUTION_CREATE_FAILED = "greenpepper.server.createexecutionfailed";

    /** ????? */
    public static final String RESOLVED_URI_FAILED = "greenpepper.server.failedtoresolveuri";
}
