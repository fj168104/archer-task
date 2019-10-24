package cn.exrick.xboot.modules.task.engine;

import java.util.Set;

/**
 * Created by feng on 2019/9/5 0005
 */
public class TaskFlowAdapter extends AbsTaskFlow {
	@Override
	public void loadTask() throws Exception{
		throw new UnsupportedOperationException("");
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void recovery() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void suspend() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void kill() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public <T extends TaskNode> Set<T> queryPhase() {
		throw new UnsupportedOperationException("");
	}
}
