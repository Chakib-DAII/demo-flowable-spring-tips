package com.demoflowablespringtips.demoflowablespringtips.processes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Deployment(resources = { "processes/one-single-task-process.bpmn20.xml" })
class processDemo{

	/*private final ProcessEngine processEngine;
	
	public processDemo(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}*/
	

	private static String CUSTOMER_ID_PV="customerId";
	private static String EMAIL_PV="email";
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private EmailService emailService;
	
	//private final RuntimeService runtimeService;
	//private final TaskService taskService;
	
	
	public processDemo(RuntimeService runtimeService, TaskService taskService) {
		super();
		//this.runtimeService = runtimeService;
		//this.taskService = taskService;
	}

	private String beginCustomerEnrollement(String costumerId,String email){
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(CUSTOMER_ID_PV, costumerId);
		vars.put(EMAIL_PV, email);
		ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("signup-process",vars);
		System.out.println("Process Instance: "+processInstance.getProcessDefinitionId());
		return processInstance.getId();
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void enrollNewUser() throws Exception{
		String customerId ="1";	
		String email = "email@email.com";
		String instanceId = this.beginCustomerEnrollement(customerId, email);
		//log.info("process instance ID:" + this.beginCustomerEnrollement(customerId, "email@email.com"));	
		//assert instanceId != null : "the process instance id should not be null";
		if (!(instanceId != null)) {
		    throw new IllegalArgumentException("the process instance id should not be null");
		}
		System.out.println("process instance ID:" + instanceId);
		
		
		//this.beginCustomerEnrollement(customerId, "email@email.com");
		// async
		//this.confirmEmail(customerId);
		
		//task claiming and completing
		List<Task> tasks = this.taskService
		.createTaskQuery()
		//.taskId("confirm-email-task")
		.taskName("confirm email")
		.includeProcessVariables()
		.processVariableValueEquals(CUSTOMER_ID_PV,customerId)
		.list();
		System.out.println("Tasks: "+tasks.size());
		//assert (tasks.size() >= 1) : "there should be one outstanding";
		//Assertions.assertTrue(tasks.size() >= 1,"there should be one outstanding");
		if (!(tasks.size() >= 1)) {
		    throw new IllegalArgumentException("there should be one outstanding");
		}
		
		tasks.forEach(task -> {
			System.out.println("Task description: "+task.getName());
			this.taskService.claim(task.getId(), "Chakib");
			this.taskService.complete(task.getId());
		});
		
		//confirm that the email has been sent
		//assert (this.emailService.sends.get("email").get() == 1) : "email sends should be equal to one";
		//Assertions.assertEquals(this.emailService.sends.get("email").get(),1);
		if (!(this.emailService.sends.get(email).get() == 1)) {
		    throw new IllegalArgumentException("email sends should be equal to one");
		}
		
		List<HistoricProcessInstance> history = this.historyService
		.createHistoricProcessInstanceQuery()
		.includeProcessVariables()
		.variableValueEquals("customerId","1")
		.list();
		
		//assert (history.size() >= 1) : "there should be one outstanding";
		if (!(history.size() >= 1)) {
		    throw new IllegalArgumentException("there should be one outstanding");
		}
		
		history.forEach(hist -> {
			System.out.println("Hist process Id: "+hist.getId());
		});
	}

	private void confirmEmail(String customerId) {	
			
	}
}
