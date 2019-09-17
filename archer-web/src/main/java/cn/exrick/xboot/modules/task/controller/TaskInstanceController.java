package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
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
@Api(description = "任务实例管理接口")
@RequestMapping("/xboot/taskInstance")
@Transactional
public class TaskInstanceController extends XbootBaseController<TaskInstance, String> {

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Override
    public TaskInstanceService getService() {
        return taskInstanceService;
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<TaskInstance>> getByCondition(@ModelAttribute TaskInstance taskInstance,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<TaskInstance> page = taskInstanceService.findByCondition(taskInstance, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<TaskInstance>>().setData(page);
    }

}
