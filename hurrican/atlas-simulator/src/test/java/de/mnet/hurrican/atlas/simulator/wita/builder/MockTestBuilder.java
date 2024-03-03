package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;

/**
 *
 */
public class MockTestBuilder extends AbstractWitaTest {

    @Override
    public void execute(TestContext context) {
        configure();
        verifyTestCase(getTestCase());
    }

    @Override
    public String getUseCaseName() {
        return "MockTestBuilder";
    }

    protected void verifyTestCase(TestCase testCase) {
    }
}
