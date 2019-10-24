package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;

/**
 * Created by feng on 2019/10/24
 */

public class UserTaskUnit extends DBTaskUnit {

	public UserTaskUnit(String taskId){
		super(taskId);
		isControllNode = false;
	}


	@Override
	public ExecuteResult execute() {
		return ExecuteResult.PENDING;
	}
}
