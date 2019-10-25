package cn.exrick.xboot.modules.task.engine.db;

import cn.exrick.xboot.modules.task.engine.BaseTaskNode;
import cn.exrick.xboot.modules.task.engine.DefaultTaskSemphone;
import cn.exrick.xboot.modules.task.engine.TaskNode;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import org.jdom.Element;

import java.util.Map;
import java.util.Set;

/**
 * Created by feng on 2019/9/7 0007
 */
public class DBTaskNode extends BaseTaskNode {

	private TaskInstanceService taskInstanceService;

	private TaskProcessService processService;


	private Map<String, Element> vertexMap;
	private Set<Element> edgeSet;


	public <T extends DBTaskUnit> DBTaskNode(T taskUnit,
											 TaskInstanceService taskInstanceService,
											 TaskProcessService processService,
											 Map<String, Element> vertexMap,
											 Set<Element> edgeSet) {
		super(taskUnit);
		this.taskInstanceService = taskInstanceService;
		this.processService = processService;
		this.vertexMap = vertexMap;
		this.edgeSet = edgeSet;
		semphone = new DefaultTaskSemphone<>(taskUnit.getUnitId());
	}


	@Override
	public void next() {
		this.nextTaskNodes.forEach(item -> item.getCurrentSets().add(semphone));
	}

	/**
	 * 添加后继节点
	 *
	 * @return
	 */
	public TaskNode addNextNode(TaskNode node) {
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

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof BaseTaskNode)) {
			return false;
		}
		return semphone.equals(((BaseTaskNode) o).getSemphone());
	}
}
