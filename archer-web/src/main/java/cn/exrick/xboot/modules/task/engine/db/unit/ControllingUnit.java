package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;

/**
 * Created by feng on 2019/10/24
 */

public class ControllingUnit extends DBTaskUnit {

	public ControllingUnit(String taskId){
		super(taskId);
		isControllNode = true;
	}


	@Override
	public ExecuteResult execute() {
		return ExecuteResult.SUCCESS;
	}
}
