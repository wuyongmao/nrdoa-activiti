package com.ruoyi.web.controller.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ruoyi.activiti.service.ActProcessInstanceService;
import com.ruoyi.common.core.controller.BaseController;

/**
 * 流程管理 操作处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/activiti/diagramView")
public class ActDiagramViewController extends BaseController {
	
	@Autowired
	private ActProcessInstanceService actProcessInstanceService;
	
	
	
	@RequestMapping("/Hi")
	public String sayHello() {
		return "diagram-viewer/index.html";
	}
	
	
	
	
	
	
	

}
