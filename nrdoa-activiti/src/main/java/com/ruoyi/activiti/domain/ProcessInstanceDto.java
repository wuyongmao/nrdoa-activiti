package com.ruoyi.activiti.domain;

import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import com.ruoyi.common.core.domain.BaseActDto;

/**
 * 流程实体数据对象
 * 
 * @author ruoyi
 */
public class ProcessInstanceDto extends BaseActDto {
	private static final long serialVersionUID = 1L;

	/**
	 * 流程名称
	 */
	private String name;
    private Integer suspensionState;  //实例状态 1 2 
	private String processDefinitionName;
	private String processDefinitionId;
	private String processDefinitionKey;
	private Integer processDefinitionVersion;
	private String businessKey;

	private Map<String, Object> processVariables;

	private String activityId;
	/**
	 * 部署编号
	 */
	private String deploymentId;

	private Boolean suspended;
	private Boolean latest;
	private String tenantId;
	private String startableByUser;
	/**
	 * 资源文件名称
	 */
	private String resourceName;

	/**
	 * 描述信息
	 */
	private String description;
	/**
	 * 图片资源文件名称
	 */
	private String diagramResourceName;

	private String procInstId;

	private String processInstanceId;

	private String taskId;
	private String startUserId;
	private String actName;
	private String assignee;
	
	
	public Integer getSuspensionState() {
		return suspensionState;
	}

	public void setSuspensionState(Integer suspensionState) {
		this.suspensionState = suspensionState;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getStartUserId() {
		return startUserId;
	}

	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}

	public String getActName() {
		return actName;
	}

	public void setActName(String actName) {
		this.actName = actName;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDiagramResourceName() {
		return diagramResourceName;
	}

	public void setDiagramResourceName(String diagramResourceName) {
		this.diagramResourceName = diagramResourceName;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}



	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public Integer getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}

	public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public Map<String, Object> getProcessVariables() {
		return processVariables;
	}

	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ProcessInstanceDto(ProcessDefinition processDefinition) {
		this.resourceName = processDefinition.getResourceName();
		this.diagramResourceName = processDefinition.getDiagramResourceName();
	}

	public ProcessInstanceDto(ProcessInstance pi) {

		this.processInstanceId = pi.getProcessInstanceId();
		this.activityId = pi.getActivityId();

		this.processDefinitionName = pi.getProcessDefinitionName();
		this.processDefinitionId = pi.getProcessDefinitionId();
		this.processDefinitionKey = pi.getProcessDefinitionKey();
		this.processDefinitionVersion = pi.getProcessDefinitionVersion();
		this.businessKey = pi.getBusinessKey();
		this.processVariables = pi.getProcessVariables();
		this.name = pi.getName();
		this.deploymentId = pi.getDeploymentId();
		this.description = pi.getDescription();
		this.suspended = pi.isSuspended();
	}

	public ProcessInstanceDto() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	public Boolean getLatest() {
		return latest;
	}

	public void setLatest(Boolean latest) {
		this.latest = latest;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getStartableByUser() {
		return startableByUser;
	}

	public void setStartableByUser(String startableByUser) {
		this.startableByUser = startableByUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
