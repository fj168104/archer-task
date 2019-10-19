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
	 * 任务已开始
	 */
	Integer INSTANCE_STARTED = 0;

	/**
	 * 任务未开启
	 */
	Integer INSTANCE_UNSTART = -1;
	/**
	 * 任务已结束
	 */
	Integer INSTANCE_FINISH = 0;

	/**
	 * 任务未结束
	 */
	Integer INSTANCE_UNFINISH = -1;
	/**
	 * 任务pending中
	 */
	Integer INSTANCE_PENDING = 0;

	/**
	 * 任务未pending中
	 */
	Integer INSTANCE_UNPENDING = -1;
	/**
	 * 流程已结束
	 */
	Integer PROCESS_FINISH = 0;

	/**
	 * 流程未结束
	 */
	Integer PROCESS_UNFINISH = -1;

}
