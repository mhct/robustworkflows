package be.kuleuven.robustworkflows.util;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorContext;
import be.kuleuven.robustworkflows.model.clientagent.EventType;

/**
 * Only methods that REALLY can't be put anywhere else without code duplication
 * 
 * @author mario
 *
 */
public class Utils {
	
	private Utils() {}
	
	public static void addExpirationTimer(final ActorContext context, long time, final EventType message) {
		context.system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						context.self().tell(message, context.self());
					}
		}, context.dispatcher());
	}
}
