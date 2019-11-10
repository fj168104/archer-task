package cn.exrick.xboot.modules.task.engine.db;


import cn.exrick.xboot.modules.task.engine.TaskUnit;
import lombok.Data;

/**
 * Created by feng on 2019/9/5 0005
 * 基于数据库的任务单元
 */
@Data
public abstract class DBTaskUnit implements TaskUnit {

	protected String unitId;

	protected String type;

	protected String typeName;

	protected String typeDesp;

	protected boolean isControllNode;

	public DBTaskUnit(String unitId, String type, String typeName, String typeDesp, boolean isControllNode) {
		this.unitId = unitId;
		this.type = type;
		this.typeName = typeName;
		this.typeDesp = typeDesp;
		this.isControllNode = isControllNode;
	}

	@Override
	public String getUnitId() {
		return unitId;
	}

	public String getNodeName(){
		return isControllNode? typeName:typeDesp;
	}

}
