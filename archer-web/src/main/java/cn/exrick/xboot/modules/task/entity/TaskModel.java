package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import cn.exrick.xboot.common.constant.CommonConstant;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Exrick
 */
@Data
@Entity
@Table(name = "t_task_model")
@TableName("t_task_model")
@ApiModel(value = "任务模型")
public class TaskModel extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "标识")
    private String modelKey;

    @ApiModelProperty(value = "版本号")
    private Integer version = 1;

    @ApiModelProperty(value = "描述/备注")
    private String description;

    @ApiModelProperty(value = "是否发布")
    private Boolean release= Boolean.FALSE;

    @ApiModelProperty(value = "是否启用 0启用 -1禁用")
    private Integer modelStatus = CommonConstant.STATUS_NORMAL;

    @Column(columnDefinition="TEXT COMMENT '流程XML'")
    @ApiModelProperty(value = "流程XML")
    private String processXml;

}
