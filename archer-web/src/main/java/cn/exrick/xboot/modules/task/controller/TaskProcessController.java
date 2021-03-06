package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskFlowMetedata;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "任务流程明细管理接口")
@RequestMapping("/xboot/taskProcess")
//@Transactional
public class TaskProcessController {

    @Autowired
    private TaskProcessService taskProcessService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<TaskProcess>> getByCondition(@ModelAttribute TaskProcess taskProcess,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<TaskProcess> page = taskProcessService.findByCondition(taskProcess, searchVo, PageUtil.initPage(pageVo));
        TaskFlowMetedata flowMetedata = taskInstanceService.loadTask(taskProcess.getTaskId());
        page.getContent().forEach(item -> {
            taskProcessService.setNodeSemphoneSet(item);
            taskProcessService.setPreExecuteNodeSet(item);
            taskProcessService.setNextExecuteNodeSet(item);
            DBTaskUnit unit = taskProcessService.getUnit(item.getExecuteNode(),
                    flowMetedata.getVertexMap().get(item.getExecuteNode()).getChild("Object"));
            item.setExecuteNodeName(unit.getNodeName());
            item.setNodeSemphoneNameSet(item.getNodeSemphoneSet().stream().map(semphone -> {
                DBTaskUnit uSemphone = taskProcessService.getUnit(semphone,
                        flowMetedata.getVertexMap().get(semphone).getChild("Object"));
                return uSemphone.getNodeName();
            }).collect(Collectors.toSet()));

            item.setPreExecuteNodeNameSet(item.getPreExecuteNodeSet().stream().map(pre -> {
                DBTaskUnit uPre = taskProcessService.getUnit(pre,
                        flowMetedata.getVertexMap().get(pre).getChild("Object"));
                return uPre.getNodeName();
            }).collect(Collectors.toSet()));

            item.setNextExecuteNodeNameSet(item.getNextExecuteNodeSet().stream().map(next -> {
                DBTaskUnit uNext = taskProcessService.getUnit(next,
                        flowMetedata.getVertexMap().get(next).getChild("Object"));
                return uNext.getNodeName();
            }).collect(Collectors.toSet()));

        });
        return new ResultUtil<Page<TaskProcess>>().setData(page);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据")
    public Result<Object> update(@ModelAttribute TaskProcess entity) {
        if (StrUtil.isBlank(entity.getId())) {
            return ResultUtil.error("缺少必需表单字段");
        }
        taskProcessService.update(entity);
        return ResultUtil.success("更新成功");
    }

    @GetMapping("/getTaskUnit/{taskId}/{nodeId}")
    @ApiOperation(value = "获取任务单元")
    public Result<Object> getTaskUnit(@PathVariable String taskId, @PathVariable String nodeId) {

        TaskFlowMetedata flowMetedata = taskInstanceService.loadTask(taskId);
        DBTaskUnit unit = taskProcessService.getUnit(nodeId, flowMetedata.getVertexMap().get(nodeId).getChild("Object"));
        return ResultUtil.data(unit);
    }


}
