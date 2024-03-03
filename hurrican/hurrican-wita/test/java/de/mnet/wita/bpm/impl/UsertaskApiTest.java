/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2011 16:46:00
 */
package de.mnet.wita.bpm.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;

@Test(groups = SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        //        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
})
public class UsertaskApiTest extends AbstractActivitiBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Deployment(resources = { "de/mnet/wita/bpm/usertask-test.bpmn20.xml" })
    public void claimAndCompleteUsertaskShouldDeleteAndSignal() {
        ProcessInstance pi = activitiRuntimeService.startProcessInstanceByKey("usertasks");
        Task task = taskService.createTaskQuery().singleResult();

        taskService.claim(task.getId(), "me");
        taskService.complete(task.getId());

        assertThat(taskService.createTaskQuery().list(), hasSize(0));

        ExecutionEntity waitingPi = (ExecutionEntity) activitiRuntimeService.createProcessInstanceQuery()
                .processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNotNull(waitingPi);
        assertEquals(waitingPi.getActivityId(), "receive");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/usertask-test.bpmn20.xml" })
    public void signalAndDeleteUsertaskShouldWork() {
        ProcessInstance pi = activitiRuntimeService.startProcessInstanceByKey("usertasks");
        Task task = taskService.createTaskQuery().taskDefinitionKey("usertask").singleResult();
        assertNotNull(task);

        activitiRuntimeService.signal(pi.getId());
        Task signaledTask = taskService.createTaskQuery().taskDefinitionKey("usertask").singleResult();
        assertNotNull(signaledTask);

        taskService.deleteTask(signaledTask.getId());
        assertNull(taskService.createTaskQuery().taskDefinitionKey("usertask").singleResult());
        assertNotNull(activitiRuntimeService.createProcessInstanceQuery()
                .processInstanceId(pi.getProcessInstanceId())
                .singleResult());
    }

    @Deployment(resources = { "de/mnet/wita/bpm/usertask-test.bpmn20.xml" })
    public void testFindBusinessKeyForUsertask() {
        activitiRuntimeService.startProcessInstanceByKey("usertasks", "12345");
        final Task task = taskService.createTaskQuery().singleResult();
        assertNotNull(task);
        List<ProcessInstance> processes = activitiRuntimeService.createProcessInstanceQuery()
                .processDefinitionKey("usertasks").list();
        Iterables.filter(processes, new Predicate<ProcessInstance>() {

            @Override
            public boolean apply(ProcessInstance input) {
                return task.getProcessInstanceId().equals(input.getProcessInstanceId());
            }
        });

        assertEquals(Iterables.getOnlyElement(processes).getBusinessKey(), "12345");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/usertask-test.bpmn20.xml" })
    public void findByCandidateGroup() {
        activitiRuntimeService.startProcessInstanceByKey("usertasks", "12345");

        final Task task = taskService.createTaskQuery().taskDefinitionKey("usertask").singleResult();
        taskService.setOwner(task.getId(), "TAM");
        assertNotNull(task);

        taskService.claim(task.getId(), "me");
        final Task claimedTask = taskService.createTaskQuery().taskOwner("TAM").singleResult();
        assertNotNull(claimedTask);
    }

    @Deployment(resources = { "de/mnet/wita/bpm/parallel-usertask-test.bpmn20.xml" })
    public void testParallel() {
        ProcessInstance pi = activitiRuntimeService.startProcessInstanceByKey("parallel-usertasks");

        Task userTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        assertNotNull(userTask);

        taskService.claim(userTask.getId(), "me");
        taskService.complete(userTask.getId());

        Execution execution = getReceiveTask(pi);
        assertNotNull(execution.getId());

        activitiRuntimeService.signal(execution.getId());

        assertNull(getReceiveTask(pi));

        HistoricProcessInstance closedPi = activitiHistoryService.createHistoricProcessInstanceQuery()
                .processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNotNull(closedPi);
        assertNotNull(closedPi.getEndTime());
    }

    @Deployment(resources = { "de/mnet/wita/bpm/usertask-test.bpmn20.xml" })
    public void testFetchHistoricTasks() {
        ProcessInstance pi = activitiRuntimeService.startProcessInstanceByKey("usertasks", "12345");

        Task task = taskService.createTaskQuery().taskDefinitionKey("usertask").singleResult();
        taskService.complete(task.getId());
        runtimeService.signal(pi.getId());

        List<HistoricActivityInstance> historicInstances = activitiHistoryService.createHistoricActivityInstanceQuery()
                .processInstanceId(pi.getId())
                .orderByHistoricActivityInstanceId().asc()
                .list();

        Iterator<HistoricActivityInstance> iterator = historicInstances.iterator();
        assertEquals(iterator.next().getActivityId(), "usertask");
        assertEquals(iterator.next().getActivityId(), "receive");
        assertEquals(iterator.next().getActivityId(), "servicetask");
        assertFalse(iterator.hasNext());
    }

    private Execution getReceiveTask(ProcessInstance pi) {
        Execution execution = activitiRuntimeService.createExecutionQuery()
                .processInstanceId(pi.getProcessInstanceId()).activityId("receive").singleResult();
        return execution;
    }

}
