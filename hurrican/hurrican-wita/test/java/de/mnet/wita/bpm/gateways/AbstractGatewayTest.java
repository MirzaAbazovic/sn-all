/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 14:01:51
 */
package de.mnet.wita.bpm.gateways;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.*;
import java.util.*;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activiti.engine.delegate.DelegateExecution;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

@Test(groups = BaseTest.UNIT)
public class AbstractGatewayTest extends BaseTest {

    private static class MethodToMethodNameAndResult<T extends AbstractGateway> implements
            Function<Method, Pair<String, Boolean>> {
        private final T processGateway;
        private final DelegateExecution execution;

        private MethodToMethodNameAndResult(T processGateway, DelegateExecution execution) {
            this.processGateway = processGateway;
            this.execution = execution;
        }

        @Override
        public Pair<String, Boolean> apply(Method input) {
            try {
                Boolean result = (Boolean) input.invoke(processGateway, execution);
                return Pair.create(input.getName(), result);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Predicate<Pair<String, Boolean>> SECOND_IS_TRUE = new Predicate<Pair<String, Boolean>>() {
        @Override
        public boolean apply(Pair<String, Boolean> input) {
            return input.getSecond();
        }
    };

    private final Predicate<Method> IS_GATEWAY_METHOD = new Predicate<Method>() {
        @Override
        public boolean apply(Method input) {
            return Modifier.isPublic(input.getModifiers())
                    && Arrays.equals(input.getParameterTypes(), new Class<?>[] { DelegateExecution.class });
        }
    };

    @DataProvider
    public Object[][] executionVariables() throws Exception {
        // @formatter:off
        ImmutableList<Class<? extends AbstractGateway>> classes = ImmutableList.of(
                ProcessTeqGateway.class,
                ProcessMessageGateway.class,
                ProcessTamGateway.class);
        // @formatter:on

        List<Object[]> resultList = newArrayList();
        for (Class<? extends AbstractGateway> clazz : classes) {
            for (MeldungsType meldungType : MeldungsType.values()) {
                for (AenderungsKennzeichen aenderungsKennzeichen : AenderungsKennzeichen.values()) {
                    for (GeschaeftsfallTyp geschaeftsfallTyp : GeschaeftsfallTyp.implementedValues()) {
                        for (Boolean workflowError : newArrayList(true, false, null)) {
                            resultList.add(new Object[] { getInstance4Clazz(clazz), meldungType, aenderungsKennzeichen,
                                    geschaeftsfallTyp, workflowError });
                        }
                    }
                }
            }
        }
        return resultList.toArray(new Object[resultList.size()][]);
    }

    private <T extends AbstractGateway> T getInstance4Clazz(Class<T> clazz) throws Exception {
        T result = clazz.newInstance();
        if (result instanceof ProcessTamGateway) {
            ((ProcessTamGateway) result).mwfEntityDao = mock(MwfEntityDao.class);
        }
        return result;
    }

    @Test(dataProvider = "executionVariables")
    public <T extends AbstractGateway> void gatewayShouldBeExclusive(T gateway, MeldungsType meldungsType,
            AenderungsKennzeichen aenderungsKennzeichen, GeschaeftsfallTyp geschaeftsfallTyp, Boolean workflowError)
            throws Exception {

        List<Method> methods = Lists.newArrayList();
        Class<?> clazz = gateway.getClass();
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            methods.addAll(Arrays.asList(declaredMethods));
            clazz = clazz.getSuperclass();
        }

        Iterable<Method> gatewayMethods = Iterables.filter(methods, IS_GATEWAY_METHOD);
        assertThat(gatewayMethods, not(Matchers.<Method>emptyIterable()));

        DelegateExecution execution = setupExecutionMock(meldungsType, aenderungsKennzeichen, geschaeftsfallTyp,
                workflowError);

        Iterable<Pair<String, Boolean>> gatewayMethodsResults = Iterables.transform(gatewayMethods,
                new MethodToMethodNameAndResult<T>(gateway, execution));

        List<Pair<String, Boolean>> takenGateways = newArrayList(filter(gatewayMethodsResults, SECOND_IS_TRUE));
        assertThat("More than 1 method yielded true: " + takenGateways, takenGateways.size(), lessThanOrEqualTo(1));
    }

    private DelegateExecution setupExecutionMock(MeldungsType meldungsType,
            AenderungsKennzeichen aenderungsKennzeichen, GeschaeftsfallTyp geschaeftsfallTyp, Boolean workflowError) {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(WitaTaskVariables.WITA_IN_MWF_ID.id)).thenReturn(123L);
        when(execution.getVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id)).thenReturn(meldungsType.name());
        when(execution.getVariable(WitaTaskVariables.WITA_MESSAGE_AENDERUNGSKENNZEICHEN.id)).thenReturn(
                aenderungsKennzeichen.name());
        when(execution.getVariable(WitaTaskVariables.WITA_MESSAGE_GESCHAEFTSFALL.id)).thenReturn(
                geschaeftsfallTyp.getDtagMeldungGeschaeftsfall());
        when(execution.getVariable(WitaTaskVariables.WORKFLOW_ERROR.id)).thenReturn(workflowError);
        return execution;
    }
}
