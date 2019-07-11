package com.ruoyi.activiti.controller;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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

	@Autowired
	private ProcessEngine processEngine;

	private String prefix = "activiti/processInstance";

	@GetMapping("/tt")
	@ResponseBody
	public TableDataInfo getList(ProcessInstanceDto pid) {
		if (pid == null) {
			pid = new ProcessInstanceDto();
		}
		return actProcessInstanceService.getProcessInstanceByExample(pid);

	}

	@GetMapping("/activiti/processInstance")
	public String process() {
		return prefix + "/processInstance";
	}

	@RequiresPermissions("activiti:process:list")
	@PostMapping("/activiti/processInstance/list")
	@ResponseBody
	public TableDataInfo list(ProcessInstanceDto pid) {
		if (pid == null) {
			pid = new ProcessInstanceDto();
		}
		BeanHelper.beanAttributeValueTrim(pid);
		return actProcessInstanceService.getProcessInstanceByExample(pid);
//		return actProcessInstanceService.seleteRunningProcess(pid);
	}

	/**
	 * 查看当前流程状态图
	 * http://localhost:8888/activiti/processInstance/viewCurrentPic?processDefinitionId=nrdleave3:1:10006&processInstanceId=15001
	 * 
	 * @param processDefinitionId 流程定义Id
	 * @param processInstanceId   流程实例Id
	 * @return
	 */
	@RequestMapping("/viewCurrentPic")
	public String viewCurrentPicByDefIdInsId(String processDefinitionId, String processInstanceId) {
		return "activiti/processInstance/index.html";
	}

	/**
	 * 启动流程流程
	 *
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	@Log(title = "启动流程实例", businessType = BusinessType.UPDATE)
	@RequiresPermissions("activiti:processInstance:start")
	@GetMapping("/activiti/processInstance/startById/{processInstanceId}")
	@ResponseBody
	public AjaxResult startProcessInstanceById(@PathVariable("processInstanceId") String processInstanceId) {
		return actProcessInstanceService.startProcessInstanceById(processInstanceId);
	}

	/**
	 * 启动流程流程
	 *
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	@Log(title = "挂起流程实例", businessType = BusinessType.UPDATE)
	@RequiresPermissions("activiti:processInstance:stop")
	@GetMapping("/activiti/processInstance/stopById/{processInstanceId}")
	@ResponseBody
	public AjaxResult stopProcessInstanceById(@PathVariable("processInstanceId") String processInstanceId) {
		return actProcessInstanceService.stopProcessInstanceById(processInstanceId);
	}

	@RequiresPermissions("activiti:processInstance:start")

	@Log(title = "删除流程实例", businessType = BusinessType.UPDATE)
	@GetMapping("/activiti/processInstance/startByKey/{processDefinitionKey}")
	@ResponseBody
	public AjaxResult startProcessInstanceByKey(@PathVariable("processDefinitionKey") String processDefinitionKey) {
		return actProcessInstanceService.startProcessInstanceByKey(processDefinitionKey);
	}

	@RequiresPermissions("activiti:processInstance:completeTask")

	@Log(title = "办理流程实例", businessType = BusinessType.UPDATE)
	@GetMapping("/activiti/processInstance/completeTask/{taskId}")
	@ResponseBody
	public AjaxResult completeTaskById(@PathVariable("taskId") String taskId) {
		return actProcessInstanceService.completeTaskById(taskId);
	}

	@RequiresPermissions("activiti:processInstance:remove")

	@Log(title = "删除流程实例", businessType = BusinessType.UPDATE)
	@GetMapping("/activiti/processInstance/remove/{processInstanceId}")
	@ResponseBody
	public AjaxResult removeProcessInstanceById(@PathVariable("processInstanceId") String processInstanceId) {
		try {
			actProcessInstanceService.deleteProcessInstance(processInstanceId, "测试");
		} catch (Exception e) {
			return AjaxResult.error(e.getMessage());
		}
		return AjaxResult.success();
	}

	/**
	 * 读取带跟踪的流程图片
	 * 
	 * @throws Exception
	 */
	@GetMapping("/traceImage/{processInstanceId}")
	public void traceImage(@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {
	}

}
