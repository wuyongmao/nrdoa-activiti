package com.ruoyi.activiti.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.activiti.domain.ProcessInstanceDto;

public interface ProcessInstanceMapper {

	/**
	 * 运行流程查看
	 * @param pid
	 * @return
	 */
	List<Map<String,Object>> getProcessInstanceByExample(@Param("pid") ProcessInstanceDto pid);
	
	
	Long getProcessInstanceByExampleCount(@Param("pid") ProcessInstanceDto pid);
	
	
}
