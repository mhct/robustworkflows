package be.kuleuven.robustworkflows.model;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.events.ModelEvent;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import static org.mockito.Mockito.mock
;public class ModelStorageTest {

	
	private static DB db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = mock(DB.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAddField() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetField() {
		fail("Not yet implemented");
	}

	@Test
	public void testPersistWriteCache() {
		ModelStorage ms = ModelStorage.getInstance(db);
		ms.persistEvent(new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				return null;
			}
			
			@Override
			public String eventType() {
				return "MyEvent";
			}
		});
		
		ms.persistWriteCache();
	}

	@Test
	public void testPersistEventModelEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testPersistEventEventTypeString() {
		fail("Not yet implemented");
	}

	@Test
	public void testPersistEventDBObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterFactoryAgent() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFactoryAgents() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnRegisterFactoryAgent() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinishedAllCompositions() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

}
