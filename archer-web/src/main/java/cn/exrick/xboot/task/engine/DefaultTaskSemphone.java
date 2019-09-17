package cn.exrick.xboot.task.engine;

/**
 * Created by feng on 2019/9/7 0007
 */
public class DefaultTaskSemphone<T> implements TaskSemphone {

	private T key;

	public DefaultTaskSemphone(T key){
		this.key = key;
	}

	@Override
	public T getKey() {
		return this.key;
	}

	@Override
	public boolean equals(Object o){
		if(o == null || !(o instanceof TaskSemphone)){
			return false;
		}
		return ((TaskSemphone) o).getKey().equals(key);
	}

	@Override
	public String toString(){
		return key.toString();
	}
}
