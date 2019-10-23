package cn.exrick.xboot.modules.task.engine.db;

import cn.exrick.xboot.modules.task.engine.BaseTaskNode;
import cn.exrick.xboot.modules.task.engine.DefaultTaskSemphone;
import cn.exrick.xboot.modules.task.engine.TaskNode;
import cn.exrick.xboot.modules.task.engine.TaskUnit;

import java.util.Set;

/**
 * Created by feng on 2019/9/7 0007
 */
public class DBTaskNode extends BaseTaskNode {

	public DBTaskNode(TaskUnit taskUnit) {
		super(taskUnit);
		semphone = new DefaultTaskSemphone<>(taskUnit.getTaskName());
	}

	@Override
	public void next() {
		this.nextTaskNodes.forEach(item ->item.getCurrentSets().add(semphone));
	}

	/**
	 * 添加后继节点
	 * @return
	 */
	public TaskNode addNextNode(TaskNode node){
		//后继
		nextTaskNodes.add(node);
		//前驱
		node.getPrevNodes().add(this);
		//信号量
		node.getPrevSets().add(this.getSemphone());

		return this;
	}

	@Override
	public Set<TaskNode> getNextNodes() {
		return nextTaskNodes;
	}

	@Override
	public Set<TaskNode> getPrevNodes() {
		return prevTaskNodes;
	}
}
