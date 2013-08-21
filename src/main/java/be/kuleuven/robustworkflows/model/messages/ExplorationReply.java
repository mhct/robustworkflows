package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.clientagent.EventType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class ExplorationReply implements Serializable {

	private static final long serialVersionUID = 20130821L;
	
	private final long computationTime; //only quality being evaluated right now
	private final ExplorationRequest requestExploration;

	public ExplorationReply(ExplorationRequest requestExploration, long computationTime) {
		this.requestExploration = requestExploration;
		this.computationTime = computationTime;
	}


	public long getComputationTime() {
		return computationTime;
	}
	
	public ExplorationRequest getRequestExploration() {
		return requestExploration;
	}

	/**
	 * Factory of ServiceRequestExplorationReply
	 * 
	 * @param requestExploration
	 * @param computationTime
	 * @return
	 */
	public static ExplorationReply getInstance(ExplorationRequest requestExploration, long computationTime) {
		return new ExplorationReply(requestExploration, computationTime);
	}


	@Override
	public String toString() {
		return "ExplorationReply [computationTime=" + computationTime
				+ ", requestExploration=" + requestExploration + "]";
	}


	public DBObject toDBObject(String factoryAgentName) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.ExplorationReply.toString());
		obj.append("ClientAgent", getRequestExploration().getOriginName());
		obj.append("FactoryAgent", factoryAgentName);
		obj.append("ComputationTime", getComputationTime());
		
		return obj;
	}
}
