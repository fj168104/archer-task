package cn.exrick.xboot.modules.task.serviceimpl;

import cn.exrick.xboot.modules.task.dao.TaskProcessDao;
import cn.exrick.xboot.modules.task.engine.TaskNode;
import cn.exrick.xboot.modules.task.engine.TaskUnit;
import cn.exrick.xboot.modules.task.engine.db.DBTaskNode;
import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import cn.exrick.xboot.modules.task.entity.TaskProcess;
import cn.exrick.xboot.modules.task.service.TaskProcessService;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;
import java.lang.reflect.Field;

/**
 * 任务流程明细接口实现
 *
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class TaskProcessServiceImpl implements TaskProcessService {

    @Autowired
    private TaskProcessDao taskProcessDao;

    @Override
    public TaskProcessDao getRepository() {
        return taskProcessDao;
    }

    @Override
    public Page<TaskProcess> findByCondition(TaskProcess taskProcess, SearchVo searchVo, Pageable pageable) {

        return taskProcessDao.findAll(new Specification<TaskProcess>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TaskProcess> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                // TODO 可添加你的其他搜索过滤条件 默认已有创建时间过滤
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                //创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

	@Override
	public TaskProcess findByTaskIdAndExecuteNode(String taskId, String executeNode) {
        TaskProcess taskProcess = taskProcessDao.findByTaskIdAndExecuteNode(taskId, executeNode);
        setNodeSemphoneSet(taskProcess);
        setPreExecuteNodeSet(taskProcess);
        setNextExecuteNodeSet(taskProcess);
        return taskProcess;
	}

    private void setNodeSemphoneSet(TaskProcess taskProcess ){
        if (StringUtils.isNotEmpty(taskProcess.getNodeSemphones())) {
            String nodeSemphones[] = taskProcess.getNodeSemphones().trim().split(",");
            taskProcess.setNodeSemphoneSet(new HashSet<>(Arrays.asList(nodeSemphones)));
        } else {
            taskProcess.setNodeSemphoneSet(Sets.newHashSet());
        }
    }

    private void setPreExecuteNodeSet(TaskProcess taskProcess ){
        if (StringUtils.isNotEmpty(taskProcess.getPreExecuteNodes())) {
            String preExecuteNodes[] = taskProcess.getPreExecuteNodes().trim().split(",");
            taskProcess.setPreExecuteNodesSet(new HashSet<>(Arrays.asList(preExecuteNodes)));
        } else {
            taskProcess.setPreExecuteNodesSet(Sets.newHashSet());
        }
    }

    private void setNextExecuteNodeSet(TaskProcess taskProcess ){
        if (StringUtils.isNotEmpty(taskProcess.getNextExecuteNodes())) {
            String nextExecuteNodes[] = taskProcess.getNextExecuteNodes().trim().split(",");
            taskProcess.setNextExecuteNodeSet(new HashSet<>(Arrays.asList(nextExecuteNodes)));
        } else {
            taskProcess.setNextExecuteNodeSet(Sets.newHashSet());
        }
    }

}
