package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;

/**
 * Created by feng on 2019/10/24
 */

public class UserTaskUnit extends DBTaskUnit {

	private String operationTag;

	public UserTaskUnit(String unitId, String type, String typeName, String typeDesp, String operationTag) {
		super(unitId, type, typeName, typeDesp, false);
		this.operationTag = operationTag;
	}

	@Override
	public ExecuteResult execute() {
		return ExecuteResult.PENDING;
	}
}
