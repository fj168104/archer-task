package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 任务模型接口
 * @author Exrick
 */
public interface TaskModelService extends XbootBaseService<TaskModel,String> {

    /**
    * 多条件分页获取
    * @param taskModel
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskModel> findByCondition(TaskModel taskModel, SearchVo searchVo, Pageable pageable);
}