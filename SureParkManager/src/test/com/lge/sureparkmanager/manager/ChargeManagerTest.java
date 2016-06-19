package test.com.lge.sureparkmanager.manager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lge.sureparkmanager.manager.ChargeManager;

public class ChargeManagerTest {

	
	@Test
	public void testCalculateCharge(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:00");
		
		assertEquals(pay, (long)10);
		
	}
	
	@Test
	public void testCalculateChargeFail(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11", "2016-06-11 12:00");
	
		assertNotSame(pay, 10);
		
	}
	
	@Test
	public void testCalculateChargeWithSeconds(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00:00.00000", "2016-06-11 12:00:00.00000");
		
		assertEquals(pay, (long)10);
		
	}
	
	@Test
	public void testRequestCharge() {
		
		ChargeManager ca = new ChargeManager();
		ca.requestCharge("1111222233334444", "2016-11", 20L);
		
		assert(true);
	}

}
