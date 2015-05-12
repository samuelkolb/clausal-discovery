package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by samuelkolb on 09/05/15.
 *
 * @author Samuel Kolb
 */
public class ParallelCalculator<R> {

	//region Variables

	private List<Future<R>> futures = new ArrayList<>();

	private ExecutorService executorService;
	//endregion

	//region Construction

	//endregion

	//region Public methods

	/**
	 * Add a callable
	 * @param callable	The callable to be added
	 */
	public synchronized void add(Callable<R> callable) {
		if(this.executorService == null)
			this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.futures.add(executorService.submit(callable));
	}

	/**
	 * Returns the element at the given index
	 * @param index	The index
	 * @return	The element at the given index, awaiting the execution of the responsible callable if necessary
	 */
	public synchronized R get(int index) {
		try {
			return this.futures.get(index).get();
		} catch(InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Shuts down the calculator and collects all results added so far
	 * @return	A list of objects created by the callables added (in the order that they were added)
	 */
	public synchronized List<R> retrieveAll() {
		if(this.executorService != null) {
			this.executorService.shutdown();
			try {
				this.executorService.awaitTermination(10, TimeUnit.DAYS);
			} catch(InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
		List<R> list = new ArrayList<>(this.futures.size());
		for(int i = 0; i < this.futures.size(); i++)
			list.add(get(i));
		this.executorService = null;
		return list;
	}

	//endregion
}
