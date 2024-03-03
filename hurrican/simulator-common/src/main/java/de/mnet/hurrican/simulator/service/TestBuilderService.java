package de.mnet.hurrican.simulator.service;

import java.util.*;
import org.springframework.context.ApplicationContext;

import de.mnet.hurrican.simulator.builder.SimulatorTestBuilder;
import de.mnet.hurrican.simulator.builder.TestBuilderParam;

/**
 * Service interface capable of executing test builders. The service takes care on setting up test builder before
 * execution. Service gets a list of normalized parameters which has to be translated to setters on the test builder
 * instance before execution.
 *
 *
 */
public interface TestBuilderService<T extends SimulatorTestBuilder> {

    /**
     * Executes a test builder instance with given test builder parameters which get translated into setters on test
     * builder implementation. It is ensured that test parameters do have necessary keys set. Application context is
     * necessary to create proper test context for execution.
     *
     * @param testBuilder
     * @param parameter
     * @param applicationContext
     */
    void run(T testBuilder, Map<String, Object> parameter, ApplicationContext applicationContext);

    /**
     * Builds a list of required test builder parameters. Values in this list represent default values. These values may
     * be set by outside logic then.
     *
     * @return
     */
    List<TestBuilderParam> getTestBuilderParameter();
}
