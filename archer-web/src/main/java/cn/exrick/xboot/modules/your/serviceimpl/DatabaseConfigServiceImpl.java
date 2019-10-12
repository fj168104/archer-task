package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.dao.DatabaseConfigDao;
import cn.exrick.xboot.modules.your.entity.DatabaseConfig;
import cn.exrick.xboot.modules.your.service.DatabaseConfigService;
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
import java.util.List;

/**
 * 数据源配置信息接口实现
 * @author rsu
 */
@Slf4j
@Service
@Transactional
public class DatabaseConfigServiceImpl implements DatabaseConfigService {

    @Autowired
    private DatabaseConfigDao databaseConfigDao;

    @Override
    public DatabaseConfigDao getRepository() {
        return databaseConfigDao;
    }

    @Override
    public Page<DatabaseConfig> findByCondition(DatabaseConfig databaseConfig, Pageable pageable) {

        return databaseConfigDao.findAll(new Specification<DatabaseConfig>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<DatabaseConfig> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Path<String> typeField = root.get("type");
                Path<String> nameField = root.get("name");

                List<Predicate> list = new ArrayList<Predicate>();
                String sType = databaseConfig.getType();
                if(StrUtil.isNotBlank(sType)){
                    list.add(cb.equal(typeField, sType));
                }
                String sName = databaseConfig.getName();
                if(StrUtil.isNotBlank(sName)){
                    list.add(cb.like(nameField, '%' + sName + '%'));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }
}