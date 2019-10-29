package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.activiti.entity.ActModel;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.service.TaskModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "任务模型管理接口")
@RequestMapping("/xboot/taskModel")
@Transactional
public class TaskModelController extends XbootBaseController<TaskModel, String> {

    @Autowired
    private TaskModelService taskModelService;

    @Override
    public TaskModelService getService() {
        return taskModelService;
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<TaskModel>> getByCondition(@ModelAttribute TaskModel taskModel,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<TaskModel> page = taskModelService.findByCondition(taskModel, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<TaskModel>>().setData(page);
    }


    @GetMapping("/getXml/{id}")
    @ApiOperation(value = "获取ModelXML")
    public Result<String> getModelXml(@PathVariable String id){
        ResultUtil<String> resultUtil = new ResultUtil<>();
        String modelXml = "";
        //todo

        return resultUtil.setData(modelXml);
    }

    @PostMapping("/saveXml/{id}")
    @ApiOperation(value = "保存ModelXML")
    public Result<String> saveModelXml(@PathVariable String id, @RequestParam String modelXml){
        ResultUtil<String> resultUtil = new ResultUtil<>();
        //todo

        return resultUtil.setSuccessMsg("Model保存成功");
    }

    @PostMapping("/release/{id}")
    @ApiOperation(value = "模型发布")
    public Result<Object> releaseModel(@PathVariable String id){
        TaskModel model = taskModelService.get(id);
        model.setIsRelease(Boolean.TRUE);
        taskModelService.save(model);
        //copy model and version++
        model.setId(null);
        model.setVersion(model.getVersion() + 1);
        taskModelService.save(model);
        return new ResultUtil<Object>().setSuccessMsg("模型发布成功");
    }


}
