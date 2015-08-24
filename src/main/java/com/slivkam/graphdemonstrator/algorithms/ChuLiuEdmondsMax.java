package com.slivkam.graphdemonstrator.algorithms;

import java.util.Iterator;

import com.slivkam.graphdemonstrator.model.GraphAdapter;
import com.tinkerpop.blueprints.Edge;

public class ChuLiuEdmondsMax extends ChuLiuEdmonds {

	public ChuLiuEdmondsMax(){
		super();
		super.setBranching(Branching.MAX);
	}
	
	public ChuLiuEdmondsMax(GraphAdapter g){
		this();
		super.setGraph(g);
	}
	
	@Override
	protected Integer recalculateWeight(Integer oldWeight, Integer cycleEdgeWeight){
		Integer cycleMinWeight = this.getCycleMinWeight();
		System.out.print(" ["+oldWeight+" - "+cycleEdgeWeight+" + "+cycleMinWeight+"]\n");
		return oldWeight - cycleEdgeWeight + cycleMinWeight;
	}
	
	/** Find max. weight in cycle edges. */
	private Integer getCycleMinWeight(){
		Iterator<Edge> it = this.cycle.iterator();
		Integer minWeight = Integer.parseInt((String)it.next().getProperty("weight"));

		while (it.hasNext()){
			Integer w = Integer.parseInt((String)it.next().getProperty("weight"));
			if (w < minWeight){
				minWeight = w;
			}
		}
		
		return minWeight;
	}
	
}
