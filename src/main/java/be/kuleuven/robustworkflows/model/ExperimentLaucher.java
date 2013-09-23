package be.kuleuven.robustworkflows.model;

import java.io.IOException;

import be.kuleuven.robustworkflows.infrastructure.GraphLoaderApplication;
import be.kuleuven.robustworkflows.infrastructure.SorcererApplication;

public class ExperimentLaucher {

	public static void main(String[] args) throws InterruptedException, IOException {
		SorcererApplication sorcerer = new SorcererApplication();
		GraphLoaderApplication graphLoader = new GraphLoaderApplication();
		RobustWorkflowsLauncher wf = new RobustWorkflowsLauncher();
		
		sorcerer.startup();
		Thread.sleep(5000);
		graphLoader.startup();
		Thread.sleep(5000);
		wf.startup();
		wf.sendComposeToAllClientAgents();

		System.in.read();
		sorcerer.shutdown();
		graphLoader.shutdown();
		wf.shutdown();
		
		
	}
}
