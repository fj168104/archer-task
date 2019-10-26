package cn.exrick.xboot.modules.task.engine.db;

import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.engine.TaskFlowAdapter;
import cn.exrick.xboot.modules.task.engine.TaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.AutoTaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.ControllingUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.MailTaskUnit;
import cn.exrick.xboot.modules.task.engine.db.unit.UserTaskUnit;
import cn.exrick.xboot.modules.task.engine.memory.MemoryTaskNode;
import cn.exrick.xboot.modules.task.engine.memory.MemoryTaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.modules.task.service.TaskModelService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.util.CollectionUtils;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by feng on 2019/10/20 0009
 */

@Slf4j
public class DBTaskFlow extends TaskFlowAdapter {

    private TaskInstance instance;

    private TaskModelService modelService;

    private TaskInstanceService taskInstanceService;

    private TaskProcessService processService;


    private Map<String, Element> vertexMap = Maps.newHashMap();
    private Set<Element> edgeSet = Sets.newHashSet();

    /**
     * 执行器
     */
    private ExecutorService executor;
    /**
     * 控制任务等待
     */
    private boolean isWaiting;

    /**
     * 当前执行的节点对象
     */
    private List<DBTaskNode> executingNodes = Lists.newArrayList();

    private ConcurrentHashSet<MemoryTaskNode> executingTaskNode = new ConcurrentHashSet<>();

    public DBTaskFlow(String taskInstanceId, TaskModelService modelService, TaskInstanceService taskInstanceService, TaskProcessService processService) {
        this.modelService = modelService;
        this.taskInstanceService = taskInstanceService;
        this.processService = processService;
        instance = taskInstanceService.get(taskInstanceId);
        setExecuteNodeList(instance);
    }

    @Override
    public void loadTask() throws Exception {
        if (instance.getFinished() == TaskConstant.INSTANCE_FINISH) {
            log.warn("任务已结束，不能操作");
        }

        if (instance.getPending() == TaskConstant.INSTANCE_UNPENDING) {
            log.warn("任务运行中，不能操作");
        }

        Document doc = getDocument();
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
                vertexMap.put(cellElement.getAttributeValue("id"), cellElement);
            if (cellElement.getAttribute("edge") != null) edgeSet.add(cellElement);
        }

        //初始化executingNodes
        List<String> executeNodeList = instance.getExecuteNodeList();
        for (String nodeId : executeNodeList) {
            executingNodes.add(new DBTaskNode(getUnit(nodeId), taskInstanceService, processService, vertexMap, edgeSet));
        }
        log.info("任务运行节点：" + executingNodes);

    }

    @Override
    public void start() {
        if (instance.getStarted().equals(TaskConstant.INSTANCE_UNSTART)) {
            //获取 START 节点
            for (Map.Entry<String, Element> entry : vertexMap.entrySet()) {
                Element element = entry.getValue().getChild("Object");
                String type = element.getAttributeValue("type");
                if (type.equalsIgnoreCase("START")) {
                    executingNodes.add(new DBTaskNode(getUnit(entry.getKey()), taskInstanceService, processService, vertexMap, edgeSet));
                    break;
                }
            }
            //插入流程记录，更新 instance
            DBTaskNode taskNode = executingNodes.get(0);
//			TaskProcess process = new TaskProcess();
//			process.setTaskId(instance.getId());
//			process.setExecuteNode(taskNode.getSemphone().toString());
//			processService.save(process);
            instance.setStarted(TaskConstant.INSTANCE_STARTED);
            instance.setExecuteNode(taskNode.getSemphone().toString());
            setExecuteNodeList(instance);
            taskInstanceService.save(instance);
            //递归next 插入流程记录，更新 instance
			if(taskNode.getTaskUnit().execute() == TaskUnit.ExecuteResult.SUCCESS){
				taskNode.next();
			}

        } else if (instance.getFinished().equals(TaskConstant.INSTANCE_FINISH)) {
            throw new XbootException("流程已经结束");

        } else if (instance.getPending().equals(TaskConstant.INSTANCE_UNPENDING)) {
            throw new XbootException("流程还在运行中，不能启动");
        } else {
			for (DBTaskNode executingNode : executingNodes) {
				executingNode.next();
			}
        }

        Set<MemoryTaskNode> executableNodes = process(rootNode);
        while (!CollectionUtils.isEmpty(executableNodes)) {
            Set<MemoryTaskNode> executableNodeTemps = Sets.newHashSet();
            executableNodes.forEach(item -> {
                executableNodeTemps.addAll(process(item));
            });
            executableNodes = executableNodeTemps;
        }
    }

    //处理PENGDING
    @Override
    public void recovery() {
		//TODO
    }

    @Override
    public void suspend() {
        isWaiting = true;
    }

    @Override
    public void kill() {

    }

    @Override
    public Set<MemoryTaskNode> queryPhase() {
        return executingTaskNode;
    }


    private Set<MemoryTaskNode> process(MemoryTaskNode node) {
        Set<MemoryTaskNode> executableNodes = Sets.newHashSet();
//		if (CollectionUtils.isEmpty(node.getNextNodes())) {
//			System.out.println("The task flow done!");
//			return executableNodes;
//		}
        //execute
        waitFor();
        if (executor.isShutdown()) return executableNodes;
        executingTaskNode.add(node);
        Future<TaskUnit.ExecuteResult> future = executor.submit(new Callable<TaskUnit.ExecuteResult>() {
            @Override
            public TaskUnit.ExecuteResult call() {
                if (node.getTaskUnit() == null) return TaskUnit.ExecuteResult.SUCCESS;
                return node.getTaskUnit().execute();
            }
        });
        TaskUnit.ExecuteResult result = null;
        try {
            result = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return executableNodes;
        } finally {
            executingTaskNode.remove(node);
        }

        if (result == TaskUnit.ExecuteResult.FAIL) {
            System.err.println("The task flow fail!");
            return executableNodes;
        }
        node.getNextNodes().forEach(item -> {
            //传递信号量
            node.next();
            //判断信号量是否以准备好，如果是则执行任务
            if (item.isReady()) executableNodes.add((MemoryTaskNode) item);
        });
        return executableNodes;
    }

    private void waitFor() {
        while (isWaiting) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private MemoryTaskNode createStartNode() {
        return new MemoryTaskNode(new MemoryTaskUnit("开始节点") {
            @Override
            public ExecuteResult execute() {
                return ExecuteResult.SUCCESS;
            }
        });
    }

    private MemoryTaskNode createEndNode() {
        return new MemoryTaskNode(new MemoryTaskUnit("结束节点") {
            @Override
            public ExecuteResult execute() {
                System.out.println("The task flow done!");
                executor.shutdownNow();
                return ExecuteResult.SUCCESS;
            }
        });
    }

    /**
     * 从db中获取XML，并实例化 Document
     */
    private Document getDocument() throws JDOMException, IOException {
        TaskModel model = modelService.get(instance.getModelId());
        String modelXml = model.getProcessXml();
        //解析XML
        String xmlDoc = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + modelXml;
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();

        return sb.build(source);
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

    private void setExecuteNodeList(TaskInstance instance) {
        if (StringUtils.isNotEmpty(instance.getExecuteNode())) {
            String nodes[] = instance.getExecuteNode().trim().split(",");
            instance.setExecuteNodeList(Arrays.asList(nodes));
        } else {
            instance.setExecuteNodeList(Lists.newArrayList());
        }
    }

}
