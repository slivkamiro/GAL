package com.slivkam.graphdemonstrator.presenters;

import java.io.IOException;
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
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Inject
    public DemoPresenter(Demonstrator d) throws ClassNotFoundException, IOException {
        super();
        this.demonstrator = d;
        this.history = new Stack<GraphAdapter>();
        this.future = new Stack<GraphAdapter>();
        this.executor = Executors.newFixedThreadPool(1);
        this.algManager = new AlgorithmManager();
        this.presentGraph = null;
        this.starter = false;
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
        if(this.alg == null) {
            this.alg = this.algManager.getAlgorithm(this.demonstrator.getSelectedAlgorithm());
            this.alg.addObserver(this);
            this.presentGraph = graph;
            //history.push(graph);
            this.alg.setGraph(graph);
            this.updateEvents();
        } else {
            this.stopDemo();
        }

    }

    /**
     * Stops demonstration.
     */
    public void stopDemo() {
        if(this.alg != null) {
            this.alg.deleteObserver(this);
            this.alg = null;
            this.demonstrator.clearEvents();
            // last element is on top of the stack
            if(this.history.empty()) {
                this.demonstrator.setGraph(this.presentGraph);
            } else {
                // Demonstration stopped in the middle
                this.demonstrator.setGraph(this.history.firstElement());
            }
            this.presentGraph = null;
            this.history.clear();
            this.future.clear();
        }

    }

    /**
     * Do one step of algorithm.
     */
    public void stepForward() {
        if (this.alg != null) {
            if (!this.future.empty()) {
                this.history.push(this.presentGraph);
                this.presentGraph = this.future.pop();
                this.demonstrator.setGraph(this.presentGraph);
            }
            else {
                this.executor.execute(this.alg);
            }
        }
    }

    /**
     * Do one step back in algorithm.
     */
    public void stepBackward() {
        if (!this.history.empty()) {
            this.future.push(this.presentGraph);
            this.presentGraph = this.history.pop();
            this.demonstrator.setGraph(this.presentGraph);
        }
    }

    @Override
    public void update(Observable alg, Object o) {
        if (alg.equals(this.alg)) {
            this.history.push(this.presentGraph);
            this.presentGraph = this.alg.getGraph();
            //history.push(this.alg.getGraph());
            this.demonstrator.setGraph(this.presentGraph);
            this.updateEvents();
        }

    }

    private void updateEvents() {
        StringBuilder event = new StringBuilder();
        for(String key : this.alg.getPropertiesName()) {
            event.append(key+": ");
            event.append(this.alg.getProperty(key)+"\n");
        }
        this.alg.clearProperties();
        this.demonstrator.addEvent(event.toString());
    }

    /**
     * Gets all known algorithms.
     * @return Array of objects that holds names of algorithms.
     */
    public Object[] getAlgorithms() {
        return this.algManager.getAlgorithms().toArray();
    }

    public void showStarterGraph() {
        // last element is on top of the stack
        if(!this.starter) {
            if(this.history.empty()) {
                this.demonstrator.setGraph(this.presentGraph);
            } else {
                this.demonstrator.setGraph(this.history.firstElement());
            }
        } else {
            this.demonstrator.setGraph(this.presentGraph);
        }
        this.starter = !this.starter;
    }

}
