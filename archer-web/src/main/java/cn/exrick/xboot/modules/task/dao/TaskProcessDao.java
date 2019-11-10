package cn.exrick.xboot.modules.task.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.task.entity.TaskProcess;

import java.util.List;

/**
 * 任务流程明细数据处理层
 *
 * @author Exrick
 */
public interface TaskProcessDao extends XbootBaseDao<TaskProcess, String> {
	/**
	 * 通过taskId 和 搜索
	 *
	 * @param taskId
	 * @param executeNode
	 * @return
	 */
	TaskProcess findByTaskIdAndExecuteNode(String taskId, String executeNode);

	List<TaskProcess> findByTaskId(String taskId);

}
