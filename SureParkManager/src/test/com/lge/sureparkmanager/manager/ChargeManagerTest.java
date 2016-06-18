package test.com.lge.sureparkmanager.manager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.lge.sureparkmanager.manager.ChargeManager;

public class ChargeManagerTest {

	
	@Test
	public void testCalculateCharge(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:00");
		
		assertEquals(pay, (long)10);
		
	}

}
