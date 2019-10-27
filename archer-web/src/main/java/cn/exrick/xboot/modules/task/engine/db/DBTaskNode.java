package cn.exrick.xboot.modules.task.engine.db;

import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.engine.BaseTaskNode;
import cn.exrick.xboot.modules.task.engine.DefaultTaskSemphone;
import cn.exrick.xboot.modules.task.engine.TaskNode;
import cn.exrick.xboot.modules.task.engine.TaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.AutoTaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.ControllingUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.MailTaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.UserTaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import org.jdom.Element;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by feng on 2019/9/7 0007
 */
public class DBTaskNode extends BaseTaskNode {

	private String taskId;

	private TaskInstanceService taskInstanceService;

	private TaskProcessService processService;

	private Map<String, Element> vertexMap;

	private Set<Element> edgeSet;


	public <T extends DBTaskUnit> DBTaskNode(T taskUnit,
											 String taskId,
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
		TaskInstance instance = taskInstanceService.get(taskId);
		TaskProcess taskProcess = processService.findByTaskIdAndExecuteNode(taskId, getSemphone().toString());
		Set<String> nodeIds = taskProcess.getNextExecuteNodeSet();
		DBTaskNode dbTaskNode = new DBTaskNode(getUnit(element.getAttributeValue("target")),
				taskInstanceService, processService, vertexMap, edgeSet);
		//没有后继节点，任务结束
		if (StringUtils.isEmpty(nodeIds)) {
			instance.setFinished(TaskConstant.INSTANCE_FINISH);
			taskInstanceService.save(instance);
			return;
		}
		for (String nodeId : nodeIds) {
			//如果完成运行，运行任务数减一
			if (executeNode(node)) {
				executingTaskCount--;
				//将PENDING状态的流程记录再次check运行
			}

		}
		if (executingTaskCount <= 0) {
			instance.setPending(TaskConstant.INSTANCE_PENDING);
			taskInstanceService.save(instance);
		}
	}

	private boolean executeNode(DBTaskNode node) {
		TaskProcess taskProcess = processService.findByTaskIdAndExecuteNode(instance.getId(), node.getSemphone().toString());
		//判断信号量是否收集完成
		//信号量未收集完成，pending该节点
		if (!taskProcess.getNodeSemphoneSet().equals(taskProcess.getPreExecuteNodesSet())) {
			return false;
		}

		//信号量收集完成，执行任务
		taskProcess.setStatus(TaskConstant.PROCESS_STATUS_RUNNING);
		processService.save(taskProcess);
		//任务执行完成，更新任务实例的状态, 任务暂时仅支持单进程运行，未考虑分布式的一致性
		TaskUnit.ExecuteResult result = node.getTaskUnit().execute();
		if (result == TaskUnit.ExecuteResult.SUCCESS) {
			taskProcess.setStatus(TaskConstant.PROCESS_STATUS_FINISHED);
			for (Element element : edgeSet) {
				if (element.getAttributeValue("source").equals(node.getSemphone().toString())) {
					DBTaskNode dbTaskNode = new DBTaskNode(getUnit(element.getAttributeValue("target")),
							taskInstanceService, processService, vertexMap, edgeSet);
					node.addNextNode(dbTaskNode);
					taskProcess.getNextExecuteNodeSet().add(dbTaskNode.getSemphone().toString());
				}
			}
		}

		//分发后继节点
		distribteNodes(node.getNextNodes());

//        instance.setStarted(TaskConstant.INSTANCE_STARTED);
		instance.setExecuteNode(node.getSemphone().toString());
		setExecuteNodeList(instance);
		taskInstanceService.save(instance);
		//递归next 插入流程记录，更新 instance
		if (node.getTaskUnit().execute() == TaskUnit.ExecuteResult.SUCCESS) {
			node.next();
		}
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

	private DBTaskUnit getUnit(String nodeId) {
		DBTaskUnit taskUnit;
		for (Map.Entry<String, Element> entry : vertexMap.entrySet()) {
			if (entry.getKey().equals(nodeId)) {
				Element element = entry.getValue().getChild("Object");
				String type = element.getAttributeValue("type");
				switch (type) {
					case "TASK":
						taskUnit = new AutoTaskUnit(nodeId,
								type,
								element.getAttributeValue("typeName"),
								element.getAttributeValue("typeDesp"),
								element.getAttributeValue("taskClass"));
						break;
					case "USER":
						taskUnit = new UserTaskUnit(nodeId,
								type,
								element.getAttributeValue("typeName"),
								element.getAttributeValue("typeDesp"),
								element.getAttributeValue("operationTag"));
						break;
					case "MAIL":
						taskUnit = new MailTaskUnit(nodeId,
								type,
								element.getAttributeValue("typeName"),
								element.getAttributeValue("typeDesp"),
								element.getAttributeValue("cc"),
								element.getAttributeValue("to"),
								element.getAttributeValue("from"));
						break;
					default:
						taskUnit = new ControllingUnit(nodeId,
								type,
								element.getAttributeValue("typeName"),
								element.getAttributeValue("typeDesp"));

				}
				return taskUnit;
			}
		}
		throw new XbootException("XML中找不到相应的节点，nodeId = " + nodeId);
	}
}
