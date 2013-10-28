package be.kuleuven.robustworkflows.infrastructure.configuration;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationConfTest {

	private Config config;

	@Before
	public void setUpBeforeClass() throws Exception {
		config = ConfigFactory.load().getConfig("robust-workflows-launcher");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testRobustWorkflowsLauncherConfigurations() {
		Config actorConfig = config.getConfig("actor");
		System.out.println("keys: " + actorConfig.getString("start-time-interval"));
	}

}
