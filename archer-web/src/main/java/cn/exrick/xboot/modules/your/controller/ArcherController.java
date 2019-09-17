package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.base.XbootBaseController;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.your.entity.Archer;
import cn.exrick.xboot.modules.your.service.ArcherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "测试管理接口")
@RequestMapping("/xboot/archer")
@Transactional
public class ArcherController extends XbootBaseController<Archer, String> {

    @Autowired
    private ArcherService archerService;

    @Override
    public ArcherService getService() {
        return archerService;
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<Archer>> getByCondition(@ModelAttribute Archer archer,
                                                            @ModelAttribute SearchVo searchVo,
                                                            @ModelAttribute PageVo pageVo){

        Page<Archer> page = archerService.findByCondition(archer, searchVo, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<Archer>>().setData(page);
    }

}
