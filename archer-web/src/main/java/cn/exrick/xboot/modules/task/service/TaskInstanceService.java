package cn.exrick.xboot.modules.task.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.task.entity.TaskFlowMetedata;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.Set;

/**
 * 任务实例接口
 *
 * @author Feng
 */
public interface TaskInstanceService extends XbootBaseService<TaskInstance, String> {

	/**
	 * 多条件分页获取
	 *
	 * @param taskInstance
	 * @param searchVo
	 * @param pageable
	 * @return
	 */
	Page<TaskInstance> findByCondition(TaskInstance taskInstance, SearchVo searchVo, Pageable pageable);

	TaskFlowMetedata loadTask(String instanceId) throws XbootException;

	void start(TaskFlowMetedata metedata);

	void suspend(String instanceId);

	void kill(String instanceId);

	Set<String> queryPhase(String instanceId);

}