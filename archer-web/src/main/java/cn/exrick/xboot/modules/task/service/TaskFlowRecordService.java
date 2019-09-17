package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.task.entity.TaskFlowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 任务执行记录接口
 * @author Feng
 */
public interface TaskFlowRecordService extends XbootBaseService<TaskFlowRecord,String> {

    /**
    * 多条件分页获取
    * @param taskFlowRecord
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<TaskFlowRecord> findByCondition(TaskFlowRecord taskFlowRecord, SearchVo searchVo, Pageable pageable);
}