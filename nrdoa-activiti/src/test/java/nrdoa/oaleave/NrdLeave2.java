package nrdoa.oaleave;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/*
 * 排他网关  nrdleave2.bpmn
 */

public class NrdLeave2 {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 1. 部署流程定义 */
 @Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("请假申请2")// 添加部署的名称
				.addClasspathResource("diagrams/nrdleave2.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/nrdleave2.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy();// 完成部署

		System.out.println("部署ID：" + deployment.getId());// 1
		System.out.println("部署名称：" + deployment.getName());// 请假申请 701
	}

	/** 2. 启动流程实例 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "nrdleave2";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());// 流程实例ID 101
		System.out.println("ID:" + pi.getProcessInstanceId());// 流程实例ID 1501
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());// 流程定义ID nrdleave:2:1304
	}

	/** 3.1 完成任务 */
	@Test
	public void completeMyPersonalTaskParam1() {
		/**
		 * ACT_RU_TASK
		 */
		// 任务ID
		String taskId = "4004";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 设置流程变量day=3
		paramMap.put("day", 11);
		// 提交任务的时候传入流程变量

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
//		.complete(taskId);
				.complete(taskId, paramMap);

		System.out.println("完成任务：任务ID：" + taskId);
	}

	/** 3.2 完成任务 */
	@Test
	public void completeMyPersonalTaskParam2() {
		/**
		 * ACT_RU_TASK
		 */
		// 任务ID
		String taskId = "4104";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

		System.out.println("完成任务：任务ID：" + taskId);
	}

	 

}
