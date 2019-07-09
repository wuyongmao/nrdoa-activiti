package com.ruoyi.activiti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.activiti.domain.ProcessInstanceDto;
import com.ruoyi.activiti.service.ActProcessInstanceService;
import com.ruoyi.activiti.utils.BeanHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 流程管理 操作处理
 * 
 * @author ruoyi
 */
@Controller
public class ActProcessInstanceController extends BaseController {

	@Autowired
	private ActProcessInstanceService actProcessInstanceService;
	private String prefix = "activiti/processInstance";

	@GetMapping("/tt")
	@ResponseBody
	public TableDataInfo getList(ProcessInstanceDto pid) {
		if(pid==null) {
			pid =new ProcessInstanceDto();
		}
		return  actProcessInstanceService.getProcessInstanceByExample(pid);
 
	}

	@GetMapping("/activiti/processInstance")
	public String process() {
		return prefix + "/processInstance";
	}

//	@RequiresPermissions("activiti:process:list")
	@PostMapping("/activiti/processInstance/list")
	@ResponseBody
	public TableDataInfo list(ProcessInstanceDto pid) {
		if(pid==null) {
			pid =new ProcessInstanceDto();
		}
		BeanHelper.beanAttributeValueTrim(pid);
		return actProcessInstanceService.getProcessInstanceByExample(pid);
//		return actProcessInstanceService.seleteRunningProcess(pid);
	}

	/**
	 * 查看当前流程状态图
	 * http://localhost:8888/Hi?processDefinitionId=nrdleave3:1:10006&processInstanceId=15001
	 * 
	 * @param processDefinitionId 流程定义Id
	 * @param processInstanceId   流程实例Id
	 * @return
	 */
	@RequestMapping("/activiti/processInstance/")
	public String viewCurrentPicBy(String processDefinitionId, String processInstanceId) {
		return "activiti/processInstance/index.html";
	}

	/**
	 * 启动流程流程
	 *
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	@Log(title = "启动流程", businessType = BusinessType.UPDATE)
//    @RequiresPermissions("activiti:model:deploy")
//  @RequiresPermissions("activiti:processInstance:start")

	@GetMapping("/activiti/processInstance/startById/{processDefinitionId}")
	@ResponseBody
	public AjaxResult startProcessInstanceById(@PathVariable("processDefinitionId") String processDefinitionId) {
		return actProcessInstanceService.startProcessInstanceById(processDefinitionId);
	}

	@Log(title = "启动流程", businessType = BusinessType.UPDATE)
	@GetMapping("/activiti/processInstance/startByKey/{processDefinitionKey}")
	@ResponseBody
	public AjaxResult startProcessInstanceByKey(@PathVariable("processDefinitionKey") String processDefinitionKey) {
		return actProcessInstanceService.startProcessInstanceByKey(processDefinitionKey);
	}

}
