package cn.exrick.xboot.modules.task.serviceimpl;

import cn.exrick.xboot.modules.task.dao.TaskInstanceDao;
import cn.exrick.xboot.modules.task.entity.TaskInstance;
import cn.exrick.xboot.modules.task.service.TaskInstanceService;
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
 * 任务实例接口实现
 * @author Feng
 */
@Slf4j
@Service
@Transactional
public class TaskInstanceServiceImpl implements TaskInstanceService {

    @Autowired
    private TaskInstanceDao taskInstanceDao;

    @Override
    public TaskInstanceDao getRepository() {
        return taskInstanceDao;
    }

    @Override
    public Page<TaskInstance> findByCondition(TaskInstance taskInstance, SearchVo searchVo, Pageable pageable) {

        return taskInstanceDao.findAll(new Specification<TaskInstance>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TaskInstance> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                // TODO 可添加你的其他搜索过滤条件 默认已有创建时间过滤
                Path<Date> createTimeField=root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                //创建时间
                if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
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

    /**
     *
     * @return
     */
    public TaskInstance get(String id){
        TaskInstance instance =  getRepository().findById(id).orElse(null);
        setExecuteNodeSet(instance);
        return instance;

    }

    /**
     * 保存
     * @param taskInstance
     * @return
     */
    @Override
    public TaskInstance save(TaskInstance taskInstance) {
        taskInstance.setExecuteNodes(String.join("','", taskInstance.getExecuteNodeSet()));
        return getRepository().save(taskInstance);
    }

    private void setExecuteNodeSet(TaskInstance instance) {
        if (StringUtils.isNotEmpty(instance.getExecuteNodes())) {
            String nodes[] = instance.getExecuteNodes().trim().split(",");
            instance.setExecuteNodeSet(new HashSet<>(Arrays.asList(nodes)));
        } else {
            instance.setExecuteNodeSet(Sets.newHashSet());
        }
    }
}