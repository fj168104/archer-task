package cn.exrick.xboot.modules.task.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * 任务模型数据处理层
 *
 * @author Exrick
 */
public interface TaskModelDao extends XbootBaseDao<TaskModel, String> {

	List<TaskModel> findByModelKey(String modelKey);

	List<TaskModel> findByModelRelease(boolean release);
}
