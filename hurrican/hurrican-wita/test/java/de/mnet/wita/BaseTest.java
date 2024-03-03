/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2011 16:31:57
 */
package de.mnet.wita;

import java.lang.reflect.*;
import org.apache.log4j.BasicConfigurator;
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.BeforeSuite;

/**
 * Basisklasse von der alle TestNG-Tests ableiten. Konfiguriert das Logging-Framework.
 */
public abstract class BaseTest {

    public static final String UNIT = "unit";

    public WitaCdmVersion getWitaVersion() {
        return WitaCdmVersion.getDefault();
    }

    @BeforeSuite(alwaysRun = true)
    public void testSetupLogging() {
        BasicConfigurator.configure();
    }

    protected void setFieldValue(Class<?> clazz, String fieldName, Object target, Object value) {
        Field field = ReflectionUtils.findField(clazz, fieldName);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, value);
    }

    protected Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes, Object target, Object... args) {
        Method method = ReflectionUtils.findMethod(clazz, methodName, paramTypes);
        ReflectionUtils.makeAccessible(method);
        return ReflectionUtils.invokeMethod(method, target, args);
    }

}
