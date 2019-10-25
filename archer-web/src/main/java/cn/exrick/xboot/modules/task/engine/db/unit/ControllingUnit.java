package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;

/**
 * Created by feng on 2019/10/24
 */

public class ControllingUnit extends DBTaskUnit {

	public ControllingUnit(String unitId, String type, String typeName, String typeDesp) {
		super(unitId, type, typeName, typeDesp, true);
	}

	@Override
	public ExecuteResult execute() {
		return ExecuteResult.SUCCESS;
	}
}
