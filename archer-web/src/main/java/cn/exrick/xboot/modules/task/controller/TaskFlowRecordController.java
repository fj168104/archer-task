package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.entity.TaskFlowRecord;
import cn.exrick.xboot.modules.task.service.TaskFlowRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Feng
 */
@Slf4j
@RestController
@Api(description = "任务执行记录管理接口")
@RequestMapping("/xboot/taskFlowRecord")
@Transactional
public class TaskFlowRecordController extends XbootBaseController<TaskFlowRecord, String> {

    @Autowired
    private TaskFlowRecordService taskFlowRecordService;

    @Override
    public TaskFlowRecordService getService() {
        return taskFlowRecordService;
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<TaskFlowRecord>> getByCondition(@ModelAttribute TaskFlowRecord taskFlowRecord,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<TaskFlowRecord> page = taskFlowRecordService.findByCondition(taskFlowRecord, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<TaskFlowRecord>>().setData(page);
    }

}
