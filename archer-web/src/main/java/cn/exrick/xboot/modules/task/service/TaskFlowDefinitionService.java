package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.entity.TaskFlowDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 任务流定义接口
 * @author Feng
 */
public interface TaskFlowDefinitionService extends XbootBaseService<TaskFlowDefinition,String> {

    /**
    * 多条件分页获取
    * @param taskFlowDefinition
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskFlowDefinition> findByCondition(TaskFlowDefinition taskFlowDefinition, SearchVo searchVo, Pageable pageable);
}