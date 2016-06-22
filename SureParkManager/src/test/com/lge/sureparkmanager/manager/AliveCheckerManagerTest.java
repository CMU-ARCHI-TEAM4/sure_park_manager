package test.com.lge.sureparkmanager.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lge.sureparkmanager.manager.AliveCheckerManager;

public class AliveCheckerManagerTest {

	private String MAC = "11:22:33:44:55:66";
	
	@Test
	public void testCreateAliveChecker() {
		
		new AliveCheckerManager().createAliveChecker(MAC);
		assertTrue(true);
	}
	
	@Test
	public void testKick() {
		
		new AliveCheckerManager().kick(MAC, 10);
		assertTrue(true);
	}
	
	@Test
	public void testIsAlive() {
		
		boolean ret = new AliveCheckerManager().isAlive(MAC);
		assertFalse(ret);
	}
}
