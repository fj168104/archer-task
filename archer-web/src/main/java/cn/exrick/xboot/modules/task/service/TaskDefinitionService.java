package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.entity.TaskDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 任务定义接口
 * @author Feng
 */
public interface TaskDefinitionService extends XbootBaseService<TaskDefinition,String> {

    /**
    * 多条件分页获取
    * @param taskDefinition
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskDefinition> findByCondition(TaskDefinition taskDefinition, SearchVo searchVo, Pageable pageable);
}