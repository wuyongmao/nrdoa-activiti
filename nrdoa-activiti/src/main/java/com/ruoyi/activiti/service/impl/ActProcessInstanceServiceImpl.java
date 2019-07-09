package com.ruoyi.activiti.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.rest.editor.model.ModelEditorJsonRestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.activiti.domain.ProcessInstanceDto;
import com.ruoyi.activiti.mapper.ProcessInstanceMapper;
import com.ruoyi.activiti.service.ActProcessInstanceService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;

@Service
public class ActProcessInstanceServiceImpl implements ActProcessInstanceService {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);

	@Autowired
	private ProcessInstanceMapper processInstanceMapper;

	@Autowired
	private RuntimeService runtimeService;

	@Override
	public AjaxResult startProcessInstanceById(String processDefinitionId) {
		ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinitionId);
  
		AjaxResult a = AjaxResult.success("操作成功");
		a.add("processInstacne", pi);
		if (pi != null) {
			return a;
		} else {

			return AjaxResult.error("流程不存在");

		}

	}

	@Override
	public AjaxResult startProcessInstanceByKey(String processDefinitionKey) {
		// TODO Auto-generated method stub
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		AjaxResult a = AjaxResult.success("操作成功");
		a.add("processInstacne", pi);
		if (pi != null) {
			return a;
		} else {
			return AjaxResult.error("流程不存在");

		}

	}

	@Override
	public TableDataInfo seleteRunningProcess(ProcessInstanceDto processInstanceDto) {
		ProcessInstanceQuery pq = runtimeService.createProcessInstanceQuery();

		TableDataInfo data = new TableDataInfo();

		if (StringUtils.isNotEmpty(processInstanceDto.getDeploymentId())) {

			pq.deploymentId(processInstanceDto.getDeploymentId());
		}
		if (StringUtils.isNotEmpty(processInstanceDto.getProcessDefinitionName())) {
			pq.processInstanceNameLike(processInstanceDto.getProcessDefinitionName());
//			pq.processDefinitionName(processInstanceDto.getProcessDefinitionName());
		}

		data.setTotal(pq.count());

//		List<ProcessInstance> processInstances = pq.orderByProcessInstanceId().desc()
//				.listPage(processInstanceDto.getPageNum(), processInstanceDto.getPageSize());
//		List<Map<String, Object>> listMap = new ArrayList<>();
//		String[] ps = { "id", "name", "processDefinitionName", "processDefinitionId", "processDefinitionKey",
//				"processDefinitionVersion", "processInstanceId", "businessKey", "deploymentId", "suspended",
//				"description" };
//		for (ProcessInstance pi : processInstances) {
//			Map<String, Object> map = CommUtil.obj2map(pi, ps);
//			listMap.add(map);
//		}

		data.setRows(pq.orderByProcessInstanceId().desc()
				.listPage(processInstanceDto.getPageNum(), processInstanceDto.getPageSize()).stream()
				.map(ProcessInstanceDto::new).collect(Collectors.toList()));
//		Stream<ProcessInstance>  aa=
//		pq.orderByProcessInstanceId().desc()
//		.listPage(processInstanceDto.getPageNum(), processInstanceDto.getPageSize()).stream()
////		.map(null).collect(null)
//		;

//		List<ProcessInstanceDto> p= (List<ProcessInstanceDto>) data.getRows();

		return data;

	}

	@Override
	public TableDataInfo getProcessInstanceByExample(ProcessInstanceDto pid) {
		long total = processInstanceMapper.getProcessInstanceByExampleCount(pid);

		List<Map<String, Object>> lms = processInstanceMapper.getProcessInstanceByExample(pid);
		TableDataInfo data = new TableDataInfo();

		data.setTotal(total);
		data.setRows(lms);

		return data;

	}

	@Override
	public void deleteProcessInstance(String processInstanceId, String deleteReason) {
		// TODO Auto-generated method stub
		runtimeService.deleteProcessInstance(processInstanceId, deleteReason);

	}

}
