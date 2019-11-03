package cn.exrick.xboot.modules.task.serviceimpl;

import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.dao.TaskInstanceDao;
import cn.exrick.xboot.modules.task.engine.TaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskFlowMetedata;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.service.TaskModelService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.api.client.util.Sets;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.lang.reflect.Field;

/**
 * 任务实例接口实现
 *
 * @author Feng
 */
@Slf4j
@Service
@Transactional
public class TaskInstanceServiceImpl implements TaskInstanceService {

	@Autowired
	private TaskInstanceDao taskInstanceDao;

	@Autowired
	private TaskModelService modelService;

	@Autowired
	private TaskProcessService processService;

	@Override
	public TaskInstanceDao getRepository() {
		return taskInstanceDao;
	}

	@Override
	public Page<TaskInstance> findByCondition(TaskInstance taskInstance, SearchVo searchVo, Pageable pageable) {

		return taskInstanceDao.findAll(new Specification<TaskInstance>() {
			@Nullable
			@Override
			public Predicate toPredicate(Root<TaskInstance> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

				// TODO 可添加你的其他搜索过滤条件 默认已有创建时间过滤
				Path<Date> createTimeField = root.get("createTime");

				List<Predicate> list = new ArrayList<Predicate>();

				//创建时间
				if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
					Date start = DateUtil.parse(searchVo.getStartDate());
					Date end = DateUtil.parse(searchVo.getEndDate());
					list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
				}

				Predicate[] arr = new Predicate[list.size()];
				cq.where(list.toArray(arr));
				return null;
			}
		}, pageable);
	}

	@Override
	public TaskFlowMetedata loadTask(String instanceId) throws XbootException {
		TaskFlowMetedata metedata = new TaskFlowMetedata();

		TaskInstance instance = get(instanceId);
		metedata.setInstance(instance);
		if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
			throw new XbootException("流程已经结束: instanceId=" + instance.getId());
		}

		if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
			throw new XbootException("流程还在运行中，不能启动: instanceId=" + instance.getId());
		}

		Document doc = getDocument(instance.getModelId());
		//取的根元素
		Element root = doc.getRootElement();
		log.info(root.getName()); //输出根元素的名称 mxGraphModel
		//得到根元素所有子元素的集合
		List mxCellElements = root.getChild("root").getChildren("mxCell");

		for (Iterator iterator = mxCellElements.iterator(); iterator.hasNext(); ) {
			Element cellElement = (Element) iterator.next();
			//去掉多余的元素
			if (!"1".equals(cellElement.getAttributeValue("parent"))) {
				iterator.remove();
				continue;
			}
			if (cellElement.getAttribute("vertex") != null)
				metedata.getVertexMap().put(cellElement.getAttributeValue("id"), cellElement);
			if (cellElement.getAttribute("edge") != null) metedata.getEdgeSet().add(cellElement);
		}
		log.info("任务运行节点：" + instance.getExecuteNodeSet().toString());
		return metedata;
	}

	@Override
	@Async
	public void start(TaskFlowMetedata metedata) {
		TaskInstance instance = metedata.getInstance();
		if (instance.getStatus().equals(TaskConstant.TASK_STATUS_UNSTART)) {
			executeNode(createStartNode(metedata), metedata);
			updateTaskStatus(instance.getId());
		} else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
			throw new XbootException("流程已经结束: instanceId=" + instance.getId());

		} else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
			throw new XbootException("流程还在运行中，不能启动: instanceId=" + instance.getId());
		} else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_PENDING)) {
			instance.getExecuteNodeSet().forEach(item -> executeNode(item, metedata));
			updateTaskStatus(instance.getId());
		} else {
			throw new XbootException("找不到该任务状态");
		}
	}

	private String createStartNode(TaskFlowMetedata metedata) {
		TaskInstance instance = metedata.getInstance();
		String startNodeId = "";
		//获取 START 节点，开启任务实例
		for (Map.Entry<String, Element> entry : metedata.getVertexMap().entrySet()) {
			Element element = entry.getValue().getChild("Object");
			String type = element.getAttributeValue("type");
			if (type.equalsIgnoreCase("START")) {
				startNodeId = element.getAttributeValue("id");
				instance.setStatus(TaskConstant.TASK_STATUS_RUNNING);
				break;
			}
		}
		save(instance);
		//新建一条流程记录
		TaskProcess taskProcess = new TaskProcess();
		taskProcess.setTaskId(instance.getId());
		taskProcess.setExecuteNode(startNodeId);
		processService.save(taskProcess);
		return startNodeId;
	}

	@Override
	public void suspend(String instanceId) {
		TaskInstance instance = get(instanceId);
		instance.setStatus(TaskConstant.TASK_STATUS_PENDING);
		save(instance);
	}

	@Override
	public void kill(String instanceId) {
		TaskInstance instance = get(instanceId);
		instance.setStatus(TaskConstant.TASK_STATUS_FINISHED);
		save(instance);
	}

	@Override
	public Set<String> queryPhase(String instanceId) {
		TaskInstance instance = get(instanceId);
		return instance.getExecuteNodeSet();
	}

	/**
	 * @return
	 */
	public TaskInstance get(String id) {
		TaskInstance instance = getRepository().findById(id).orElse(null);
		setExecuteNodeSet(instance);
		return instance;

	}

	/**
	 * 保存
	 *
	 * @param taskInstance
	 * @return
	 */
	@Override
	public TaskInstance save(TaskInstance taskInstance) {
		taskInstance.setExecuteNodes(String.join("','", taskInstance.getExecuteNodeSet()));
		return getRepository().save(taskInstance);
	}

	private void setExecuteNodeSet(TaskInstance instance) {
		if (StringUtils.isNotEmpty(instance.getExecuteNodes())) {
			String nodes[] = instance.getExecuteNodes().trim().split(",");
			instance.setExecuteNodeSet(new HashSet<>(Arrays.asList(nodes)));
		} else {
			instance.setExecuteNodeSet(Sets.newHashSet());
		}
	}

	private void executeNode(String nodeId, TaskFlowMetedata metedata) {

		//检查instance状态
		TaskInstance instance = get(metedata.getInstance().getId());
		metedata.setInstance(instance);
		Map<String, Element> vertexMap = metedata.getVertexMap();
		Set<Element> edgeSet = metedata.getEdgeSet();
		if (instance.getStatus().equals(TaskConstant.TASK_STATUS_PENDING)) {
			log.info("任务已暂停：taskId=" + instance.getId());
			return;
		}

		TaskProcess taskProcess = processService.findByTaskIdAndExecuteNode(instance.getId(), nodeId,
				vertexMap.get(nodeId).getChild("Object"));
		/**判断信号量是否收集完成**/
		//信号量未收集完成，pending该节点
		if (!taskProcess.getNodeSemphoneSet().equals(taskProcess.getPreExecuteNodesSet())) {
			return;
		}

		TaskUnit.ExecuteResult result;
		//信号量收集完成，执行任务
		if (taskProcess.getStatus().equals(TaskConstant.NODE_STATUS_FINISHED)) {
			result = TaskUnit.ExecuteResult.SUCCESS;
		} else {
			taskProcess.setStatus(TaskConstant.NODE_STATUS_RUNNING);
			processService.save(taskProcess);
			//任务执行完成，更新任务实例的状态, 任务暂时仅支持单进程运行，未考虑分布式的一致性
			result = taskProcess.getTaskUnit().execute();
		}


		if (result == TaskUnit.ExecuteResult.SUCCESS) {
			taskProcess.setStatus(TaskConstant.NODE_STATUS_FINISHED);
			//增加后继节点
			for (Element element : edgeSet) {
				if (element.getAttributeValue("source").equals(taskProcess.getExecuteNode())) {
					//分支节点的处理
					if (element.getAttributeValue("type").equals("MERGE") &&
							!element.getAttributeValue("value").equals(taskProcess.getRunResult()))
						continue;
					String nextNodeId = element.getAttributeValue("target");
					TaskProcess nextProcess = new TaskProcess();
					nextProcess.setTaskId(instance.getId());
					nextProcess.setExecuteNode(nextNodeId);
					nextProcess.getNodeSemphoneSet().add(nodeId);
					for (Element elementPre : edgeSet) {
						if (elementPre.getAttributeValue("target").equals(nextNodeId)) {
							nextProcess.getPreExecuteNodesSet().add(elementPre.getAttributeValue("source"));
						}
					}
					processService.save(nextProcess);
					//添加到任务实例中执行的节点集合
					instance.getExecuteNodeSet().add(nextNodeId);
					taskProcess.getNextExecuteNodeSet().add(nextNodeId);
				}
			}
			processService.save(taskProcess);
			//从 task instance移除当前节点
			instance.getExecuteNodeSet().remove(nodeId);
			save(instance);
			//分发后继节点
			for (String nextNodeId : taskProcess.getNextExecuteNodeSet()) {
				executeNode(nextNodeId, metedata);
			}
		} else if (result == TaskUnit.ExecuteResult.PENDING) {
			taskProcess.setStatus(TaskConstant.NODE_STATUS_PENDING);
			processService.save(taskProcess);
		} else {
			throw new XbootException("无法处理的异常");
		}
	}

	/**
	 * 从db中获取XML，并实例化 Document
	 */
	private Document getDocument(String modelId) throws XbootException {
		TaskModel model = modelService.get(modelId);
		String modelXml = model.getProcessXml();
		//解析XML
		String xmlDoc = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + modelXml;
		//创建一个新的字符串
		StringReader read = new StringReader(xmlDoc);
		//创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		//创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder();

		try {
			return sb.build(source);
		} catch (Exception e) {
			log.error("parse model error", e);
			throw new XbootException(e.getMessage());
		}
	}

	private void updateTaskStatus(String instanceId) {
		//refresh instance
		TaskInstance instance = get(instanceId);
		if (instance.getStatus().equals(TaskConstant.TASK_STATUS_PENDING)) {
			log.info("手动暂停任务：instanceId=" + instance.getId());
			return;
		} else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
			Integer status = 99;
			for (String executeNode : instance.getExecuteNodeSet()) {
				TaskProcess process = processService.findByTaskIdAndExecuteNode(instanceId, executeNode);
				if (!process.getStatus().equals(TaskConstant.NODE_STATUS_FINISHED)) {
					status = TaskConstant.TASK_STATUS_PENDING;
					break;
				}
			}
			if (status == 99) {
				instance.setStatus(TaskConstant.TASK_STATUS_FINISHED);
				save(instance);
				log.info("任务结束：instanceId=" + instance.getId());
			} else {
				instance.setStatus(TaskConstant.TASK_STATUS_PENDING);
				save(instance);
				log.info("任务暂停：instanceId=" + instance.getId());
			}
		}

	}

}
