package presenters;

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
		public void setGraph(GraphAdapter graph);
		public String getSelectedAlgorithm();
	}

	private Demonstrator demonstrator;

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
	}

	public void start(GraphAdapter graph) {
		// Called on demo button selected/deselected
		if(alg == null) {
			alg = algManager.getAlgorithm(demonstrator.getSelectedAlgorithm());
			alg.addObserver(this);
			history.push(graph);
			alg.setGraph(graph.getGraph());
		} else {
			alg.deleteObserver(this);
			alg = null;
			// last element is on top of the stack
			demonstrator.setGraph(history.firstElement());
			history.clear();
			future.clear();
		}

	}

	public void stepForward() {
		if (!future.empty()) {
			history.push(future.peek());
			demonstrator.setGraph(future.pop());
		}
		else {
			executor.execute(alg);
		}
	}

	public void stepBackward() {
		future.push(history.peek());
		demonstrator.setGraph(history.pop());
	}

	public void update(Observable alg, Object o) {
		if (alg.equals(this.alg)) {
			history.push(this.alg.getGraph());
			demonstrator.setGraph(history.peek());
			StringBuilder event = new StringBuilder();
			for(String key : this.alg.getPropertiesName()) {
				event.append(key+" ");
				event.append(this.alg.getProperty(key)+"\n");
			}
			demonstrator.addEvent(event.toString());
		}

	}

	public Object[] getAlgorithms() {
		return algManager.getAlgorithms().toArray();
	}

}
