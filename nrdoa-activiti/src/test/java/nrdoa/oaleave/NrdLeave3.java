package nrdoa.oaleave;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.ruoyi.activiti.utils.NodeJumpTaskCmd;

/*
 * 并行网关  nrdleave3.bpmn
 */

public class NrdLeave3 {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	/** 部署流程定义（从zip） */
	@Test
	public void deploymentProcessDefinition_zip() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/nrdleave3.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象
				.name("流程定义")// 添加部署的名称
				.addZipInputStream(zipInputStream)// 指定zip格式的文件完成部署
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
				//.deploy();// 完成部署
 ;
		System.out.println("部署ID：" + deployment.getId());// 5001
		System.out.println("部署名称：" + deployment.getName());// 请假申请3
	}

	@Test
	public void findProcessDefinition(){
		RepositoryService repositoryService=processEngine.getRepositoryService();
		
		
		List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
						.createProcessDefinitionQuery()//创建一个流程定义的查询
						/**指定查询条件,where条件*/
//						.deploymentId(deploymentId)//使用部署对象ID查询
//						.processDefinitionId(processDefinitionId)//使用流程定义ID查询
//						.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
//						.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询
						
						/**排序*/
						.orderByProcessDefinitionVersion().asc()//按照版本的升序排列
//						.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列
						
						/**返回的结果集*/
						.list();//返回一个集合列表，封装流程定义
//						.singleResult();//返回惟一结果集
//						.count();//返回结果集数量
//						.listPage(firstResult, maxResults);//分页查询
		if(list!=null && list.size()>0){
			for(ProcessDefinition pd:list){
//				repositoryService.suspendProcessDefinitionById(pd.getId());
				repositoryService.activateProcessDefinitionById(pd.getId());

				
				
				System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数
				System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
				System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
				System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
				System.out.println("资源名称bpmn文件:"+pd.getResourceName());
				System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
				System.out.println("部署对象ID："+pd.getDeploymentId());
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
		//        nrdleave3:2:17505  nrdleave3:1:10006
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
	
	
}
