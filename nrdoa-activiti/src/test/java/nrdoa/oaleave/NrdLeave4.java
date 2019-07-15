package nrdoa.oaleave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.ruoyi.activiti.utils.MyProcessDiagramGenerator;
import com.ruoyi.activiti.utils.NodeJumpTaskCmd;

/*
 * 并行网关  nrdleave3.bpmn
 */

public class NrdLeave4 {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义（从zip） */
	@Test
	public void deploymentProcessDefinition_zip() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/daling.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("流程定义")// 添加部署的名称
				.addZipInputStream(zipInputStream)// 指定zip格式的文件完成部署 70001
				.deploy();// 完成部署
		System.out.println("部署ID：" + deployment.getId());//
		System.out.println("部署名称：" + deployment.getName());//
	}

	/** 1. 部署流程定义 */
	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("请假申请3")// 添加部署的名称
				.addClasspathResource("diagrams/nrdleave3.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/nrdleave3.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy()
		// .deploy();// 完成部署
		;
		System.out.println("部署ID：" + deployment.getId());// 5001
		System.out.println("部署名称：" + deployment.getName());// 请假申请3
	}

	@Test
	public void findProcessDefinition() {
		RepositoryService repositoryService = processEngine.getRepositoryService();

		List<ProcessDefinition> list = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createProcessDefinitionQuery()// 创建一个流程定义的查询
				/** 指定查询条件,where条件 */
//						.deploymentId(deploymentId)//使用部署对象ID查询
//						.processDefinitionId(processDefinitionId)//使用流程定义ID查询
//						.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
//						.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

				/** 排序 */
				.orderByProcessDefinitionVersion().asc()// 按照版本的升序排列
//						.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

				/** 返回的结果集 */
				.list();// 返回一个集合列表，封装流程定义
//						.singleResult();//返回惟一结果集
//						.count();//返回结果集数量
//						.listPage(firstResult, maxResults);//分页查询
		if (list != null && list.size() > 0) {
			for (ProcessDefinition pd : list) {
//				repositoryService.suspendProcessDefinitionById(pd.getId());
				repositoryService.activateProcessDefinitionById(pd.getId());

				System.out.println("流程定义ID:" + pd.getId());// 流程定义的key+版本+随机生成数
				System.out.println("流程定义的名称:" + pd.getName());// 对应helloworld.bpmn文件中的name属性值
				System.out.println("流程定义的key:" + pd.getKey());// 对应helloworld.bpmn文件中的id属性值
				System.out.println("流程定义的版本:" + pd.getVersion());// 当流程定义的key值相同的相同下，版本升级，默认1
				System.out.println("资源名称bpmn文件:" + pd.getResourceName());
				System.out.println("资源名称png文件:" + pd.getDiagramResourceName());
				System.out.println("部署对象ID：" + pd.getDeploymentId());
				System.out.println("#########################################################");

			}
		}
	}

	/** 2. 启动流程实例 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "nrdleave3";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceById("nrdleave3:3:52504");
		// nrdleave3:2:17505 nrdleave3:1:10006
//		 .startProcessInstanceByKey(processDefinitionKey);//
		// 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());// 流程实例ID 7501
		System.out.println("ID:" + pi.getProcessInstanceId());// 流程实例ID 2501
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());// 流程定义ID nrdleave3:1:5004

	}

	/** 3.2 完成任务 */
	@Test
	public void completeMyPersonalTaskParam2() {
		/**
		 * ACT_RU_TASK
		 */
		// 任务ID 2501
		String taskId = "110009";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

//		processEngine.getTaskService().complete("72527");
	}

	/**
	 * 查看流程图
	 * 
	 * @throws IOException
	 */
	@Test
	public void viewPic() throws IOException {
		/** 将生成图片放到文件夹下 */
		String deploymentId = "201";
		// 获取图片资源名称
		List<String> list = processEngine.getRepositoryService()//
				.getDeploymentResourceNames(deploymentId);
		// 定义图片资源的名称
		String resourceName = "";
		if (list != null && list.size() > 0) {
			for (String name : list) {
				if (name.indexOf(".png") >= 0) {
					resourceName = name;
				}
			}
		}

		// 获取图片的输入流
		InputStream in = processEngine.getRepositoryService()//
				.getResourceAsStream(deploymentId, resourceName);

		// 将图片生成到D盘的目录下
		File file = new File("/home/yongmaow/图片/activitipt/" + resourceName);
		// 将输入流的图片写到D盘下
		FileUtils.copyInputStreamToFile(in, file);
	}

	@Test
	public void viewPic2() throws IOException {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		TaskService taskService = processEngine.getTaskService();
		RepositoryService repositoryService = processEngine.getRepositoryService();
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("nrdleave3");
//		taskService.complete(taskService.createTaskQuery().singleResult().getId());
		HistoryService historyService = processEngine.getHistoryService();
// 得到流程定义实体类
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition("daling:1:70004");
//		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery()
//				.processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();

//得到流程执行对象
		List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId("102501").list();
//得到正在执行的Activity的Id
		List<String> activityIds = new ArrayList<String>();
		for (Execution exe : executions) {
			List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
			activityIds.addAll(ids);
		}
		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId("102501").list();

		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId("102501").singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
		ProcessDiagramGenerator pec = new MyProcessDiagramGenerator();
		List<String> highLightedFlows = getHighLightedFlows(pde, highLightedActivitList);

		InputStream in = pec.generateDiagram(bpmnModel, "png", activityIds, highLightedFlows);

		FileOutputStream out = new FileOutputStream("/home/yongmaow/图片/ab.png");
		FileCopyUtils.copy(in, out);
	}

	@Test
	public void showPic3() throws Exception {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		TaskService taskService = processEngine.getTaskService();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		HistoryService historyService = processEngine.getHistoryService();

		String processInstanceId = "112501";
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		String procDefId = processInstance.getProcessDefinitionId();

		// 当前活动节点、活动线
		List<String> activeActivityIds = new ArrayList<>(), highLightedFlows;
		// 所有的历史活动节点
		List<String> highLightedFinishes = new ArrayList<>();

		// 如果流程已经结束，则得到结束节点
		if (!isFinished(processInstanceId)) {
			// 如果流程没有结束，则取当前活动节点
			// 根据流程实例ID获得当前处于活动状态的ActivityId合集
			activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
		}

		// 获得历史活动记录实体（通过启动时间正序排序，不然有的线可以绘制不出来）
		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			highLightedFinishes.add(historicActivityInstance.getActivityId());
		}
		// 计算活动线
		highLightedFlows = getHighLightedFlows((ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(procDefId), historicActivityInstances);

		// 获得流程引擎配置
		ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		// 根据流程定义ID获得BpmnModel
		BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
		// 输出资源内容到相应对象
		InputStream		imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds,
				highLightedFlows, "宋体", "宋体", "宋体", processEngineConfiguration.getClassLoader(), 1.0);
		FileOutputStream out = new FileOutputStream("/home/yongmaow/图片/abc.png");
		FileCopyUtils.copy(imageStream, out);
	}

	/**
	 * 节点跳转
	 * 
	 * @throws IOException
	 */
	@Test
	public void Jump() throws IOException {
		Map<String, Object> vars = new HashMap<String, Object>();
		// String[] v = { "张三", "李四", "王五"};
		// vars.put("assigneeList", Arrays.asList(v));
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition processDefinitionEntity = (ReadOnlyProcessDefinition) repositoryService
				.getProcessDefinition("nrdleave3:3:52504");
		// 目标节点
		ActivityImpl destinationActivity = (ActivityImpl) processDefinitionEntity.findActivity("userTask1");
		String executionId = "65001";
		// 当前节点
		ActivityImpl currentActivity = (ActivityImpl) processDefinitionEntity.findActivity("userTask1");
		processEngine.getManagementService()
				.executeCommand(new NodeJumpTaskCmd(executionId, destinationActivity, vars, currentActivity));
	}

	/**
	 * usertask3--->usertask5
	 */
	@Test
	public void Jump2() {
		Map<String, Object> vars = new HashMap<String, Object>();
		String[] v = { "shareniu1", "shareniu2", "shareniu3", "shareniu4" };
		vars.put("assigneeList", Arrays.asList(v));
		// 分享牛原创(尊重原创 转载对的时候第一行请注明，转载出处来自分享牛http://blog.csdn.net/qq_30739519)
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition processDefinitionEntity = (ReadOnlyProcessDefinition) repositoryService
				.getProcessDefinition("daling:1:70004");
		// 目标节点
		ActivityImpl destinationActivity = (ActivityImpl) processDefinitionEntity.findActivity("usertask5");
		String executionId = "72532";
		// 当前节点
		ActivityImpl currentActivity = (ActivityImpl) processDefinitionEntity.findActivity("usertask3");

		// taskId, executionId, parentId
		processEngine.getManagementService().executeCommand(
				new JDJumpTaskCmd("72538", executionId, null, destinationActivity, vars, currentActivity));

	}

	/**
	 * 节点跳转
	 * 
	 * @throws IOException
	 */
	@Test
	public void Jump3() throws IOException {
		Map<String, Object> vars = new HashMap<String, Object>();
		String[] v = { "shareniu3", "shareniu4" };
		vars.put("assigneeList", Arrays.asList(v));
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition processDefinitionEntity = (ReadOnlyProcessDefinition) repositoryService
				.getProcessDefinition("daling:1:70004");
		// 目标节点
		ActivityImpl destinationActivity = (ActivityImpl) processDefinitionEntity.findActivity("usertask5");
		String executionId = "72547";
		// 当前节点
		ActivityImpl currentActivity = (ActivityImpl) processDefinitionEntity.findActivity("usertask2");
		processEngine.getManagementService()
				.executeCommand(new NodeJumpTaskCmd(executionId, destinationActivity, vars, currentActivity));
	}

	/**
	 * 获取需要高亮的线
	 * 
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {
		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
			ActivityImpl activityImpl = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i).getActivityId());// 得到节点定义的详细信息
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
			ActivityImpl sameActivityImpl1 = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i + 1).getActivityId());
			// 将后面第一个节点放在时间相同节点的集合里
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
				if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
					// 如果第一个节点和第二个节点开始时间相同保存
					ActivityImpl sameActivityImpl2 = processDefinitionEntity
							.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {
					// 有不相同跳出循环
					break;
				}
			}
			List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
			for (PvmTransition pvmTransition : pvmTransitions) {
				// 对所有的线进行遍历
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
				// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	public boolean isFinished(String processInstanceId) {
		HistoryService historyService = processEngine.getHistoryService();

		return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId)
				.count() > 0;
	}
}
