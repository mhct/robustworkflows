package be.kuleuven.robustworkflows.infrastructure.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigLoadingTests {
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
//		System.out.println("sysout" + config.getConfig("sorcerer").getString("robust-workflows.system-name"));
		System.out.println("sysout" + config.getConfig("sorcerer").getString("akka.remote.netty.port"));
	}
}
