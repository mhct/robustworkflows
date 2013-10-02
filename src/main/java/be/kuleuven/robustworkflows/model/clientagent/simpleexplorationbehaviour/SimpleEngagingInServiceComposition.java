package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestCompleted;

import com.mongodb.BasicDBObject;

/**
 * Engages with a simple service
 * 
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with different providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * TODO check ExplorationResult
 * 
 * === Inbound Messages ===
 * 
 * === Outbound Messages ===
 * 
 * 
 * @author mario
 *
 */
public class SimpleEngagingInServiceComposition extends ClientAgentState {
	private static final String EXPECTED_TIME_TO_SERVE_REQUEST = "EXPECTED_TIME_TO_SERVE_REQUEST";
	private static final String REAL_TIME_TO_SERVE_REQUEST = "REAL_TIME_TO_SERVE_REQUEST"; 
	
	private int serviceRequestCounter = 0;
	private long startTimeCurrentTask;
	
	private ActorRef selectedService;
	private long expectedTimeToExecuteTask;
	private ServiceType serviceType;
	private long realTimeToServeRequest;
	
	private SimpleEngagingInServiceComposition(ClientAgentProxy clientAgentProxy, SimpleExplorationResult simpleExplorationResult) {
		super(clientAgentProxy);
		this.selectedService = simpleExplorationResult.actor();
		this.expectedTimeToExecuteTask = simpleExplorationResult.computationTime();
		this.serviceType = simpleExplorationResult.serviceType();
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			engageWithServiceProvider();
		} 
		else if (ServiceRequestCompleted.class.isInstance(message)){
			realTimeToServeRequest = System.currentTimeMillis() - startTimeCurrentTask;
			final ServiceRequestCompleted msg = (ServiceRequestCompleted) message;
			
			RequestExecutionData requestExecutionData = RequestExecutionData.getInstance(getClientAgentProxy().clientAgentName(), msg.factoryAgentName(), expectedTimeToExecuteTask, realTimeToServeRequest);
			addRequestExecutionData(requestExecutionData);
			persistEvent(summaryServiceRequest(requestExecutionData));
			setState(RunningCompositionState.getActiveInstance(getClientAgentProxy()));
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	private void engageWithServiceProvider() {
		startTimeCurrentTask = System.currentTimeMillis();
		realTimeToServeRequest = 0;
		serviceRequestCounter = serviceRequestCounter + 1;
		
		selectedService.tell(ServiceRequest.getInstance(serviceRequestCounter, serviceType), getClientAgentProxy().self());
	}
	
	/**
	 * Creates a summary of the finished request in a format accepted by the ModelStorage
	obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
	 * 
	 * @param req
	 * @return
	 */
	private BasicDBObject summaryServiceRequest(RequestExecutionData requestData) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.SERVICE_REQUEST_SUMMARY.toString());
		
		obj.append(EXPECTED_TIME_TO_SERVE_REQUEST, requestData.getExpectedTimeToExecuteTask()); 
		obj.append(REAL_TIME_TO_SERVE_REQUEST, requestData.getRealTimeToServeRequest());
		obj.append("FACTORY_AGENT", requestData.getFactoryAgentName());
		obj.append("CLIENT_AGENT---------------------------------------------", requestData.getClientAgentName());
		return obj;
	}

	public static SimpleEngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, SimpleExplorationResult simpleExplorationResult) {
		return new SimpleEngagingInServiceComposition(clientAgentProxy, simpleExplorationResult);
	}

}
