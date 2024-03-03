/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2014
 */
package de.mnet.hurrican.simulator.servlet;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.actions.SleepAction;
import com.consol.citrus.report.TestResult;
import com.consol.citrus.report.TestResults;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 *
 */
public class SimulatorActivityServlet extends AbstractSimulatorServlet {

    private static final long serialVersionUID = 5694832985766284395L;

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger("SimActivityLogger");

    /**
     * Accumulated test results
     */
    private TestResults testResults = new TestResults();

    /**
     * Currently running test
     */
    private Map<Integer, TestResult> runningTests = new LinkedHashMap<>();

    /**
     * Handlebars
     */
    private Template activityTemplate;

    /**
     * Date format for start and end date display
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init() throws ServletException {
        super.init();
        activityTemplate = compileHandlebarsTemplate("activity");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (StringUtils.hasText(req.getQueryString()) && req.getQueryString().contains("clear=true")) {
            testResults.clear();
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("running", runningTests);
        model.put("results", reverseOrder(testResults));
        model.put("contextPath", req.getContextPath());

        Context context = Context.newContext(model);
        activityTemplate.apply(context, resp.getWriter());
    }

    @Override
    public void onTestStart(TestCase test) {
        test.getParameters().put("START", dateFormat.format(new Date()));
        runningTests.put(test.hashCode(), new TestResult(test.getName(), TestResult.RESULT.SUCCESS, test.getParameters()));
    }

    @Override
    public void onTestFinish(TestCase test) {
        test.getParameters().put("END", dateFormat.format(new Date()));
        runningTests.remove(test.hashCode());
    }

    @Override
    public void onTestSuccess(TestCase test) {
        TestResult result = new TestResult(test.getName(), TestResult.RESULT.SUCCESS, test.getParameters());
        testResults.addResult(result);
        LOG.info(result.toString());
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
        TestResult result = new TestResult(test.getName(), TestResult.RESULT.FAILURE, cause, test.getParameters());
        testResults.addResult(result);

        LOG.info(result.toString());
        LOG.info(result.getFailureCause());
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
        if (!testAction.getClass().equals(SleepAction.class)) {
            LOG.debug(testCase.getName() + "(" +
                    getParameterString(testCase.getParameters()) + ") - " +
                    testAction.getName() + ": " +
                    (StringUtils.hasText(testAction.getDescription()) ? testAction.getDescription() : ""));
        }
    }

    private String getParameterString(Map<String, Object> parameters) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }

        return builder.toString();
    }

    /**
     * Reverses the order of the provided test results
     */
    private TestResults reverseOrder(TestResults testResults) {
        TestResults reversed = new TestResults();
        for (int i = testResults.size(); i > 0; i--) {
            reversed.add(testResults.get(i - 1));
        }
        return reversed;
    }
}
