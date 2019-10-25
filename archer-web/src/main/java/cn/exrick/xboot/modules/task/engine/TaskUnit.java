package cn.exrick.xboot.modules.task.engine;

/**
 * Created by feng on 2019/9/5 0005
 */
public interface TaskUnit {

	/**
	 * 获取任单元名，不能重复
	 * @return
	 */
	String getUnitId();

	/**
	 * 任务执行
	 */
	ExecuteResult execute();

	enum ExecuteResult {
		SUCCESS,
		FAIL,
		PENDING
	}
}
