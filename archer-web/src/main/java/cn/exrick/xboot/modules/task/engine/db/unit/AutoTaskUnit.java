package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import cn.exrick.xboot.modules.task.engine.db.IAutoTask;

/**
 * Created by feng on 2019/10/24
 */

public class AutoTaskUnit extends DBTaskUnit {

	protected IAutoTask autoTask;

	public AutoTaskUnit(String taskId){
		super(taskId);
		isControllNode = false;
	}


	@Override
	public ExecuteResult execute() {
		ExecuteResult result = ExecuteResult.FAIL;
		try{
			autoTask.run();
			result = ExecuteResult.SUCCESS;
		}catch (Exception e){
			throw new XbootException(e.getMessage());
		}

		return result;
	}
}
