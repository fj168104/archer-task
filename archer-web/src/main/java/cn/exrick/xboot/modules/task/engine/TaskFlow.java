package cn.exrick.xboot.modules.task.engine;

import java.util.Set;

/**
 * Created by feng on 2019/9/5 0005
 * 任务流的核心控制器，本质就是遍历执行一个DAU(有向无环图)，本接口仅定义了一个任务流实体有的功能
 * 实现类将实现任务流的调度执行
 *
 */
public interface TaskFlow {

	/**
	 * 载入任务
	 */
	void loadTask();

	/**
	 * 启动任务
	 */
	void start();

	/**
	 * 任务暂停
	 */
	void recovery();

	/**
	 * 任务继续
	 */
	void suspend();

	/**
	 * 强制结束任务
	 */
	void kill();

	/**
	 * 查询任务执行阶段
	 */
	<T extends TaskNode> Set<T>  queryPhase();

}
