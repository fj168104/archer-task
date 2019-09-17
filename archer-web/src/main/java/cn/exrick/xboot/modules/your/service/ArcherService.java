package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.your.entity.Archer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.exrick.xboot.common.vo.SearchVo;

import java.util.List;

/**
 * 测试接口
 * @author Exrick
 */
public interface ArcherService extends XbootBaseService<Archer,String> {

    /**
    * 多条件分页获取
    * @param archer
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<Archer> findByCondition(Archer archer, SearchVo searchVo, Pageable pageable);
}