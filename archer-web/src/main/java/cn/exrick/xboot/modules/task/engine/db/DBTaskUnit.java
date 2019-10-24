package cn.exrick.xboot.modules.task.engine.db;


import cn.exrick.xboot.modules.task.engine.TaskUnit;

/**
 * Created by feng on 2019/9/5 0005
 * 基于数据库的任务单元
 */
public abstract class DBTaskUnit implements TaskUnit {

	protected String taskId;

	protected String type;

	protected String typeName;

	protected String typeDesp;

	protected boolean isControllNode;


	public DBTaskUnit(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}
}
