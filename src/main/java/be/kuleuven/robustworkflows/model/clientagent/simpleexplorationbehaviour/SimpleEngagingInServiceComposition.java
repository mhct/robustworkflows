package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.events.ServiceRequestSummaryEvent;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestCompleted;

/**
 * Engages with a simple service
 * 
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with different providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * TODO check ExplorationResult
 * 
 * === Inbound Messages ===
 * - ''' ServiceRequestCompleted '''
 * 
 * === Outbound Messages ===
 * - ''' ServiceRequest '''
 * 
 * @author mario
 *
 */
public class SimpleEngagingInServiceComposition extends ClientAgentState {
	
	
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
	public void run() {
		getClientAgentProxy().getLoggingAdapter().info("ClientAgent" + getClientAgentProxy().clientAgentName() + "SimpleEngaginInServiceCompositionState");
		engageWithServiceProvider();
	}
	
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (ServiceRequestCompleted.class.isInstance(message)){
			realTimeToServeRequest = System.currentTimeMillis() - startTimeCurrentTask;
			final ServiceRequestCompleted msg = (ServiceRequestCompleted) message;
			
			RequestExecutionData requestExecutionData = RequestExecutionData.getInstance(getClientAgentProxy().clientAgentName(), msg.factoryAgentName(), expectedTimeToExecuteTask, realTimeToServeRequest);
			addRequestExecutionData(requestExecutionData);
			persistEvent(summaryServiceRequest(requestExecutionData));
			setState(getClientAgentProxy().getHackingState());
			
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
	private ServiceRequestSummaryEvent summaryServiceRequest(RequestExecutionData requestData) {
		return ServiceRequestSummaryEvent.instance(requestData);
	}

	public static SimpleEngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, SimpleExplorationResult simpleExplorationResult) {
		return new SimpleEngagingInServiceComposition(clientAgentProxy, simpleExplorationResult);
	}

}
