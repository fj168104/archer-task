package cn.exrick.xboot.task.engine.memory;

import cn.exrick.xboot.task.engine.TaskFlowAdapter;
import cn.exrick.xboot.task.engine.TaskUnit;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.RandomUtil;
import com.google.api.client.util.Sets;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by feng on 2019/9/5 0005
 */
public class MemoryTaskFlow extends TaskFlowAdapter {

	/**
	 * 执行器
	 */
	private ExecutorService executor;
	/**
	 * 控制任务等待
	 */
	private boolean isWaiting;

	/**
	 *
	 */
	private MemoryTaskNode rootNode;

	private ConcurrentHashSet<MemoryTaskNode> executingTaskNode = new ConcurrentHashSet<>();

	@Override
	public void loadTask() {
		rootNode = createTask();
		executor = Executors.newFixedThreadPool(5);
	}

	@Override
	public void start() {
		Set<MemoryTaskNode> executableNodes = process(rootNode);
		while (!CollectionUtils.isEmpty(executableNodes)) {
			Set<MemoryTaskNode> executableNodeTemps = Sets.newHashSet();
			executableNodes.forEach(item -> {
				executableNodeTemps.addAll(process(item));
			});
			executableNodes = executableNodeTemps;
		}
	}

	@Override
	public void recovery() {
		isWaiting = false;
	}

	@Override
	public void suspend() {
		isWaiting = true;
	}

	@Override
	public void kill() {
		executor.shutdownNow();
	}

	@Override
	public Set<MemoryTaskNode> queryPhase() {
		return executingTaskNode;
	}


	private Set<MemoryTaskNode> process(MemoryTaskNode node) {
		Set<MemoryTaskNode> executableNodes = Sets.newHashSet();
//		if (CollectionUtils.isEmpty(node.getNextNodes())) {
//			System.out.println("The task flow done!");
//			return executableNodes;
//		}
		//execute
		waitFor();
		if (executor.isShutdown()) return executableNodes;
		executingTaskNode.add(node);
		Future<TaskUnit.ExecuteResult> future = executor.submit(new Callable<TaskUnit.ExecuteResult>() {
			@Override
			public TaskUnit.ExecuteResult call() {
				if (node.getTaskUnit() == null) return TaskUnit.ExecuteResult.SUCCESS;
				return node.getTaskUnit().execute();
			}
		});
		TaskUnit.ExecuteResult result = null;
		try {
			result = future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return executableNodes;
		} finally {
			executingTaskNode.remove(node);
		}

		if (result == TaskUnit.ExecuteResult.FAIL) {
			System.err.println("The task flow fail!");
			return executableNodes;
		}
		node.getNextNodes().forEach(item -> {
			//传递信号量
			node.next();
			//判断信号量是否以准备好，如果是则执行任务
			if (item.isReady()) executableNodes.add((MemoryTaskNode) item);
		});
		return executableNodes;
	}

	private void waitFor() {
		while (isWaiting) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private MemoryTaskNode createTask() {
		MemoryTaskNode rootNode = this.createStartNode();
		MemoryTaskNode taskNode1 = createNode("任务1");
		rootNode.addNextNode(taskNode1);

		MemoryTaskNode taskNode2 = createNode("任务2");
		MemoryTaskNode taskNode3 = createNode("任务3");
		taskNode1.addNextNode(taskNode2).addNextNode(taskNode3);

		MemoryTaskNode taskNode4 = createNode("任务4");
		taskNode2.addNextNode(taskNode4);
		taskNode3.addNextNode(taskNode4);

		MemoryTaskNode endNode = this.createEndNode();
		taskNode4.addNextNode(endNode);

		return rootNode;
	}

	private MemoryTaskNode createNode(String taskName) {
		MemoryTaskUnit taskUnit = new MemoryTaskUnit(taskName) {

			@Override
			public ExecuteResult execute() {
				System.out.println(taskName + "任务启动");
				for (int i = 0; i < RandomUtil.randomInt(5); i++) {
					System.out.println(taskName + "任务执行中...");
					try {
						waitFor();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(taskName + "任务完成");
				return ExecuteResult.SUCCESS;
			}
		};
		return new MemoryTaskNode(taskUnit);
	}

	private MemoryTaskNode createStartNode() {
		return new MemoryTaskNode(new MemoryTaskUnit("开始节点") {
			@Override
			public ExecuteResult execute() {
				return ExecuteResult.SUCCESS;
			}
		});
	}

	private MemoryTaskNode createEndNode() {
		return new MemoryTaskNode(new MemoryTaskUnit("结束节点") {
			@Override
			public ExecuteResult execute() {
				System.out.println("The task flow done!");
				executor.shutdownNow();
				return ExecuteResult.SUCCESS;
			}
		});
	}
}
