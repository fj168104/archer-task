package cn.exrick.xboot.modules.task.engine;

/**
 * Created by feng on 2019/9/5 0005
 * 任务信号量
 */
public interface TaskSemphone<T> {

	T getKey();

	boolean equals(Object o);

}
