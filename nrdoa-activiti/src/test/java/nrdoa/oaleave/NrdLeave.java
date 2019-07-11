package nrdoa.oaleave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/*
 * 条件判断nrdleave.bpmn
 */

public class NrdLeave {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 1. 部署流程定义 */
//	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("请假申请")// 添加部署的名称
				.addClasspathResource("diagrams/nrdleave.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/nrdleave.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy();// 完成部署

		System.out.println("部署ID：" + deployment.getId());// 1
		System.out.println("部署名称：" + deployment.getName());// 请假申请 701
	}

	/** 2. 启动流程实例 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "nrdleave";
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
		String taskId = "3304";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 设置流程变量day=3
		paramMap.put("day", 5);
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
		String taskId = "3403";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

		System.out.println("完成任务：任务ID：" + taskId);
	}

	/*
	 * 实例 并完成 提交
	 */
	@Test
	public void startTask() {

		TaskService taskService = processEngine.getTaskService();

		// 通过流程实例ID获取任务对象
		Task task = taskService.createTaskQuery().processInstanceId("2301").singleResult();
		System.out.println("taskID:" + task.getId() + ",name:" + task.getName());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 设置流程变量day=3
		paramMap.put("day", 5);
		// 提交任务的时候传入流程变量
		taskService.complete(task.getId(), paramMap);

		// 查询任务
		task = taskService.createTaskQuery().processInstanceId("nrdleave:2:1304").singleResult();

		// 如果任务对象为空,则流程执行结束
		if (task != null) {
			System.out.println("taskID:" + task.getId() + ",name:" + task.getName());
		} else {
			System.out.println("任务执行完毕");
		}
	}

	/*
	 * 启动流程 并完成 提交
	 */
	@Test
	public void startProcessAndComp() {

		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("nrdleave");
		System.out.println(
				"id:" + pi.getId() + ",流程实例ID:" + pi.getProcessInstanceId() + ",流程定义ID:" + pi.getProcessDefinitionId());
		TaskService taskService = processEngine.getTaskService();

		// 通过流程实例ID获取任务对象
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
//							.singleResult();

		for (Task task : tasks) {
			System.out.println("taskID:" + task.getId() + ",name:" + task.getName());

			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 设置流程变量day=3
			paramMap.put("day", 5);
			// 提交任务的时候传入流程变量
			taskService.complete(task.getId(), paramMap);

			// 查询任务
//			task = taskService.createTaskQuery()
//					.processInstanceId(pi.getProcessInstanceId())
//					.singleResult();

		}
	}

	/**
	 * 启动流程 并完成 提交
	 */
	@Test
	public void startProcessAndComp2() {

		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("leave");

		System.out.println(
				"id:" + pi.getId() + ",流程实例ID:" + pi.getProcessInstanceId() + ",流程定义ID:" + pi.getProcessDefinitionId());

		TaskService taskService = processEngine.getTaskService();

		// 通过流程实例ID获取任务对象
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		System.out.println("taskID:" + task.getId() + ",name:" + task.getName());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 设置流程变量day=3
		paramMap.put("day", 3);
		// 提交任务的时候传入流程变量
		taskService.complete(task.getId(), paramMap);

		// 查询任务
		task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

		// 如果任务对象为空,则流程执行结束
		if (task != null) {
			System.out.println("taskID:" + task.getId() + ",name:" + task.getName());
		} else {
			System.out.println("任务执行完毕");
		}

	}

	/** 查询当前人的个人任务 */
	@Test
	public void findMyPersonalTask() {
		String assignee = "张三"; // 王五 张三
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.createTaskQuery()// 创建任务查询对象
				.taskAssignee(assignee)// 指定个人任务查询，指定办理人
				.list();
		if (list != null && list.size() > 0) {
			System.out.println("------------list.size():" + list.size());

			for (Task task : list) {
				System.out.println("任务ID:" + task.getId());
				System.out.println("任务名称:" + task.getName());
				System.out.println("任务的创建时间:" + task.getCreateTime());
				System.out.println("任务的办理人:" + task.getAssignee());
				System.out.println("流程实例ID：" + task.getProcessInstanceId());
				System.out.println("执行对象ID:" + task.getExecutionId());
				System.out.println("流程定义ID:" + task.getProcessDefinitionId());
				System.out.println("########################################################");
			}
		}
	}

	/** 查询当前人的个人任务 */
	@Test
	public void completeAllTask() {

		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.createTaskQuery()// 创建任务查询对象
				.list();
		if (list != null && list.size() > 0) {
			System.out.println("------------list.size():" + list.size());

			for (Task task : list) {
				System.out.println("任务ID:" + task.getId());
				System.out.println("任务名称:" + task.getName());
				System.out.println("任务的创建时间:" + task.getCreateTime());
				System.out.println("任务的办理人:" + task.getAssignee());
				System.out.println("流程实例ID：" + task.getProcessInstanceId());
				System.out.println("执行对象ID:" + task.getExecutionId());
				System.out.println("流程定义ID:" + task.getProcessDefinitionId());
				System.out.println("########################################################");
			}

			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 设置流程变量day=3
			paramMap.put("day", 5);

			for (Task task : list) {

				processEngine.getTaskService()// 与正在执行的任务管理相关的Service
						.complete(task.getId(), paramMap);

			}

		}
	}

	/** 完成我的任务 */
	@Test
	public void completeMyPersonalTask() {
		// 任务ID
		String taskId = "2104";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 设置流程变量day=3
		paramMap.put("day", 5);
		// 提交任务的时候传入流程变量

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
//		.complete(taskId);
				.complete(taskId, paramMap);

		System.out.println("完成任务：任务ID：" + taskId);
	}

	@Test
	public void completeMyPersonalTaskById() {
		// 任务ID
		String taskId = "2811";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

		System.out.println("完成任务：任务ID：" + taskId);
	}

}
