package com.slivkam.graphdemonstrator.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.slivkam.graphdemonstrator.model.EdgeAdapter;
import com.slivkam.graphdemonstrator.model.VertexAdapter;

public class BreadthFirstSearch extends Algorithm{

    private LinkedList<Integer> queue;

    private List<Integer> closed;

    public BreadthFirstSearch() {
        super();
        this.queue = new LinkedList<Integer>();
        this.closed = new ArrayList<Integer>();
    }

    @Override
    public void init() {
        this.queue.add(Integer.valueOf(this.graph.getRootVertex().getId()));
        this.addProperty("Queue: ", this.queue.toString());
        this.addProperty("Closed: ", this.closed.toString());
    }

    @Override
    protected void doStep() {
        if (this.queue.isEmpty()) this.endAlgorithm();

        // Select next working node that was not closed yet
        Integer wNode = null;
        do {
            wNode = this.queue.removeFirst();
        } while(this.closed.contains(wNode));
        this.addProperty("Queue: ", this.queue.toString());
        this.highlightVertex(wNode);
        this.closed.add(wNode);
        this.addProperty("Closed: ", this.closed.toString());
        VertexAdapter v = this.graph.getVertex(wNode);
        String solution = v.getAttribute("solution");

        // Solution found
        if (solution != null) {
            this.addProperty("Solution in node with id ", wNode.toString());
            this.addProperty("solution", solution);
            this.queue.clear();
            return;
        }

        for (EdgeAdapter ea : v.getOutEdges()) {
            this.queue.add(Integer.valueOf(ea.getInVertex().getId()));
            this.addProperty("Queue: ", this.queue.toString());
        }

    }

}
