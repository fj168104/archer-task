package cn.exrick.xboot.modules.your.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * @author rsu
 */
@Data
@Entity
@Table(name = "b_database_config")
@TableName("b_database_config")
@ApiModel(value = "数据源配置信息")
public class DatabaseConfig extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @ApiModelProperty(value = "数据源编号")
    @Column(unique = true, length = 40, nullable = false)
    private String id;

    @ApiModelProperty(value = "名称")
    @Column(nullable = false, length = 80)
    private String name;

    @ApiModelProperty(value = "数据库类型")
    @Column(nullable = false, length = 10)
    private String type;

    @ApiModelProperty(value = "驱动名称")
    @Column(nullable = false, length = 100)
    private String driverClassName;

    @ApiModelProperty(value = "url")
    @Column(nullable = false, length = 100)
    private String url;

    @ApiModelProperty(value = "用户名")
    @Column(nullable = false, length = 40)
    private String username;

    @ApiModelProperty(value = "密码")
    @Column(nullable = false, length = 80)
    private String password;

    @ApiModelProperty(value = "初始连接数")
    private Integer initialSize;

    @ApiModelProperty(value = "最大连接数")
    private Integer maxActive;

    @ApiModelProperty(value = "最大建立连接等待时间")
    private Integer maxWait;

    @ApiModelProperty(value = "状态")
    @Transient
    private String status;

}