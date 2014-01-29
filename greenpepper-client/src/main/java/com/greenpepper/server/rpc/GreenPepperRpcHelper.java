package com.greenpepper.server.rpc;

import java.util.Vector;

public interface GreenPepperRpcHelper
{
    String getRenderedSpecification(String username, String password, Vector<?> args);
    Vector getSpecificationHierarchy(String username, String password, Vector<?> args);
    String setSpecificationAsImplemented(String username, String password, Vector<?> args);
	String saveExecutionResult(String username, String password, Vector<?> args);
}