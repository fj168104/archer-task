package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.entity.TaskUnitDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 任务单元定义接口
 * @author Feng
 */
public interface TaskUnitDefinitionService extends XbootBaseService<TaskUnitDefinition,String> {

    /**
    * 多条件分页获取
    * @param taskUnitDefinition
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskUnitDefinition> findByCondition(TaskUnitDefinition taskUnitDefinition, SearchVo searchVo, Pageable pageable);
}