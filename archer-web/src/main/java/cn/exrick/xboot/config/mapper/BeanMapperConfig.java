package cn.exrick.xboot.config.mapper;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by feng on 2019/11/4 0004
 */
@Configuration
public class BeanMapperConfig {

	@Bean
	public Mapper mapper(){
		return DozerBeanMapperBuilder.buildDefault();
	}
}
