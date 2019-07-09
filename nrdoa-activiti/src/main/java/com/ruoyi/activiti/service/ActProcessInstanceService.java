package com.ruoyi.activiti.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.activiti.domain.ProcessInstanceDto;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 流程实例 服务层
 * 
 * @author ruoyi
 */
public interface ActProcessInstanceService {

	/**
	 * 启动流程
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public AjaxResult startProcessInstanceById(String processDefinitionId);

	public AjaxResult startProcessInstanceByKey(String processDefinitionKey);

	
	/**
	 * 运行中流程
	 * @param processDefinitionDto
	 * @return
	 */
	public TableDataInfo seleteRunningProcess(ProcessInstanceDto processInstanceDto);

	
	/**
	 * 运行流程查看
	 * @param pid
	 * @return
	 */
	TableDataInfo  getProcessInstanceByExample(  ProcessInstanceDto pid);
	
	
	
}
