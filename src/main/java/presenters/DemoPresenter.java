package presenters;

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.GraphAdapter;
import algorithms.Algorithm;
import algorithms.AlgorithmManager;


public class DemoPresenter extends Presenter implements Observer {

	public interface Demonstrator {
		public void addEvent(String ev);
		public void clearEvents();
		public void setGraph(GraphAdapter graph);
		public String getSelectedAlgorithm();
	}

	private Demonstrator demonstrator;

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

	public DemoPresenter(Demonstrator d) {
		super();
		demonstrator = d;
		history = new Stack<GraphAdapter>();
		future = new Stack<GraphAdapter>();
		executor = Executors.newFixedThreadPool(1);
		algManager = new AlgorithmManager();
		presentGraph = null;
	}

	public void start(GraphAdapter graph) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// Called on demo button selected/deselected
		if(alg == null) {
			alg = algManager.getAlgorithm(demonstrator.getSelectedAlgorithm());
			alg.addObserver(this);
			presentGraph = graph;
			//history.push(graph);
			alg.setGraph(graph.getGraph());
		} else {
			stopDemo();
		}

	}

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
				event.append(key+" ");
				event.append(this.alg.getProperty(key)+"\n");
			}
			this.alg.clearProperties();
			demonstrator.addEvent(event.toString());
		}

	}

	public Object[] getAlgorithms() {
		return algManager.getAlgorithms().toArray();
	}

}
