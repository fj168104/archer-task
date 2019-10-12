package cn.exrick.xboot.modules.datasource;

import cn.exrick.xboot.modules.your.entity.DatabaseConfig;
import com.alibaba.druid.pool.DruidDataSource;

import java.util.concurrent.ConcurrentHashMap;

public class DBUtil {

  private static ConcurrentHashMap<String, DruidDataSource> DBList = new ConcurrentHashMap<String, DruidDataSource>();
  private static ConcurrentHashMap<String, DatabaseConfig> ConfigList = new ConcurrentHashMap<String, DatabaseConfig>();

  /**
   * 新增数据库连接池
   * @param databaseConfig
   * @throws Exception
   */
  public static void addPool(DatabaseConfig databaseConfig) throws Exception{
    String sId = databaseConfig.getId();
    DruidDataSource dds = new DruidDataSource();
    dds.setDriverClassName(databaseConfig.getDriverClassName());
    dds.setUrl(databaseConfig.getUrl());
    dds.setUsername(databaseConfig.getUsername());
    dds.setPassword(databaseConfig.getPassword());
    dds.setMaxActive(databaseConfig.getMaxActive());
    dds.setInitialSize(databaseConfig.getInitialSize());
    dds.init();

    DBList.put(sId, dds);
    ConfigList.put(sId, databaseConfig);
  }

  /**
   * 修改数据库连接池
   * 1、先停用之前的连接池
   * 2、再重新生成新的连接池
   * @param databaseConfig
   * @throws Exception
   */
  public static void updatePool(DatabaseConfig databaseConfig) throws Exception{
    String sId = databaseConfig.getId();
    DruidDataSource olddds = DBList.get(sId);
    /*if (olddds != null) {
      olddds.close();
    }

    DruidDataSource dds = new DruidDataSource();
    dds.setDriverClassName(databaseConfig.getDriverClassName());
    dds.setUrl(databaseConfig.getUrl());
    dds.setUsername(databaseConfig.getUsername());
    dds.setPassword(databaseConfig.getPassword());
    dds.setMaxActive(databaseConfig.getMaxActive());
    dds.setInitialSize(databaseConfig.getInitialSize());
    dds.init();

    DBList.put(sId, dds);
    */

    if (olddds != null) {
      olddds.close();
      olddds.setDriverClassName(databaseConfig.getDriverClassName());
      olddds.setUrl(databaseConfig.getUrl());
      olddds.setUsername(databaseConfig.getUsername());
      olddds.setPassword(databaseConfig.getPassword());
      olddds.setMaxActive(databaseConfig.getMaxActive());
      olddds.setInitialSize(databaseConfig.getInitialSize());
      olddds.restart();
    } else {
      olddds = new DruidDataSource();
      olddds.setDriverClassName(databaseConfig.getDriverClassName());
      olddds.setUrl(databaseConfig.getUrl());
      olddds.setUsername(databaseConfig.getUsername());
      olddds.setPassword(databaseConfig.getPassword());
      olddds.setMaxActive(databaseConfig.getMaxActive());
      olddds.setInitialSize(databaseConfig.getInitialSize());
      olddds.init();
    }

    DBList.put(sId, olddds);
    ConfigList.put(sId, databaseConfig);
  }

  public static void deletePool(String id) {
    DruidDataSource olddds = DBList.get(id);
    if (olddds != null) {
      olddds.close();
      DBList.remove(id);
      ConfigList.remove(id);
    }
  }

  /**
   * 根据id获取连接池
   * @param id
   * @return
   */
  public static DruidDataSource getPool(String id) {
    return DBList.get(id);
  }

  /**
   * 根据id获取连接池配置信息
   * @param id
   * @return
   */
  public static DatabaseConfig getConfig(String id) {
    return ConfigList.get(id);
  }
}
