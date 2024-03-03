package de.mnet.hurrican.simulator.builder;

import java.util.*;

/**
 * Basic simulator test builder interface. All Simulator builders must implement this interface.
 *
 *
 */
public interface SimulatorTestBuilder {

    /**
     * Gets the use case name. Usually the class name, but Subclasses may use different use case
     * name.
     *
     * @return
     */
    String getUseCaseName();

    /**
     * Gets the use case version.
     * @return
     */
    String getUseCaseVersion();

    /**
     * Provides public access to the test case parameters.
     *
     * @return
     */
    public Map<String, Object> getTestParameters();

    /**
     * Sets the test builder parameter automatically as test variables.
     *
     * @param testBuilderParameter
     */
    public void setTestBuilderParameter(Map<String, Object> testBuilderParameter);
}
