package com.ruoyi.activiti.utils;

import java.util.Iterator;
import java.util.Map;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * 
 * 节点的跳转
 */
public class NodeJumpTaskCmd implements Command<Void> {

	protected String executionId;// 执行ID
	protected ActivityImpl desActivity; // 目标引擎对象
	protected Map<String, Object> paramvar;// 变量
	protected ActivityImpl currentActivity;// 当前引擎对象
	protected String deleteReason = "completed";

	/**
	 * 构造参数 可以根据自己的业务需要添加更多的字段
	 * 
	 * @param executionId
	 * @param desActivity
	 * @param paramvar
	 * @param currentActivity
	 */
	public NodeJumpTaskCmd(String executionId, ActivityImpl desActivity, Map<String, Object> paramvar,
			ActivityImpl currentActivity) {
		this.executionId = executionId;
		this.desActivity = desActivity;
		this.paramvar = paramvar;
		this.currentActivity = currentActivity;
	}

	public NodeJumpTaskCmd(String executionId, ActivityImpl desActivity, Map<String, Object> paramvar,
			ActivityImpl currentActivity, String deleteReason) {
		this.executionId = executionId;
		this.desActivity = desActivity;
		this.paramvar = paramvar;
		this.currentActivity = currentActivity;
		this.deleteReason = deleteReason;
	}

	/**
	 * 获取执行实体管理 获取当前的任务执行对象 设置对应的值 获取当前的任务 删除 执行目标工作流
	 */
	public Void execute(CommandContext commandContext) {
		// 获取执行实体管理
		ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
		// 根据ExecutionID 查找执行实体 获取当前流程的executionId，因为在并发的情况下executionId是唯一的。
		ExecutionEntity executionEntity = executionEntityManager.findExecutionById(executionId);
		executionEntity.setVariables(paramvar);
		executionEntity.setEventSource(this.currentActivity);
		executionEntity.setActivity(this.currentActivity);
		// 获取当前ExecutionID的任务 根据executionId 获取Task
		Iterator<TaskEntity> localIterator = commandContext.getTaskEntityManager()
				.findTasksByExecutionId(this.executionId).iterator();
		while (localIterator.hasNext()) {
			TaskEntity taskEntity = (TaskEntity) localIterator.next();
			// 触发任务监听
			taskEntity.fireEvent("complete");
			// 删除任务的原因
			commandContext.getTaskEntityManager().deleteTask(taskEntity, this.deleteReason, false);
		}
		executionEntity.executeActivity(this.desActivity);
		System.out.println();
		return null;
	}
}
