package com.kv.crawler;

/**
 * Interface to get callbacks on thread state - current task,finished,paused
 * 
 * @author hadoopninja
 * 
 */
public interface IThreadStateReceiver {
	public void executingTask(Object task, int threadId);

	public void finished(int threadId);
}
