package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import cn.exrick.xboot.common.constant.TaskConstant;
import cn.exrick.xboot.modules.task.engine.db.DBTaskUnit;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.api.client.util.Sets;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

/**
 * @author Exrick
 */
@Data
@Entity
@Table(name = "t_task_process")
@TableName("t_task_process")
@ApiModel(value = "任务流程明细")
public class TaskProcess extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务实例Id")
    private String taskId;

    @ApiModelProperty(value = "任务执行节点")
    private String executeNode;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "收集的信号量Set")
    private Set<String> nodeSemphoneSet = Sets.newHashSet();

    @ApiModelProperty(value = "收集的信号量")
    private String nodeSemphones;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "上一任务执行节点Set")
    private Set<String> preExecuteNodesSet = Sets.newHashSet();

    @ApiModelProperty(value = "上一任务执行节点")
    private String preExecuteNodes;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "下一任务执行节点Set")
    private Set<String> nextExecuteNodeSet = Sets.newHashSet();

    @ApiModelProperty(value = "下一任务执行节点")
    private String nextExecuteNodes;

    @ApiModelProperty(value = "运行结果/下一任务所需参数")
    private String runResult;

    @ApiModelProperty(value = "流程状态")
    private Integer status = TaskConstant.TASK_STATUS_PENDING;

    @ApiModelProperty(value = "执行异常")
    private Boolean exception=Boolean.FALSE;

    @ApiModelProperty(value = "异常描述")
    private String errorMessage;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "节点执行单元")
    private DBTaskUnit taskUnit;
}
