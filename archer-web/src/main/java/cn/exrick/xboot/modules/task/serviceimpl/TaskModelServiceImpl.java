package cn.exrick.xboot.modules.task.serviceimpl;

import cn.exrick.xboot.modules.task.dao.TaskModelDao;
import cn.exrick.xboot.modules.task.entity.TaskModel;
import cn.exrick.xboot.modules.task.service.TaskModelService;
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
 * 任务模型接口实现
 * @author Exrick
 */
@Slf4j
@Service
//@Transactional
public class TaskModelServiceImpl implements TaskModelService {

    @Autowired
    private TaskModelDao taskModelDao;

    @Override
    public TaskModelDao getRepository() {
        return taskModelDao;
    }

    @Override
    public Page<TaskModel> findByCondition(TaskModel taskModel, SearchVo searchVo, Pageable pageable) {

        return taskModelDao.findAll(new Specification<TaskModel>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<TaskModel> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                // TODO 可添加你的其他搜索过滤条件 默认已有创建时间过滤
                Path<Date> createTimeField=root.get("createTime");
                Path<Boolean> modelReleaseField=root.get("modelRelease");

                List<Predicate> list = new ArrayList<Predicate>();

                //创建时间
                if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }
                list.add(cb.equal(modelReleaseField, false));
                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public List<TaskModel> findByModelKey(String modelKey) {
        return taskModelDao.findByModelKey(modelKey);
    }

    @Override
    public List<TaskModel> findByModelRelease(boolean release) {
        return taskModelDao.findByModelRelease(release);
    }
}
