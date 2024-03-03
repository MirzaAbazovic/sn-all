/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.13
 */
package de.mnet.hurrican.simulator.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.consol.citrus.exceptions.TestCaseFailedException;
import com.consol.citrus.report.TestResult;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;

import de.mnet.hurrican.simulator.builder.SimulatorTestBuilder;
import de.mnet.hurrican.simulator.builder.TestBuilderParam;
import de.mnet.hurrican.simulator.builder.UseCaseTrigger;
import de.mnet.hurrican.simulator.service.MessageTemplateService;
import de.mnet.hurrican.simulator.service.TestBuilderService;

/**
 * Servlet implementation enables user to invoke a simulator use case and wait for messages to be exchanged.
 *
 *
 */
public class SimulatorInvokeServlet extends AbstractSimulatorServlet {

    private static final long serialVersionUID = 177099657522134737L;

    /** Handlebars templates */
    private Template invokeTemplate;

    /**
     * Service for executing test builders
     */
    private TestBuilderService<SimulatorTestBuilder> testBuilderService;
    private MessageTemplateService messageTemplateService;

    @Override
    public void init() throws ServletException {
        super.init();

        testBuilderService = getApplicationContext().getBean(TestBuilderService.class);
        messageTemplateService = getApplicationContext().getBean(MessageTemplateService.class);
        invokeTemplate = compileHandlebarsTemplate("invoke");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Context context = Context.newContext(buildViewModel(req));
        invokeTemplate.apply(context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String useCaseName = req.getParameter("useCase");
        SimulatorTestBuilder testBuilder = getApplicationContext().getBean(useCaseName, SimulatorTestBuilder.class);

        Map<String, Object> formParameters = getFormParameter(req);

        TestResult testResult;
        try {
            testBuilderService.run(testBuilder, formParameters, getApplicationContext());
            testResult = new TestResult(testBuilder.getUseCaseName(), TestResult.RESULT.SUCCESS, testBuilder.getTestParameters());
        }
        catch (TestCaseFailedException e) {
            testResult = new TestResult(testBuilder.getUseCaseName(), TestResult.RESULT.FAILURE, e.getCause(), testBuilder.getTestParameters());
        }

        Map<String, Object> model = buildViewModel(req);
        model.put("result", testResult);

        Context context = Context.newContext(model);
        invokeTemplate.apply(context, resp.getWriter());
    }

    /**
     * Reads form parameters from http request and transforms these into map of test builder parameters. Master for
     * parameter names is the default list of test parameters provided by test builder service. Method tries to read all
     * parameters from http request accordingly.
     *
     * @param req
     * @return
     */
    private Map<String, Object> getFormParameter(HttpServletRequest req) {
        List<TestBuilderParam> defaultParameters = testBuilderService.getTestBuilderParameter();
        Map<String, Object> formParameters = new LinkedHashMap<>(defaultParameters.size());
        for (TestBuilderParam parameterEntry : defaultParameters) {
            String formParameter = req.getParameter(parameterEntry.getId());
            if (formParameter != null) {
                formParameters.put(parameterEntry.getId(), formParameter);
            }
            else {
                formParameters.put(parameterEntry.getId(), parameterEntry.getValue());
            }
        }

        formParameters.put("payload", req.getParameter("payload"));

        return formParameters;
    }

    /**
     * Builds default view model for this servlet's handlebars view template.
     *
     * @param req
     * @return
     */
    private Map<String, Object> buildViewModel(HttpServletRequest req) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("useCaseList", getApplicationContext().getBeansOfType(UseCaseTrigger.class));
        model.put("messageTemplates", messageTemplateService.getDefaultMessageTemplates());
        model.put("parameter", testBuilderService.getTestBuilderParameter());
        model.put("contextPath", req.getContextPath());

        return model;
    }
}
