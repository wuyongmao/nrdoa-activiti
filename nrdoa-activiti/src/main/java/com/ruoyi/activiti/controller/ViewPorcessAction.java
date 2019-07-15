package com.ruoyi.activiti.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.activiti.imgUtil.AnimatedGifEncoder;
import com.ruoyi.activiti.imgUtil.Base64Utils;
import com.ruoyi.activiti.imgUtil.DefaultProcessDiagramCanvas;
import com.ruoyi.activiti.imgUtil.HMProcessDiagramGenerator;
import com.ruoyi.activiti.utils.MyProcessDiagramGenerator;

import sun.misc.BASE64Encoder;

@Controller
public class ViewPorcessAction {
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;
	@Autowired
	ProcessEngineFactoryBean processEngine;
	@Autowired
	HistoryService historyService;
	@Autowired
	TaskService taskService;


	/**
	 * 读取带跟踪的流程图片
	 * 
	 * @throws Exception
	 */
	@GetMapping("/tt/{processInstanceId}")
	public void traceImage(@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {
		// 获取历史流程实例
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// 获取流程图
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).list();
		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();
		// 高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		/*
		 * for(HistoricActivityInstance tempActivity : highLightedActivitList){ String
		 * activityId = tempActivity.getActivityId();
		 * highLightedActivitis.add(activityId); }
		 */
		// 当前流程实例执行到哪个节点
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(processInstanceId).singleResult();// 执行实例
		highLightedActivitis.add(execution.getActivityId());

		// 中文显示的是口口口，设置字体就好了
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,
				highLightedFlows, "宋体", "宋体", null, null, 1.0);
		// 单独返回流程图，不高亮显示
//        InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
		// 输出资源内容到相应对象
		byte[] b = new byte[1024];
		int len;
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}

	}

	/**
	 * 获取需要高亮的线
	 * 
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows2(ProcessDefinitionEntity processDefinitionEntity,
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

	/**
	 * 读取带跟踪的流程图片
	 * 
	 * @throws Exception
	 */
	@GetMapping("/tt2/{processInstanceId}")
	public void traceImage2(@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {

		try {
			// 获取历史流程实例
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();

			if (historicProcessInstance != null) {
				// 获取流程定义
				ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
						.getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

				// 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
				List<HistoricActivityInstance> historicActivityInstanceList = historyService
						.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
						.orderByHistoricActivityInstanceId().asc().list();

				// 已执行的节点ID集合
				List<String> executedActivityIdList = new ArrayList<String>();
				int index = 1;
				// 获取已经执行的节点ID
				for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
					executedActivityIdList.add(activityInstance.getActivityId());
					index++;
				}

				// 已执行的线集合
				List<String> flowIds = new ArrayList<String>();
				// 获取流程走过的线
				flowIds = getHighLightedFlows(processDefinition, historicActivityInstanceList);

				BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
				// 获取流程图图像字符流
				ProcessDiagramGenerator pec = new MyProcessDiagramGenerator();
				// 配置字体
				InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体",
						"微软雅黑", "黑体", null, 2.0);

				response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.close();
				imageStream.close();
			}
		} catch (Exception e) {
		}
	}

	 

	@GetMapping("/tt3/{processInstanceId}")
	public void traceImage3(@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {

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

		if (null != activeActivityIds) {
			InputStream imageStream = null;
			try {
				response.setContentType("image/png");

				// 获得流程引擎配置
				ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
				// 根据流程定义ID获得BpmnModel
				BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
				// 输出资源内容到相应对象
				imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds,
						highLightedFlows, "宋体", "宋体", "宋体", processEngineConfiguration.getClassLoader(), 1.0);

				int len;
				byte[] b = new byte[1024];

				while ((len = imageStream.read(b, 0, 1024)) != -1) {
					response.getOutputStream().write(b, 0, len);
				}
			} finally {
				if (imageStream != null) {
					imageStream.close();
				}
			}
		}
	}

	public boolean isFinished(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId)
				.count() > 0;
	}

	@GetMapping("/getShineProcImage")
	@ResponseBody
	public String getShineProcImage(HttpServletRequest request, HttpServletResponse resp, String processInstanceId)
			throws IOException {

		JSONObject result = new JSONObject();
		JSONArray shineProImages = new JSONArray();
		BASE64Encoder encoder = new BASE64Encoder();
		InputStream imageStream = generateStream(request, resp, processInstanceId, true);

		if (imageStream != null) {
			String imageCurrentNode = Base64Utils.ioToBase64(imageStream);
			if (StringUtils.isNotBlank(imageCurrentNode)) {
				shineProImages.add(imageCurrentNode);
			}
		}

		InputStream imageNoCurrentStream = generateStream(request, resp, processInstanceId, false);
		if (imageNoCurrentStream != null) {
			String imageNoCurrentNode = Base64Utils.ioToBase64(imageNoCurrentStream);
			if (StringUtils.isNotBlank(imageNoCurrentNode)) {
				shineProImages.add(imageNoCurrentNode);
			}
		}
		result.put("id", UUID.randomUUID().toString());
		result.put("errorNo", 0);
		result.put("images", shineProImages);
		return result.toJSONString();
	}

	// 只读图片页面
	@GetMapping("/shinePics/{processInstanceId}")
	public String shinePics(HttpServletRequest request, HttpServletResponse resp,
			@PathVariable String processInstanceId) {
		request.setAttribute("processInstanceId", processInstanceId);
		return "/activiti/processInstance/shinePics";
	}

	@GetMapping("/tt6/{processInstanceId}")
	public void getProcImage(HttpServletRequest request, HttpServletResponse resp,
			@PathVariable("processInstanceId") String processInstanceId) throws IOException {
		InputStream imageStream = generateStream(request, resp, processInstanceId, true);
		if (imageStream == null) {
			return;
		}
		InputStream imageNoCurrentStream = generateStream(request, resp, processInstanceId, false);
		if (imageNoCurrentStream == null) {
			return;
		}

		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.setTransparent(Color.BLACK);
		e.setRepeat(0);
		e.setQuality(19);
		e.start(resp.getOutputStream());

		BufferedImage current = ImageIO.read(imageStream); // 读入需要播放的jpg文件
		e.addFrame(current); // 添加到帧中

		e.setDelay(200); // 设置播放的延迟时间
		BufferedImage nocurrent = ImageIO.read(imageNoCurrentStream); // 读入需要播放的jpg文件
		e.addFrame(nocurrent); // 添加到帧中

		e.finish();

//        byte[] b = new byte[1024];
//        int len;
//        while ((len = imageStream.read(b, 0, 1024)) != -1) {
//            resp.getOutputStream().write(b, 0, len);
//        }
	}

	public InputStream generateStream(HttpServletRequest request, HttpServletResponse resp, String processInstanceId,
			boolean needCurrent) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		String processDefinitionId = null;
		List<String> executedActivityIdList = new ArrayList<String>();
		List<String> currentActivityIdList = new ArrayList<>();
		List<HistoricActivityInstance> historicActivityInstanceList = new ArrayList<>();
		if (processInstance != null) {
			processDefinitionId = processInstance.getProcessDefinitionId();
			if (needCurrent) {
				currentActivityIdList = this.runtimeService.getActiveActivityIds(processInstance.getId());
			}
		}
		if (historicProcessInstance != null) {
			processDefinitionId = historicProcessInstance.getProcessDefinitionId();
			historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
					.processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
			for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
				executedActivityIdList.add(activityInstance.getActivityId());
			}
		}

		if (StringUtils.isEmpty(processDefinitionId) || executedActivityIdList.isEmpty()) {
			return null;
		}

		// 高亮线路id集合
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, historicActivityInstanceList);

		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		// List<String> activeActivityIds =
		// runtimeService.getActiveActivityIds(processInstanceId);
		processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
		HMProcessDiagramGenerator diagramGenerator = (HMProcessDiagramGenerator) processEngineConfiguration
				.getProcessDiagramGenerator();
		// List<String> activeIds =
		// this.runtimeService.getActiveActivityIds(processInstance.getId());

		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList,
				highLightedFlows, processEngine.getProcessEngineConfiguration().getActivityFontName(),
				processEngine.getProcessEngineConfiguration().getLabelFontName(), "宋体", null, 1.0,
				currentActivityIdList);

		return imageStream;
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

	/**
	 * 获取各个节点的具体的信息
	 * 
	 * @param wfKey 流程定义的key
	 * @return
	 */
	@RequestMapping("/getProcessTrace")
	@ResponseBody
	public List<Map<String, Object>> getProcessTrace(String wfKey) throws Exception {
		List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(wfKey)
				.latestVersion().singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(pd.getId());
		List<ActivityImpl> activitiList = processDefinition.getActivities();
		InputStream xmlIs = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				processDefinition.getResourceName());
		BpmnModel bm = new BpmnXMLConverter().convertToBpmnModel(new InputStreamSource(xmlIs), false, true);
		Class<?> clazz = Class.forName("com.ruoyi.activiti.imgUtil.HMProcessDiagramGenerator");
		Method method = clazz.getDeclaredMethod("initProcessDiagramCanvas", BpmnModel.class);
		method.setAccessible(true);
		DefaultProcessDiagramCanvas pdc = (DefaultProcessDiagramCanvas) method.invoke(clazz.newInstance(), bm); // 调用方法

		clazz = Class.forName("org.activiti.engine.impl.bpmn.diagram.ProcessDiagramCanvas");
		Field minXField = clazz.getDeclaredField("minX"); // 得到minX字段
		Field minYField = clazz.getDeclaredField("minY");
		minXField.setAccessible(true);
		minYField.setAccessible(true);
		int minX = minXField.getInt(pdc);// 最小的x值
		int minY = minYField.getInt(pdc); // 最小的y的值

		minX = minX > 0 ? minX - 5 : 0;
		minY = minY > 0 ? minY - 5 : 0;
		for (ActivityImpl activity : activitiList) {
			Map<String, Object> activityInfo = new HashMap<String, Object>();
			activityInfo.put("width", activity.getWidth());
			activityInfo.put("height", activity.getHeight());
			activityInfo.put("x", activity.getX() - minX);
			activityInfo.put("y", activity.getY() - minY);
			activityInfo.put("actId", activity.getId());
			activityInfo.put("name", activity.getProperty("name"));
			activityInfos.add(activityInfo);
		}
		return activityInfos;
	}

}