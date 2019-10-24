package cn.exrick.xboot.modules.task.engine.memory;

/**
 * Created by feng on 2019/9/8 0008
 */
public class MemoryTaskTest {
	public static void main(String[] s) throws Exception {
		MemoryTaskFlow taskFlow = new MemoryTaskFlow();
		taskFlow.loadTask();
		taskFlow.start();
	}
}
