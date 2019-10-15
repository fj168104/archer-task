package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import cn.exrick.xboot.common.constant.CommonConstant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Feng
 */
@Data
@Entity
@Table(name = "t_task_instance")
@TableName("t_task_instance")
@ApiModel(value = "任务实例")
public class TaskInstance extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型Id")
    private String modelId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "描述/备注")
    private String description;

    @ApiModelProperty(value = "是否开启")
    private Boolean started;

    @ApiModelProperty(value = "是否结束")
    private Boolean finished;

    @ApiModelProperty(value = "是否停滞状态/人工处理中")
    private Boolean pending;

    @ApiModelProperty(value = "任务执行节点")
    private String executeNode;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "任务执行节点")
    private String executeNodeName;
}
