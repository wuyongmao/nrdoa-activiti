package com.ruoyi.activiti.domain;

import java.util.Map;

import org.activiti.engine.repository.Deployment;
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
	 * 流程编号
	 */
	private String processId;

	/**
	 * 流程名称
	 */
	private String name;

	private String processDefinitionName;
	private String processDefinitionId;
	private String processDefinitionKey;
	private Integer processDefinitionVersion;
	private String businessKey;

	/**
	 * 部署编号
	 */
	private String deploymentId;

	private Boolean suspended;
	private Boolean latest;
	private String tenantId;
	private String startableByUser;

	/**
	 * 描述信息
	 */
	private String description;

	private Map<String, Object> processVariables;

	public ProcessInstanceDto(Deployment processDefinition) {
		this.setProcessId(processDefinition.getId());
		this.name = processDefinition.getName();
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

	public ProcessInstanceDto(ProcessInstance processInstance) {

		this.processDefinitionName = processInstance.getProcessDefinitionName();
		this.processDefinitionId = processInstance.getProcessDefinitionId();
		this.processDefinitionKey = processInstance.getProcessDefinitionKey();
		this.processDefinitionVersion = processInstance.getProcessDefinitionVersion();
		this.businessKey = processInstance.getBusinessKey();
		this.processVariables = processInstance.getProcessVariables();
		this.setProcessId(processInstance.getId());
		this.name = processInstance.getName();
		this.deploymentId = processInstance.getDeploymentId();
		this.description = processInstance.getDescription();
		this.suspended = processInstance.isSuspended();
	}

	public ProcessInstanceDto() {

	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
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
