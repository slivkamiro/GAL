package presenters;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algorithms.Algorithm;
import model.GraphAdapter;


public class DemoPresenter extends Presenter implements Observer {
	
	public interface Demonstrator {
		public void addEvent(String ev);
		public void setGraph(GraphAdapter graph);
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
	
	private Algorithm alg = null;
	
	public DemoPresenter(Demonstrator d) {
		super();
		demonstrator = d;
		history = new Stack<GraphAdapter>();
		future = new Stack<GraphAdapter>();
		executor = Executors.newFixedThreadPool(1);
		//alg = new ChiLiuEdmonds();
		alg.addObserver(this);
	}

	public void start(GraphAdapter graph) {
		history.push(graph);
		alg.setGraph(graph.getGraph());
		//demonstrator.setGraph(_);
	}
	
	public void stepForward(GraphAdapter graph) {
		//history.push(graph);
		if(!future.empty()) {
			history.push(future.peek());
			demonstrator.setGraph(future.pop());
		}
		else {
			executor.execute(alg);
		}
		//demonstrator.setGraph(_);
	}
	
	public void stepBackward() {
		future.push(history.peek());
		demonstrator.setGraph(history.pop());
	}

	public void update(Observable alg, Object o) {
		if(alg.equals(this.alg)) {
			history.push(this.alg.getGraph());
			demonstrator.setGraph(history.peek());
		}
		
	}
	
}
