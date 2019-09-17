package cn.exrick.xboot.task.engine.memory;


import cn.exrick.xboot.task.engine.TaskUnit;

/**
 * Created by feng on 2019/9/5 0005
 * 基于内存的任务单元，测试或非数据库任务使用
 */
public abstract class MemoryTaskUnit implements TaskUnit {

	private String taskName;

	public MemoryTaskUnit(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

}
