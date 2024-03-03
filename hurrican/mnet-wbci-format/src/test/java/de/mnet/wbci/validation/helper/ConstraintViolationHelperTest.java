/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.validation.helper;

import static org.mockito.Mockito.*;

import java.util.*;
import javax.validation.*;
import javax.validation.metadata.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;

/**
 *
 */
public class ConstraintViolationHelperTest {
    ConstraintViolationHelper testling = new ConstraintViolationHelper();
    Set<ConstraintViolation<Meldung>> violationTestSet;
    @Mock
    private Path.Node nodeMock;
    @Mock
    private Meldung meldungMock;

    private String testMessage = "Der angegebene Meldungscode ADAHSNR ist nicht zulässig";
    String className = Meldung.class.getSimpleName();

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        violationTestSet = new HashSet<>();
        violationTestSet.add(new ConstraintViolation<Meldung>() {
            @Override
            public String getMessage() {
                return testMessage;
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Meldung getRootBean() {
                when(meldungMock.getTyp()).thenReturn(MeldungTyp.ABBM);
                return meldungMock;
            }

            @Override
            public Class<Meldung> getRootBeanClass() {
                return Meldung.class;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                Path pathmock = Mockito.mock(Path.class);
                Iterator itMock = Arrays.asList(nodeMock).iterator();
                when(pathmock.iterator()).thenReturn(itMock);
                return pathmock;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }
        });
    }

    @Test
    public void testConvertToErrorMsgWithNode() throws Exception {
        String nodename = "TEST_NODE_NAME";
        when(nodeMock.getName()).thenReturn(nodename);
        String errorMessage = testling.generateErrorMsg(this.violationTestSet);
        Assert.assertTrue(errorMessage.startsWith(String.format("Die %s ist nicht vollständig",
                MeldungTyp.ABBM.toString())));
        Assert.assertTrue(errorMessage.contains(String.format("- %s: %s!", nodename, testMessage)));
        Assert.assertFalse(errorMessage.contains("- " + className));
    }

    @Test
    public void testConvertToErrorMsgWithClass() throws Exception {

        when(nodeMock.getName()).thenReturn(null);
        String errorMessage = testling.generateErrorMsg(this.violationTestSet);
        Assert.assertTrue(errorMessage.startsWith(String.format("Die %s ist nicht vollständig",
                MeldungTyp.ABBM.toString())));
        Assert.assertTrue(errorMessage.contains("- " + className));
        Assert.assertTrue(errorMessage.contains(testMessage + "!"));
    }

    @Test
    public void testConvertToWarnMsgWithNode() throws Exception {
        String nodename = "TEST_NODE_NAME";
        when(nodeMock.getName()).thenReturn(nodename);
        String errorMessage = testling.generateWarningMsg(this.violationTestSet);
        Assert.assertTrue(errorMessage.startsWith(String.format("Achtung, die %s enthält Warnungen.",
                MeldungTyp.ABBM.toString())));
        Assert.assertTrue(errorMessage.contains(String.format("- %s: %s!", nodename, testMessage)));
        Assert.assertFalse(errorMessage.contains("- " + className));
    }


    @Test
    public void testConvertToWarnMsgWithClass() throws Exception {
        when(nodeMock.getName()).thenReturn(null);
        String errorMessage = testling.generateWarningMsg(this.violationTestSet);
        Assert.assertTrue(errorMessage.startsWith(String.format("Achtung, die %s enthält Warnungen.",
                MeldungTyp.ABBM.toString())));
        Assert.assertTrue(errorMessage.contains("- " + className));
        Assert.assertTrue(errorMessage.contains(testMessage + "!"));
    }

}
