package cn.exrick.xboot.modules.task.engine;

import java.util.Set;

/**
 * Created by feng on 2019/9/7 0007
 * 任务节点
 */
public interface TaskNode {

	TaskSemphone getSemphone();

	TaskUnit getTaskUnit();

	/**
	 * 当前节点任务运行所需要的信号量集合
	 * @return
	 */
	Set<TaskSemphone> getPrevSets();

	/**
	 * 当前拥有的信号量集合
	 * @return
	 */
	Set<TaskSemphone> getCurrentSets();

	/**
	 * 添加后继节点，后继节点增加前驱节点
	 *
	 * @return
	 */
	TaskNode addNextNode(TaskNode node);

	/**
	 * 任务执行完成并输出信号量给下一节点集合
	 */
	void next();

	/**
	 * 返回下一节点集合
	 *
	 * @return
	 */
	Set<TaskNode> getNextNodes();

	/**
	 * 返回上一节点集合
	 *
	 * @return
	 */
	Set<TaskNode> getPrevNodes();

	/**
	 * 是否信号量已经准备完成，可以执行任务
	 *
	 * @return
	 */
	Boolean isReady();
}
