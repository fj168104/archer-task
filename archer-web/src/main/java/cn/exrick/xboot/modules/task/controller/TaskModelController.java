package cn.exrick.xboot.modules.task.controller;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.service.TaskModelService;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "任务模型管理接口")
@RequestMapping("/xboot/taskModel")
@Transactional
public class TaskModelController {

	@Autowired
	private TaskModelService taskModelService;

	@RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "多条件分页获取")
	public Result<Page<TaskModel>> getByCondition(@ModelAttribute TaskModel taskModel,
												  @ModelAttribute SearchVo searchVo,
												  @ModelAttribute PageVo pageVo) {

		Page<TaskModel> page = taskModelService.findByCondition(taskModel, searchVo, PageUtil.initPage(pageVo));
		return new ResultUtil<Page<TaskModel>>().setData(page);
	}

	@GetMapping("/getAllList")
	@ApiOperation(value = "获取全部已发布模型")
	public Result<List<Map<String, Object>>> getModelKeys() {
		List<Map<String, Object>> modelKeyList = Lists.newArrayList();
		List<TaskModel> unReleaseModels = taskModelService.findByRelease(Boolean.FALSE);
		for (TaskModel model : unReleaseModels) {
			Set< Object> versionSets = Sets.newHashSet();
			List<TaskModel> taskModels = taskModelService.findByModelKey(model.getModelKey());
			taskModels.forEach(item -> {
				if(item.getRelease().equals(Boolean.TRUE)){
					Map<String, Object> map = Maps.newHashMap();
					map.put("version", item.getVersion());
					map.put("modelId", item.getId());
					versionSets.add(map);
				}
			});
			if(versionSets.size()>0) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("modelKey", model.getModelKey());
				map.put("modelName", model.getModelName());
				map.put("versionAndId", versionSets);
				modelKeyList.add(map);
			}
		}

		return ResultUtil.data(modelKeyList);
	}

	@GetMapping("/getXml/{id}")
	@ApiOperation(value = "获取ModelXML")
	public Result<String> getModelXml(@PathVariable String id) {
		ResultUtil<String> resultUtil = new ResultUtil<>();
		TaskModel taskModel = taskModelService.get(id);
		String modelXml = taskModel.getProcessXml();
		return resultUtil.setData(modelXml);
	}

	@PostMapping("/saveXml/{id}")
	@ApiOperation(value = "保存ModelXML")
	public Result<String> saveModelXml(@PathVariable String id, @RequestParam String modelXml) {
		ResultUtil<String> resultUtil = new ResultUtil<>();
		TaskModel taskModel = taskModelService.get(id);
		taskModel.setProcessXml(modelXml);
		taskModelService.save(taskModel);
		return resultUtil.setSuccessMsg("Model保存成功");
	}

	@PostMapping("/release/{id}")
	@ApiOperation(value = "模型发布")
	public Result<Object> releaseModel(@PathVariable String id) {
		TaskModel model = taskModelService.get(id);
		model.setRelease(Boolean.TRUE);
		taskModelService.save(model);
		//copy model and version++
		model.setId(null);
		model.setVersion(model.getVersion() + 1);
		taskModelService.save(model);
		return new ResultUtil<Object>().setSuccessMsg("模型发布成功");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加模型")
	public Result<Object> add(@ModelAttribute TaskModel model) {

		if (StrUtil.isBlank(model.getModelKey()) || StrUtil.isBlank(model.getModelName())) {
			return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
		}

		if (taskModelService.findByModelKey(model.getModelKey()) != null) {
			return new ResultUtil<Object>().setErrorMsg("该模型标识已存在");
		}
		TaskModel taskModel = taskModelService.save(model);
		if (taskModel == null) {
			return new ResultUtil<Object>().setErrorMsg("添加失败");
		}

		return new ResultUtil<Object>().setSuccessMsg("添加成功");
	}

	@PostMapping("/status/{id}")
	@ApiOperation(value = "启用或者禁用模型")
	public Result<Object> updateStatus(@RequestParam String id) {
		TaskModel model = taskModelService.get(id);
		if (model == null) {
			return new ResultUtil<Object>().setErrorMsg("该模型不存在");
		}
		if (model.getModelStatus() == CommonConstant.STATUS_NORMAL) {
			model.setModelStatus(CommonConstant.STATUS_DISABLE);
		} else {
			model.setModelStatus(CommonConstant.STATUS_NORMAL);
		}
		taskModelService.save(model);
		return new ResultUtil<Object>().setSuccessMsg("执行成功");
	}

	@DeleteMapping("/delAllByIds/{ids}")
	@ApiOperation(value = "批量通过ids删除")
	public Result<Object> delByIds(@PathVariable String[] ids) {

		for (String id : ids) {
			TaskModel taskModel = taskModelService.get(id);
			List<TaskModel> modelList = taskModelService.findByModelKey(taskModel.getModelKey());
			if (modelList.size() > 1) {
				return new ResultUtil<Object>().setErrorMsg("删除失败，包含已经发布的模型");
			}
		}
		for (String id : ids) {
			taskModelService.delete(id);
		}
		return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
	}
}
