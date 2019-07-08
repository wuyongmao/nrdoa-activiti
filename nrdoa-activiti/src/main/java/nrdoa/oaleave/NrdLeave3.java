package nrdoa.oaleave;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/*
 * 并行网关  nrdleave3.bpmn
 */

public class NrdLeave3 {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 1. 部署流程定义 */
	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("请假申请3")// 添加部署的名称
				.addClasspathResource("diagrams/nrdleave3.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/nrdleave3.png")// 从classpath的资源中加载，一次只能加载一个文件
			.deploy()
				//.deploy();// 完成部署
 ;
		System.out.println("部署ID：" + deployment.getId());// 5001
		System.out.println("部署名称：" + deployment.getName());// 请假申请3
	}

	/** 2. 启动流程实例 */
	@Test
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "nrdleave3";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceById("nrdleave3:2:17505");
		//        nrdleave3:2:17505  nrdleave3:1:10006
		// .startProcessInstanceByKey("");//
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
		String taskId = "5305";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

		System.out.println("完成任务：任务ID：" + taskId);
		taskId = "5307";

		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);

		System.out.println("完成任务：任务ID：" + taskId);
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

}
