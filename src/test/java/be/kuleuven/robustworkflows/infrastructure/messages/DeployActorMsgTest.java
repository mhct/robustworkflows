package be.kuleuven.robustworkflows.infrastructure.messages;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import akka.actor.Props;

public class DeployActorMsgTest {

	@Test(expected=RuntimeException.class)
	public void valueOfError() {
		DeployActorMsg.valueOf("10");
	}
	
	@Test
	public void valueOfOk() {
		String expectedName = "name1";
		Props expectedProps = Mockito.mock(Props.class);
		
		DeployActorMsg msg = DeployActorMsg.valueOf((Object) new DeployActorMsg(expectedProps, expectedName));
		
		assertNotNull(msg);
		assertEquals(expectedName, msg.getName());
		assertEquals(expectedProps, msg.getProps());
	}

}
