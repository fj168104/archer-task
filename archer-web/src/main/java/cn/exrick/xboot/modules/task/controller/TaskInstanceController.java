package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Feng
 */
@Slf4j
@RestController
@Api(description = "任务实例管理接口")
@RequestMapping("/xboot/taskInstance")
//@Transactional
public class TaskInstanceController {

	@Autowired
	private TaskInstanceService taskInstanceService;

	@Autowired
	private TaskProcessService processService;

	@RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "多条件分页获取")
	public Result<Page<TaskInstance>> getByCondition(@ModelAttribute TaskInstance taskInstance,
													 @ModelAttribute SearchVo searchVo,
													 @ModelAttribute PageVo pageVo) {

		Page<TaskInstance> page = taskInstanceService.findByCondition(taskInstance, searchVo, PageUtil.initPage(pageVo));
		return ResultUtil.data(page);
	}

	@PostMapping("/start/{id}")
	@ApiOperation(value = "开启任务")
	public Result<String> startTask(@PathVariable String id) {

		TaskFlowMetedata metedata = taskInstanceService.loadTask(id);
		taskInstanceService.start(metedata);
		return ResultUtil.success("开启成功");
	}

	@PostMapping("/suspend/{id}")
	@ApiOperation(value = "暂停任务")
	public Result<String> suspend(@PathVariable String id) {
		taskInstanceService.suspend(id);
		return ResultUtil.success("暂停成功");
	}

	@GetMapping("/queryPhase/{id}")
	@ApiOperation(value = "查询任务执行节点")
	public Result<Set<TaskProcess>> queryPhase(@PathVariable String id) {
		Set<String> executeNodeSet = taskInstanceService.queryPhase(id);
		Set<TaskProcess> processSet = executeNodeSet.stream().map(item -> processService.findByTaskIdAndExecuteNode(id, item)).collect(Collectors.toSet());
		return ResultUtil.data(processSet);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增任务")
	public Result<Object> add(@ModelAttribute TaskInstance instance) {

		if (StrUtil.isBlank(instance.getModelId()) || StrUtil.isBlank(instance.getName())) {
			return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
		}

		TaskInstance taskInstance = taskInstanceService.save(instance);
		if (taskInstance == null) {
			return ResultUtil.error("添加失败");
		}

		return ResultUtil.data(taskInstance.getId());
	}

	@PostMapping("/update")
	@ApiOperation(value = "更新数据")
	public Result<Object> update(@ModelAttribute TaskInstance entity) {
		if (StrUtil.isBlank(entity.getModelId()) || StrUtil.isBlank(entity.getName())) {
			return ResultUtil.error("缺少必需表单字段");
		}
		taskInstanceService.update(entity);
		return ResultUtil.success("更新成功");
	}


	@DeleteMapping("/delAllByIds/{ids}")
	@ApiOperation(value = "批量通过ids删除")
	public Result<Object> delByIds(@PathVariable String[] ids) {

		for (String id : ids) {
			TaskInstance instance = taskInstanceService.get(id);
			if (instance.getStatus().equals(TaskConstant.TASK_STATUS_RUNNING)) {
				return ResultUtil.error("删除失败，有任务正在运行中");
			}
		}
		for (String id : ids) {
			//删除流程数据
			for (TaskProcess process : processService.findByTaskId(id)) {
				processService.delete(process);
			}
			//删除实例数据
			taskInstanceService.delete(id);
		}
		return ResultUtil.success("批量通过id删除数据成功");
	}
}
