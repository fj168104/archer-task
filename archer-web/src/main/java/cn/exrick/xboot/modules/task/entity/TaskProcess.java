package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

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

    @ApiModelProperty(value = "下一任务执行节点")
    private String nextExecuteNode;

    @ApiModelProperty(value = "运行结果/下一任务所需参数")
    private String runResult;

    @ApiModelProperty(value = "是否结束")
    private Boolean finished;

    @ApiModelProperty(value = "执行异常")
    private Boolean exception;

    @ApiModelProperty(value = "异常描述")
    private String errorMessage;
}
