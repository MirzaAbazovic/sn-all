/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 13:30:30
 */
package de.mnet.wita.bpm.test;

import java.lang.reflect.*;
import java.util.*;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.test.TestHelper;
import org.activiti.engine.impl.util.ClassNameUtil;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.Logger;

/**
 * Convenience for ProcessEngine and services initialization in the form of a TestNG helper class. Used by {@link
 * AbstractActivitiBaseTest}. <p/> <p> Usage: </p> <p/>
 * <pre>
 * public class YourTest {
 *
 *   &#64;Rule
 *   public ActivitiTestNGRule activitiRule = new ActivitiTestNGRule();
 *
 *   ...
 * }
 * </pre>
 * <p/> <p> The ProcessEngine and the services will be made available to the test class through the getters of the
 * activitiRule. The processEngine will be initialized by default with the activiti.cfg.xml resource on the classpath.
 * To specify a different configuration file, pass the resource location in {@link #ActivitiTestNGRule(String) the
 * appropriate constructor}. Process engines will be cached statically. Right before the first time the setUp is called
 * for a given configuration resource, the process engine will be constructed. </p> <p/> <p> You can declare a
 * deployment with the {@link Deployment} annotation. This base class will make sure that this deployment gets deployed
 * before the setUp and {@link RepositoryService#deleteDeployment(String, boolean) cascade deleted} after the tearDown.
 * </p> <p/> <p> The activitiRule also lets you {@link ActivitiTestNGRule#setCurrentTime(Date) set the current time used
 * by the process engine}. This can be handy to control the exact time that is used by the engine in order to verify
 * e.g. e.g. due dates of timers. Or start, end and duration times in the history service. In the tearDown, the internal
 * clock will automatically be reset to use the current system time rather then the time that was set during a test
 * method. In other words, you don't have to clean up your own time messing mess ;-) </p>
 *
 * @author Tom Baeyens
 */
public class ActivitiTestNGRule {

    private static final Logger LOGGER = Logger.getLogger(ActivitiTestNGRule.class);

    /**
     * Method copied from {@link TestHelper#annotationDeploymentSetUp(ProcessEngine, Class, String)} to fix bug in
     * TestHelper.annotationDeploymentSetUp such that Method with arguments (required for DataProvider) will work with
     * AcitivitiTestNGRule ;)
     */
    private static String annotationDeploymentSetUp(ProcessEngine processEngine, Method method) {
        String deploymentId = null;
        Deployment deploymentAnnotation = method.getAnnotation(Deployment.class);
        LOGGER.info("annotation @Deployment creates deployment for "
                + ClassNameUtil.getClassNameWithoutPackage(method.getDeclaringClass()) + "." + method.getName());
        if (deploymentAnnotation != null) {
            String[] resources = deploymentAnnotation.resources();
            if (resources.length == 0) {
                String name = method.getName();
                String resource = TestHelper.getBpmnProcessDefinitionResource(method.getDeclaringClass(), name);
                resources = new String[] { resource };
            }

            DeploymentBuilder deploymentBuilder = processEngine
                    .getRepositoryService()
                    .createDeployment()
                    .name(ClassNameUtil.getClassNameWithoutPackage(method.getDeclaringClass()) + "." + method.getName());
            for (String resource : resources) {
                deploymentBuilder.addClasspathResource(resource);
            }
            deploymentId = deploymentBuilder.deploy().getId();
        }
        return deploymentId;
    }

    protected String configurationResource = "activiti.cfg.xml";
    protected String deploymentId = null;

    protected ProcessEngine processEngine;
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected IdentityService identityService;
    protected ManagementService managementService;
    protected FormService formService;

    public ActivitiTestNGRule() {
        // for Spring
    }

    public ActivitiTestNGRule(String configurationResource) {
        this.configurationResource = configurationResource;
    }

    public ActivitiTestNGRule(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void starting(Method method) {
        if (processEngine == null) {
            initializeProcessEngine();
            initializeServices();
        }
        deploymentId = annotationDeploymentSetUp(processEngine, method);
    }

    protected void initializeProcessEngine() {
        processEngine = TestHelper.getProcessEngine(configurationResource);
    }

    protected void initializeServices() {
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        formService = processEngine.getFormService();
    }

    public void finished(Method method) {
        TestHelper.annotationDeploymentTearDown(processEngine, deploymentId, method.getDeclaringClass(),
                method.getName());

        ClockUtil.reset();
    }

    public void setCurrentTime(Date currentTime) {
        ClockUtil.setCurrentTime(currentTime);
    }

    public String getConfigurationResource() {
        return configurationResource;
    }

    public void setConfigurationResource(String configurationResource) {
        this.configurationResource = configurationResource;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public RuntimeService getRuntimeService() {
        return runtimeService;
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public void setHistoricDataService(HistoryService historicDataService) {
        this.historyService = historicDataService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public ManagementService getManagementService() {
        return managementService;
    }

    public FormService getFormService() {
        return formService;
    }

    public void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }
}
