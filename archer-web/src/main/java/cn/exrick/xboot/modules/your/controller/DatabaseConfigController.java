package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.datasource.DBUtil;
import cn.exrick.xboot.modules.your.entity.DatabaseConfig;
import cn.exrick.xboot.modules.your.service.DatabaseConfigService;
import com.alibaba.druid.pool.DruidDataSource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author rsu
 */
@Slf4j
@RestController
@Api(description = "数据源配置信息管理接口")
@RequestMapping("/xboot/databaseConfig")
@Transactional
public class DatabaseConfigController extends XbootBaseController<DatabaseConfig, String> {

    @Autowired
    private DatabaseConfigService databaseConfigService;

    @Override
    public DatabaseConfigService getService() {
        return databaseConfigService;
    }

    @RequestMapping(value = "/initpools", method = RequestMethod.POST)
    @ApiOperation(value = "初始化所有连接池")
    public Result<Object> initpools() throws Exception{
        List<DatabaseConfig> list = databaseConfigService.getAll();
        for (DatabaseConfig dc : list) {
            DBUtil.addPool(dc);
        }
        return new ResultUtil<Object>().setSuccessMsg("初始化成功");
    }

    @RequestMapping(value = "/initpool", method = RequestMethod.POST)
    @ApiOperation(value = "初始化单个连接池")
    public Result<Object> initpool(@RequestParam(required = true) String id) throws Exception{
        DatabaseConfig databaseConfig = databaseConfigService.get(id);
        DBUtil.addPool(databaseConfig);

        return new ResultUtil<Object>().setSuccessMsg("初始化成功");
    }

    @RequestMapping(value = "/closepool", method = RequestMethod.POST)
    @ApiOperation(value = "关闭单个连接池")
    public Result<Object> closepool(@RequestParam(required = true) String id) throws Exception{
        DBUtil.getPool(id).close();

        return new ResultUtil<Object>().setSuccessMsg("关闭成功");
    }

    @RequestMapping(value = "/restartpool", method = RequestMethod.POST)
    @ApiOperation(value = "启用单个连接池")
    public Result<Object> restartpool(@RequestParam(required = true) String id) throws Exception{
        DBUtil.getPool(id).restart();

        return new ResultUtil<Object>().setSuccessMsg("启用成功");
    }

    @RequestMapping(value = "/getByCondition", method = RequestMethod.POST)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<DatabaseConfig>> getByCondition(@ModelAttribute DatabaseConfig databaseConfig,
                                                            @ModelAttribute PageVo pageVo){
        Page<DatabaseConfig> page = databaseConfigService.findByCondition(databaseConfig, PageUtil.initPage(pageVo));
        Iterator<DatabaseConfig> it = page.iterator();
        while(it.hasNext()) {
            DatabaseConfig dc = it.next();
            String sId = dc.getId();
            DruidDataSource dds = DBUtil.getPool(sId);
            if (dds == null) {
                dc.setStatus("未初始化");
            } else if (dds.isClosed()) {
                dc.setStatus("已关闭");
            } else if (dds.isEnable()) {
                dc.setStatus("已启用");
            }
        }
        return new ResultUtil<Page<DatabaseConfig>>().setData(page);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Object> add(@ModelAttribute DatabaseConfig databaseConfig) throws Exception{
        databaseConfigService.save(databaseConfig);
        DBUtil.addPool(databaseConfig);

        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Object> edit(@ModelAttribute DatabaseConfig databaseConfig) throws Exception{
        databaseConfigService.update(databaseConfig);
        DBUtil.updatePool(databaseConfig);

        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }

    @RequestMapping(value = "/delById/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除")
    public Result<Object> delById(@PathVariable String id){
        DBUtil.deletePool(id);
        databaseConfigService.delete(id);

        return new ResultUtil<Object>().setSuccessMsg("通过id删除数据成功");
    }

    @RequestMapping(value = "/mydelByIds/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "自定义批量通过id删除")
    public Result<Object> mydelAllByIds(@PathVariable String[] ids){
        for(String id:ids){
            DBUtil.deletePool(id);
            getService().delete(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }
}
