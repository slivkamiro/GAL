package com.slivkam.graphdemonstrator.presenters;

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import com.slivkam.graphdemonstrator.algorithms.Algorithm;
import com.slivkam.graphdemonstrator.algorithms.AlgorithmManager;
import com.slivkam.graphdemonstrator.model.GraphAdapter;
import com.slivkam.graphdemonstrator.views.GraphDemonstratorView;

/**
 *
 * @author Miroslav
 *
 */
public class DemoPresenter extends Presenter implements Observer {

	/**
	 *
	 * @author Miroslav
	 * Interface that every view that this presenter manage must implement.
	 */
	public interface Demonstrator {

		PresenterFactory getPresenterFactory();

		/**
		 * Publish events.
		 * @param ev
		 */
		public void addEvent(String ev);

		/**
		 * Clear published events.
		 */
		public void clearEvents();

		/**
		 * Publish graph.
		 * @param graph
		 */
		public void setGraph(GraphAdapter graph);

		/**
		 * Gets algorithm to be demonstrated.
		 * @return
		 */
		public String getSelectedAlgorithm();
	}

	private Demonstrator demonstrator;

	private boolean starter;

	private GraphAdapter presentGraph;
	private Stack<GraphAdapter> history;

	/**
	 * When going back to history, graphs are pushed here.
	 * Then if there is attempt to step forward, instead of
	 * calling algorithm, graph is poped from here.
	 */
	private Stack<GraphAdapter> future;

	private ExecutorService executor;

	private AlgorithmManager algManager;
	private Algorithm alg = null;

	/**
	 * Constructor.
	 * @param d View that this presenter manages.
	 */
	@Inject
	public DemoPresenter(Demonstrator d) {
		super();
		demonstrator = d;
		history = new Stack<GraphAdapter>();
		future = new Stack<GraphAdapter>();
		executor = Executors.newFixedThreadPool(1);
		algManager = new AlgorithmManager();
		presentGraph = null;
		starter = false;
	}
	
	@Override
	public View getView() {
		return (View) this.demonstrator;
	}

	/**
	 * Initialize algorithm to be demonstrated.
	 * @param graph Graph that algorithm should be demonstrated on.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void start(GraphAdapter graph) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// Called on demo button selected/deselected
		if(alg == null) {
			alg = algManager.getAlgorithm(demonstrator.getSelectedAlgorithm());
			alg.addObserver(this);
			presentGraph = graph;
			//history.push(graph);
			alg.setGraph(graph);
		} else {
			stopDemo();
		}

	}

	/**
	 * Stops demonstration.
	 */
	public void stopDemo() {
		if(alg != null) {
			alg.deleteObserver(this);
			alg = null;
			demonstrator.clearEvents();
			// last element is on top of the stack
			if(history.empty()) {
				demonstrator.setGraph(presentGraph);
			} else {
				// Demonstration stopped in the middle
				demonstrator.setGraph(history.firstElement());
			}
			presentGraph = null;
			history.clear();
			future.clear();
		}

	}

	/**
	 * Do one step of algorithm.
	 */
	public void stepForward() {
		if (alg != null) {
			if (!future.empty()) {
				history.push(presentGraph);
				presentGraph = future.pop();
				demonstrator.setGraph(presentGraph);
			}
			else {
				executor.execute(alg);
			}
		}
	}

	/**
	 * Do one step back in algorithm.
	 */
	public void stepBackward() {
		if (!history.empty()) {
			future.push(presentGraph);
			presentGraph = history.pop();
			demonstrator.setGraph(presentGraph);
		}
	}

	@Override
	public void update(Observable alg, Object o) {
		if (alg.equals(this.alg)) {
			history.push(presentGraph);
			presentGraph = this.alg.getGraph();
			//history.push(this.alg.getGraph());
			demonstrator.setGraph(presentGraph);
			StringBuilder event = new StringBuilder();
			for(String key : this.alg.getPropertiesName()) {
				event.append(key+": ");
				event.append(this.alg.getProperty(key)+"\n");
			}
			this.alg.clearProperties();
			demonstrator.addEvent(event.toString());
		}

	}

	/**
	 * Gets all known algorithms.
	 * @return Array of objects that holds names of algorithms.
	 */
	public Object[] getAlgorithms() {
		return algManager.getAlgorithms().toArray();
	}

	public void showStarterGraph() {
		// last element is on top of the stack
		if(!starter) {
			if(history.empty()) {
				demonstrator.setGraph(presentGraph);
			} else {
				// Demonstration stopped in the middle
				demonstrator.setGraph(history.firstElement());
			}
		} else {
			demonstrator.setGraph(presentGraph);
		}
		starter = !starter;
	}

}
