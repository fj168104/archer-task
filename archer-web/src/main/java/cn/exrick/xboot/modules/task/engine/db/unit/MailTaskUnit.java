package cn.exrick.xboot.modules.task.engine.db.unit;

import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by feng on 2019/10/24
 */
@Slf4j
public class MailTaskUnit extends DBTaskUnit {

	private String to;

	private String cc;

	private String from;

	public MailTaskUnit(String unitId, String type, String typeName, String typeDesp, String to, String cc, String from) {
		super(unitId, type, typeName, typeDesp, false);
	}


	@Override
	public ExecuteResult execute() {
		log.info("mail send nodeId={}", unitId);
		return ExecuteResult.SUCCESS;
	}
}
