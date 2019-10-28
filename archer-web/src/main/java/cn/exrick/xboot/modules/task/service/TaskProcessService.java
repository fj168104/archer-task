package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import org.jdom.Element;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.Map;

/**
 * 任务流程明细接口
 * @author Exrick
 */
public interface TaskProcessService extends XbootBaseService<TaskProcess,String> {

    /**
    * 多条件分页获取
    * @param taskProcess
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskProcess> findByCondition(TaskProcess taskProcess, SearchVo searchVo, Pageable pageable);

    TaskProcess findByTaskIdAndExecuteNode(String taskId, String executeNode, Element vertexElement);

    TaskProcess findByTaskIdAndExecuteNode(String taskId, String executeNode);

    DBTaskUnit getUnit(String nodeId, Element vertexElement);
}
