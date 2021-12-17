package otros;

import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import chunks_NoCeldas.Chunk;
import chunks_NoCeldas.Chunks;

public class Tareas {
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

	public static final ExecutorServiceContainer<ExecutorService> p�xeles = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newCachedThreadPool());
		}
	}, chunks = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newFixedThreadPool(Chunk.CARGA_LIGERA ? 2 * Chunks.PARALELOS : Chunks.PARALELOS+7));
		}
	}, archivos = new ExecutorServiceContainer<ExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newCachedThreadPool());
		}
	};
	public static final ExecutorServiceContainer<ScheduledExecutorService> timers = new ExecutorServiceContainer<ScheduledExecutorService>() {
		@Override
		public void init() {
			setES(Executors.newScheduledThreadPool(Chunk.CARGA_LIGERA ? 2 * Chunks.PARALELOS + 30 : 5)); // La sobrecarga de CPU puede impedir que se lleguen a ejecutar los timers
		}
	};
	
	private Tareas() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	/**
	 * Mediante {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)} monitoriza
	 * la condici�n e interrumpe el hilo monitorizado peri�dicamente si es falsa
	 * @param hilo - hilo que se va a monitorizar
	 * @return la tarea mandada a "timers"
	 */
	public static ScheduledFuture<?> monitorizar(Thread hilo, int periodoMillis, AtomicBoolean condici�n, Object testigo) {
		return timers.getES().scheduleWithFixedDelay(() -> {
			boolean interrumpible = interrumpible(hilo);
			if (interrumpible) {
				// Esperar y volver a comprobar ya que puede ser un falso positivo (entre el cambio de interrumpido a esperando)
				try {
					Thread.sleep(50);
					interrumpible = interrumpible(hilo);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			synchronized (testigo) {
				if (interrumpible && !condici�n.get()) hilo.interrupt();
			}
		}, periodoMillis, periodoMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Cesa la monitorizaci�n del hilo que llama a este m�todo.
	 * Cambia el estado de la condici�n de monitorizaci�n a verdadero
	 * @param future - La tarea que monitoriza al hilo
	 */
	public static void endMonitorizar(ScheduledFuture<?> tarea, AtomicBoolean condici�n, Object testigo) {
		tarea.cancel(true);
		condici�n.set(true);
		synchronized (testigo) {
			Thread.interrupted(); // Limpia el estado residual de interrumpido
		}
	}
	
	/**
	 * Mediante {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)} monitoriza
	 * la condici�n e interrumpe la tarea monitorizada peri�dicamente si es falsa
	 * @return la tarea mandada a "timers"
	 * @deprecated No funciona
	 */
	@Deprecated
	public static ScheduledFuture<?> monitorizar(Future<?> tarea, int periodoMillis, AtomicBoolean condici�n, Object testigo) {
		return timers.getES().scheduleWithFixedDelay(() -> {
			synchronized (testigo) {
				if (!condici�n.get()) tarea.cancel(true); //FIXME no se puede descancelar desde dentro. Una manera es monitorizar el hilo y una vez cumplido esperar a que esto acabe con Future#get(), pero introduce esa espera
			}
		}, periodoMillis, periodoMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @return si se puede interrumpir el hilo, es decir, que no est� esperando ni est� ya interrumpido
	 */
	public static boolean interrumpible(Thread hilo) {
		return !hilo.isInterrupted() && hilo.getState() != Thread.State.TIMED_WAITING && hilo.getState() != Thread.State.WAITING;
	}
	
	/**
	 * Espera a que se completen las tareas
	 * @return el n�mero de tareas canceladas (puede no ser relevante)
	 */
	public static int esperar(Collection<? extends Future<?>> tareas) {
		int canceladas = 0;
		for (Future<?> tarea : tareas) {
			if (tarea != null) try {
				tarea.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CancellationException e) {
				canceladas++;
			}
		}
		return canceladas;
	}
	
}