package cn.exrick.xboot.task.engine;

import com.google.api.client.util.Sets;

import java.util.Set;

/**
 * Created by feng on 2019/9/7 0007
 */
public abstract class BaseTaskNode implements TaskNode {

	/**
	 * 该节点持有的信号量
	 */
	protected TaskSemphone semphone;

	/**
	 * 任务单元
	 */
	protected TaskUnit taskUnit;

	/**
	 * 前驱节点
	 */
	protected Set<TaskNode> prevTaskNodes = Sets.newHashSet();

	/**
	 * 后继节点
	 */
	protected Set<TaskNode> nextTaskNodes = Sets.newHashSet();


	/**
	 * 所需信号量的定义，即前驱节点的信号量的集合
	 */
	protected Set<TaskSemphone> prevSets = Sets.newHashSet();

	/**
	 * 搜集的信号量的集合
	 */
	protected Set<TaskSemphone> currentSets = Sets.newHashSet();

	public BaseTaskNode(TaskUnit taskUnit){
		this.taskUnit = taskUnit;
	}

	@Override
	public Boolean isReady() {
		return prevSets.equals(currentSets);
	}

	@Override
	public TaskSemphone getSemphone() {
		return this.semphone;
	}

	@Override
	public TaskUnit getTaskUnit() {
		return this.taskUnit;
	}

	public Set<TaskSemphone> getPrevSets() {
		return prevSets;
	}

	@Override
	public Set<TaskSemphone> getCurrentSets() {
		return currentSets;
	}

	@Override
	public String toString(){
		return String.format("TaskNode{semphone:[%s], taskUnit:[%s], currentSets:[%s]}",
				semphone, taskUnit.getClass().getName(), currentSets.toString());
	}
}
