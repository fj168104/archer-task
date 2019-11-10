package cn.exrick.xboot.modules.task.engine.db;

import cn.hutool.core.util.RandomUtil;

/**
 * Created by feng on 2019/11/5 0005
 */
public class AutoTaskTest implements IAutoTask{
	@Override
	public void run() throws Exception {
		System.out.print("任务运行开始。。。。。。");
		Thread.sleep(RandomUtil.randomInt(5) * 1000);
		System.out.print("任务运行结束。。。。。。");
	}
}
