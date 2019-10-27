package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import cn.exrick.xboot.common.constant.TaskConstant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.api.client.util.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

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

    @ApiModelProperty(value = "任务状态")
    private Integer status = TaskConstant.TASK_STATUS_UNSTART;

    @ApiModelProperty(value = "任务执行节点集合")
    private String executeNodes;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "任务执行节点集合Set")
    private Set<String> executeNodeSet = Sets.newHashSet();
}
