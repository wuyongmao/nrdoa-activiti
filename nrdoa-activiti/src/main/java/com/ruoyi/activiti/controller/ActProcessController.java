package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.ProcessDefinitionDto;
import com.ruoyi.activiti.service.ActProcessInstanceService;
import com.ruoyi.activiti.service.ActProcessService;
import com.ruoyi.activiti.utils.BeanHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 流程管理 操作处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/activiti/process")
public class ActProcessController extends BaseController {
	private String prefix = "activiti/process";

	@Autowired
	private ActProcessService actProcessService;

	@Autowired
	private ActProcessInstanceService actProcessInstanceService;

	@RequiresPermissions("activiti:process:view")
	@GetMapping
	public String process() {
		return prefix + "/process";
	}

	@RequiresPermissions("activiti:process:list")
	@PostMapping("list")
	@ResponseBody
	public TableDataInfo list(ProcessDefinitionDto processDefinitionDto) {
		return actProcessService.selectProcessDefinitionList(processDefinitionDto);
	}

	@GetMapping("/add")
	public String add() {
		return prefix + "/add";
	}

	@RequiresPermissions("activiti:process:add")
	@Log(title = "流程管理", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(@RequestParam String category, @RequestParam("file") MultipartFile file)
			throws IOException {
		InputStream fileInputStream = file.getInputStream();
		String fileName = file.getOriginalFilename();
		return actProcessService.saveNameDeplove(fileInputStream, fileName, category);
	}

	@RequiresPermissions("activiti:process:model")
	@GetMapping(value = "/convertToModel/{processId}")
	@ResponseBody
	public AjaxResult convertToModel(@PathVariable("processId") String processId) {
		try {
			Model model = actProcessService.convertToModel(processId);
			return success(StringUtils.format("转换模型成功，模型编号[{}]", model.getId()));
		} catch (Exception e) {
			return error("转换模型失败" + e.getMessage());
		}
	}

	/**
	 * 启动流程流程
	 *
	 * @param processDefinitionId
	 * @return
	 * @throws Exception
	 */
	@Log(title = "启动流程", businessType = BusinessType.UPDATE)
	@RequiresPermissions("activiti:process:start")
	@GetMapping("/startById/{processDefinitionId}")
	@ResponseBody
	public AjaxResult startProcessInstanceById(@PathVariable("processDefinitionId") String processDefinitionId) {
		BeanHelper.beanAttributeValueTrim(processDefinitionId);
		return actProcessService.startProcessInstanceById(processDefinitionId);
	}

	@GetMapping(value = "/resource/{imageName}/{deploymentId}")
	public void viewImage(@PathVariable("imageName") String imageName,
			@PathVariable("deploymentId") String deploymentId, HttpServletResponse response) {
		try {
			InputStream in = actProcessService.findImageStream(deploymentId, imageName);
			for (int bit = -1; (bit = in.read()) != -1;) {
				response.getOutputStream().write(bit);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequiresPermissions("activiti:process:remove")
	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		return toAjax(actProcessService.deleteProcessDefinitionByDeploymentIds(ids));
	}
}
