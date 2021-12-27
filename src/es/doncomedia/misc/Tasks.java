package es.doncomedia.misc;

import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import es.doncomedia.chunks.Chunk;
import es.doncomedia.chunks.Chunks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Tasks {
	public abstract static class ExecutorServiceContainer<E extends ExecutorService> {
		private E execServ;
		
		protected ExecutorServiceContainer() {
			init();
		}
		
		public abstract void init();
		
		public synchronized E getES() {
			return execServ;
		}
		
		public synchronized void setES(E es) {
			execServ = es;
		}
		
		public synchronized void await() {
			execServ.shutdown();
			try {
				execServ.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} finally {
				init();
			}
		}
	}

	public static final ExecutorServiceContainer<ExecutorService> pixels = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newCachedThreadPool());
		}
	}, chunks = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newFixedThreadPool(Chunk.LIGHTWEIGHT_LOAD ? 2 * Chunks.PARALLEL : Chunks.PARALLEL+7));
		}
	}, files = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newCachedThreadPool());
		}
	};
	public static final ExecutorServiceContainer<ScheduledExecutorService> timers = new ExecutorServiceContainer<ScheduledExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newScheduledThreadPool(Chunk.LIGHTWEIGHT_LOAD ? 2 * Chunks.PARALLEL + 30 : 5)); // CPU overload may prevent timer execution
		}
	};
	
	private Tasks() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	/**
	 * Using {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)} monitors
	 * the condition and interrupts the monitored thread periodically if it's false
	 * @param thread - thread to be monitored
	 * @return the task sent to "timers"
	 */
	public static ScheduledFuture<?> monitor(Thread thread, int periodMillis, AtomicBoolean condition, Object monitor) {
		return timers.getES().scheduleWithFixedDelay(() -> {
			boolean interruptible = interruptible(thread);
			if (interruptible) {
				// Wait and check again since it can be a false positive (while changing between interrupted and waiting)
				try {
					Thread.sleep(50);
					interruptible = interruptible(thread);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			synchronized (monitor) {
				if (interruptible && !condition.get()) thread.interrupt();
			}
		}, periodMillis, periodMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Ceases monitorization of the thread calling this method.
	 * Changes the monitoring condition's state to true
	 * @param future - the task monitoring this thread
	 */
	public static void endMonitoring(ScheduledFuture<?> task, AtomicBoolean condition, Object monitor) {
		task.cancel(true);
		condition.set(true);
		synchronized (monitor) {
			Thread.interrupted(); // Cleans residual interrupted state
		}
	}
	
	/**
	 * Using {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)} monitors
	 * the condition and interrupts the monitored task periodically if it's false
	 * @return the task sent to "timers"
	 * @deprecated Doesn't work
	 */
	@Deprecated
	public static ScheduledFuture<?> monitor(Future<?> task, int periodMillis, AtomicBoolean condition, Object monitor) {
		return timers.getES().scheduleWithFixedDelay(() -> {
			synchronized (monitor) {
				if (!condition.get()) task.cancel(true); //FIXME no se puede descancelar desde dentro. Una manera es monitorizar el hilo y una vez cumplido esperar a que esto acabe con Future#get(), pero introduce esa espera
			}
		}, periodMillis, periodMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @return whether the thread can be interrupted, meaning it isn't waiting nor is it already interrupted
	 */
	public static boolean interruptible(Thread thread) {
		return !thread.isInterrupted() && thread.getState() != Thread.State.TIMED_WAITING && thread.getState() != Thread.State.WAITING;
	}
	
	/**
	 * Waits until the tasks are done
	 * @return the number of cancelled tasks (may not be relevant)
	 */
	public static int await(Collection<? extends Future<?>> tasks) {
		int cancelled = 0;
		for (Future<?> task : tasks) {
			if (task != null) try {
				task.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CancellationException e) {
				cancelled++;
			}
		}
		return cancelled;
	}
	
}