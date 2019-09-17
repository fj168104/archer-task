package cn.exrick.xboot.modules.task.serviceimpl;

import cn.exrick.xboot.modules.task.dao.TaskUnitDefinitionDao;
import cn.exrick.xboot.modules.task.entity.TaskUnitDefinition;
import cn.exrick.xboot.modules.task.service.TaskUnitDefinitionService;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Field;

/**
 * 任务单元定义接口实现
 * @author Feng
 */
@Slf4j
@Service
@Transactional
public class TaskUnitDefinitionServiceImpl implements TaskUnitDefinitionService {

    @Autowired
    private TaskUnitDefinitionDao taskUnitDefinitionDao;

    @Override
    public TaskUnitDefinitionDao getRepository() {
        return taskUnitDefinitionDao;
    }

    @Override
    public Page<TaskUnitDefinition> findByCondition(TaskUnitDefinition taskUnitDefinition, SearchVo searchVo, Pageable pageable) {

        return taskUnitDefinitionDao.findAll(new Specification<TaskUnitDefinition>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TaskUnitDefinition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

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
}