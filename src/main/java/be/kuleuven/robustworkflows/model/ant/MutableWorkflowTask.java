package be.kuleuven.robustworkflows.model.ant;

import java.util.List;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ImmutableWorkflowTask;
import com.google.common.collect.Lists;

//FIXME remove this class
//public class MutableWorkflowTask {
//	private final ServiceType type;
//	private final List<MetaExplorationReply> replies;
//	
//	private MutableWorkflowTask(ServiceType type) {
//		this.type = type;
//		this.replies = Lists.newArrayList();
//	}
//
////	public void setAgent(ActorRef actor) {
////		if (actor == null) {
////			throw new IllegalArgumentException("This task already has a matching service, or actor is null");
////		} else {
////			this.agent = actor;
////		}
////	}
//	
//	public void addReply(MetaExplorationReply reply) {
//		if (reply == null) {
//			throw new IllegalArgumentException("QoS can't be null");
//		} else {
//			replies.add(reply);
//		}
//	}
//	
//	public ServiceType getType() {
//		return type;
//	}
////	
////	public ActorRef getAgent() {
////		return agent;
////	}
////	
////	public ExplorationReply getQoS() {
////		return qos;
////	}
//	
//	/**
//	 * selects the best available service (minimum computational time)
//	 * @return
//	 */
//	public ImmutableWorkflowTask getImmutableWorkflowTask() {
////		return ImmutableWorkflowTask.getInstance(getType(), getAgent(), getQoS());
//		long computationTime = Long.MAX_VALUE;
//		MetaExplorationReply winner = null;
//		for (MetaExplorationReply m: replies) {
//			if (m.getReply().getComputationTime() <= computationTime) {
//				winner = m;
//				computationTime = winner.getReply().getComputationTime();
//			}
//		}
//		
//		if (winner == null) {
//			return ImmutableWorkflowTask.getInstance(type, null, null);
//		} else {
//			return ImmutableWorkflowTask.getInstance(type, winner.getSender(), winner.getReply());
//		}
//	}
//	
//	
//	public static MutableWorkflowTask getInstance(ServiceType type) {
//		if (type == null) {
//			throw new IllegalArgumentException("ServiceType can't be null");
//		}
//		
//		return new MutableWorkflowTask(type);
//	}
//
//}
