package be.kuleuven.robustworkflows.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a workflow specification
 * 
 * @author mariohct
 *
 */
public class Workflow {
	//Adjacency list representing a service workflow
	Multimap<String, String> workflow;
	
	private Workflow(Multimap<String, String> workflow) {
		this.workflow = workflow;
	}
	
	public static Workflow getLinear1(){
		Multimap<String, String> workflow = ArrayListMultimap.create();
		workflow.put("A", "B");
		
		return new Workflow(workflow);
	}
}
