package com.ruoyi.activiti.service;

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
	 * 启动挂起流程
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public AjaxResult startProcessInstanceById(String processInstanceId);

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
	
	
	public void deleteProcessInstance(String processInstanceId,String deleteReason);

	public AjaxResult stopProcessInstanceById(String processInstanceId);

	public AjaxResult completeTaskById(String taskId);
	

}
