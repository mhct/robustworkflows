package be.kuleuven.robustworkflows.model;

import java.io.IOException;

import be.kuleuven.robustworkflows.infrastructure.GraphLoaderApplication;
import be.kuleuven.robustworkflows.infrastructure.SorcererApplication;

public class ExperimentLauncher {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		SorcererApplication sorcerer = new SorcererApplication();
		GraphLoaderApplication graphloader = new GraphLoaderApplication();
		RobustWorkflowsLauncher robustWorkflows = new RobustWorkflowsLauncher();
		
		sorcerer.startup();
		Thread.sleep(10000);
		
		graphloader.startup();
		Thread.sleep(10000);
		
		robustWorkflows.startup();
		robustWorkflows.sendComposeToAllClientAgents();
		Thread.sleep(10000);
		
		System.in.read();
		sorcerer.shutdown();
		graphloader.shutdown();
		robustWorkflows.shutdown();
	}

}
