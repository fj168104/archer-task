package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.your.entity.DatabaseConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 数据源配置信息接口
 * @author rsu
 */
public interface DatabaseConfigService extends XbootBaseService<DatabaseConfig,String> {

    /**
    * 多条件分页获取
    * @param databaseConfig
    * @param pageable
    * @return
    */
    Page<DatabaseConfig> findByCondition(DatabaseConfig databaseConfig, Pageable pageable);
}