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
	Multimap<ServiceType, ServiceType> workflow;
	
	private Workflow(Multimap<ServiceType, ServiceType> workflow) {
		this.workflow = workflow;
	}
	
	public static Workflow getLinear1(){
		Multimap<ServiceType, ServiceType> workflow = ArrayListMultimap.create();
		workflow.put(ServiceType.A, ServiceType.B);
		
		return new Workflow(workflow);
	}

	/**
	 * Returns the service type required by the workflow.. at a given position
	 * @param i
	 * @return
	 */
	public ServiceType get(int i) {
		return workflow.keys().toArray(new ServiceType[workflow.size()])[i]; //TODO check how to do this in a better way.. now has to convert entire workflow
		// in order to get a single element
	}
}
