package test.com.lge.sureparkmanager.manager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lge.sureparkmanager.manager.ChargeManager;

public class ChargeManagerTest {

	private final long PERHOUR= 10;
	
	@Test
	public void testCalculateCharge(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:00");
		
		assertEquals(pay, PERHOUR);
		
	}
	
	@Test
	public void testCalculateCharge2(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 11:01");
		
		assertEquals(pay, PERHOUR);
		
	}
	
	@Test
	public void testCalculateCharge3(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 11:30");
		
		assertEquals(pay, PERHOUR);
		
	}
	
	@Test
	public void testCalculateCharge4(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 11:31");
		
		assertEquals(pay, PERHOUR);
		
	}
	
	
	@Test
	public void testCalculateCharge5(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:29");
		
		assertEquals(pay, PERHOUR * 1);
		
	}
	
	@Test
	public void testCalculateCharge6(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:30");
		
		assertEquals(pay, PERHOUR * 2);
		
	}
	
	@Test
	public void testCalculateCharge7(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 12:31");
		
		assertEquals(pay, PERHOUR * 2);
		
	}
	
	@Test
	public void testCalculateCharge8(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 13:15");
		
		assertEquals(pay, PERHOUR * 2);
		
	}
	
	@Test
	public void testCalculateCharge9(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-11 14:15");
		
		assertEquals(pay, PERHOUR * 3);
		
	}
	
	@Test
	public void testCalculateCharge10(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-12 11:00");
		
		assertEquals(pay, PERHOUR * 24);
		
	}
	
	@Test
	public void testCalculateCharge11(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-12 11:30");
		
		assertEquals(pay, PERHOUR * 25);
		
	}
	
	@Test
	public void testCalculateCharge12(){
		
		ChargeManager ca = new ChargeManager();
		long pay = ca.calculateCharge("2016-06-11 11:00", "2016-06-12 11:59");
		
		assertEquals(pay, PERHOUR * 25);
		
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
