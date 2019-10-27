package cn.exrick.xboot.modules.task.engine.db;

import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.engine.TaskFlowAdapter;
import cn.exrick.xboot.modules.task.engine.TaskUnit;
import cn.exrick.xboot.modules.task.engine.memory.MemoryTaskNode;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.modules.task.service.TaskModelService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutorService;

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

    private Integer executingTaskCount = 0;

    /**
     * 执行器
     */
    private ExecutorService executor;
    /**
     * 控制任务等待
     */
    private boolean isWaiting;

    private ConcurrentHashSet<MemoryTaskNode> executingTaskNode = new ConcurrentHashSet<>();

    public DBTaskFlow(String taskInstanceId, TaskModelService modelService, TaskInstanceService taskInstanceService, TaskProcessService processService) {
        this.modelService = modelService;
        this.taskInstanceService = taskInstanceService;
        this.processService = processService;
        instance = taskInstanceService.get(taskInstanceId);
    }

    @Override
    public void loadTask() throws Exception {
        if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
            log.warn("任务已结束，不能操作");
        }

        if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
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
        log.info("任务运行节点：" + instance.getExecuteNodeSet().toString());
    }

    @Override
    public void start() {
        if (instance.getStatus().equals(TaskConstant.TASK_STATUS_UNSTART)) {
            String startNodeId = "";
            //获取 START 节点，开启任务实例
            for (Map.Entry<String, Element> entry : vertexMap.entrySet()) {
                Element element = entry.getValue().getChild("Object");
                String type = element.getAttributeValue("type");
                if (type.equalsIgnoreCase("START")) {
                    startNodeId = element.getAttributeValue("id");
                    instance.setStatus(TaskConstant.TASK_STATUS_RUNNING);
                    break;
                }
            }
            taskInstanceService.save(instance);
            //新建一条流程记录
            TaskProcess taskProcess = new TaskProcess();
            taskProcess.setTaskId(instance.getId());
            taskProcess.setExecuteNode(startNodeId);
            processService.save(taskProcess);
            executeNode(startNodeId);
            if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
                log.info("任务运行结束：" + instance.getId());
            }
        } else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
            throw new XbootException("流程已经结束");

        } else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
            throw new XbootException("流程还在运行中，不能启动");
        } else if (instance.getStatus().equals(TaskConstant.TASK_STATUS_PENDING)) {
            instance.getExecuteNodeSet().forEach(item -> executeNode(item));
            if (instance.getStatus().equals(TaskConstant.TASK_STATUS_FINISHED)) {
                log.info("任务运行结束：" + instance.getId());
            }

        } else {
            throw new XbootException("找不到该任务状态");
        }
    }

    private void executeNode(String nodeId) {
        TaskProcess taskProcess = processService.findByTaskIdAndExecuteNode(instance.getId(), nodeId,
                vertexMap.get(nodeId).getChild("Object"));
        /**判断信号量是否收集完成**/
        //信号量未收集完成，pending该节点
        if (!taskProcess.getNodeSemphoneSet().equals(taskProcess.getPreExecuteNodesSet())) {
            return;
        }

        //信号量收集完成，执行任务
        taskProcess.setStatus(TaskConstant.TASK_STATUS_RUNNING);
        processService.save(taskProcess);

        //任务执行完成，更新任务实例的状态, 任务暂时仅支持单进程运行，未考虑分布式的一致性
        TaskUnit.ExecuteResult result = taskProcess.getTaskUnit().execute();
        if (result == TaskUnit.ExecuteResult.SUCCESS) {
            taskProcess.setStatus(TaskConstant.TASK_STATUS_FINISHED);
            //增加后继节点
            for (Element element : edgeSet) {
                if (element.getAttributeValue("source").equals(taskProcess.getExecuteNode())) {
                    //分支节点的处理
                    if (element.getAttributeValue("type").equals("MERGE") &&
                            !element.getAttributeValue("value").equals(taskProcess.getRunResult()))
                        continue;
                    TaskProcess nextProcess = new TaskProcess();
                    nextProcess.setTaskId(instance.getId());
                    nextProcess.setExecuteNode(nodeId);
                    nextProcess.getNodeSemphoneSet().add(nodeId);
                    for (Element elementPre : edgeSet) {
                        if (elementPre.getAttributeValue("target").equals(element.getAttributeValue("target"))) {
                            nextProcess.getPreExecuteNodesSet().add(elementPre.getAttributeValue("source"));
                        }
                    }
                    processService.save(nextProcess);
                    //添加到任务实例中执行的节点集合
                    instance.getExecuteNodeSet().add(nextProcess.getExecuteNode());
                    taskProcess.getNextExecuteNodeSet().add(element.getAttributeValue("target"));
                }
            }
            processService.save(taskProcess);
            //更新 task instance
            instance.getExecuteNodeSet().remove(nodeId);
            //分发后继节点
            for (String nextNodeId : taskProcess.getNextExecuteNodeSet()) {
                executeNode(nextNodeId);
            }
        } else if (result == TaskUnit.ExecuteResult.PENDING) {
            taskProcess.setStatus(TaskConstant.TASK_STATUS_PENDING);
            processService.save(taskProcess);
        } else {
            throw new XbootException("无法处理的异常");
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

}
