/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.11.13
 */
package de.augustakom.common;

import org.apache.log4j.Logger;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Logs TestNG events like {@link ITestListener#onTestFailure(org.testng.ITestResult)}, to help in identifying which
 * test is causing problems. Particularly useful when Service tests stall when executing within a jenkins job.
 */
public class TestNGEventListener implements IConfigurationListener, ITestListener {
    private static final Logger LOGGER = Logger.getLogger(TestNGEventListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.debug(getLogMessage("onTestStart", result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.debug(getLogMessage("onTestSuccess", result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error(getLogMessage("onTestFailure", result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.debug(getLogMessage("onTestSkipped", result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LOGGER.debug(getLogMessage("onTestFailedButWithinSuccessPercentage", result));
    }

    @Override
    public void onStart(ITestContext context) {
        LOGGER.debug("onStart");
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.debug("onFinish");
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        LOGGER.debug(getLogMessage("onConfigurationSuccess", itr));
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        LOGGER.error(getLogMessage("onConfigurationFailure", itr));
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        LOGGER.debug(getLogMessage("onConfigurationSkip", itr));
    }

    private String getLogMessage(String listenerMethod, ITestResult itr) {
        String testClass = itr.getTestClass().getName();
        String realClass = itr.getMethod().getRealClass().getName();
        if (realClass.equals(testClass)) {
            return String.format("%s - %s.%s()", listenerMethod, testClass, itr.getName());
        }
        return String.format("%s - %s.%s() (%s)", listenerMethod, testClass, itr.getName(), realClass);
    }
}
