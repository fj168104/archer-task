package cn.exrick.xboot.modules.task.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Feng
 */
@Data
@Entity
@Table(name = "t_task_unit_definition")
@TableName("t_task_unit_definition")
@ApiModel(value = "任务单元定义")
public class TaskUnitDefinition extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

}