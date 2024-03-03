package de.mnet.hurrican.simulator.builder;

import java.util.*;
import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.CitrusTestBuilder;

/**
 *
 */
public class MockTestBuilder extends CitrusTestBuilder implements SimulatorTestBuilder {

    @Override
    public void execute(TestContext context) {
        configure();
        verifyTestCase(getTestCase());
    }

    protected void verifyTestCase(TestCase testCase) {
    }

    @Override
    public String getUseCaseName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getUseCaseVersion() {
        return "v0";
    }

    @Override
    public Map<String, Object> getTestParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void setTestBuilderParameter(Map<String, Object> testBuilderParameter) {
    }

}
