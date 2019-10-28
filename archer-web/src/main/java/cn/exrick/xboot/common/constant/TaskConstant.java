package cn.exrick.xboot.common.constant;

/**
 * Created by feng on 2019/10/19 0019
 */
public interface TaskConstant {
    /**
     * 模型已发布
     */
    Integer MODEL_RELEASE = 0;

    /**
     * 模型未发布
     */
    Integer MODEL_UNRELEASE = -1;
    /**
     * 任务未开启
     */
    Integer TASK_STATUS_UNSTART = 0;

    /**
     * 任务已运行
     */
    Integer TASK_STATUS_RUNNING = 1;

    /**
     * 任务pending中
     */
    Integer TASK_STATUS_PENDING = 2;

    /**
     * 任务结束
     */
    Integer TASK_STATUS_FINISHED = 3;


    /**
     * 节点已运行
     */
    Integer NODE_STATUS_RUNNING = 0;

    /**
     * 节点pending中
     */
    Integer NODE_STATUS_PENDING = 1;

    /**
     * 节点结束
     */
    Integer NODE_STATUS_FINISHED = 2;
}
