package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.entity.TaskUnitDefinition;
import cn.exrick.xboot.modules.task.service.TaskUnitDefinitionService;
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
@Api(description = "任务单元定义管理接口")
@RequestMapping("/xboot/taskUnitDefinition")
@Transactional
public class TaskUnitDefinitionController extends XbootBaseController<TaskUnitDefinition, String> {

    @Autowired
    private TaskUnitDefinitionService taskUnitDefinitionService;

    @Override
    public TaskUnitDefinitionService getService() {
        return taskUnitDefinitionService;
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<TaskUnitDefinition>> getByCondition(@ModelAttribute TaskUnitDefinition taskUnitDefinition,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<TaskUnitDefinition> page = taskUnitDefinitionService.findByCondition(taskUnitDefinition, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<TaskUnitDefinition>>().setData(page);
    }

}
