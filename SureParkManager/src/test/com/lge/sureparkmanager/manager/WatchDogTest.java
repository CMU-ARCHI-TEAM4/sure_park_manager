package test.com.lge.sureparkmanager.manager;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lge.sureparkmanager.manager.WatchDog;
import com.lge.sureparkmanager.manager.WatchDog.TimeoutCallback;

public class WatchDogTest {

	@Test
	public void testConstructorWithTimeoutCallback() {
		long timeoutInMilliSecs = 10;
		WatchDog dog = new WatchDog(timeoutInMilliSecs);
		
		dog.start();
		dog.stop();
		
		assertTrue("constructor test with timeout callback", true);
	}
	
	@Test
	public void testConstructorWithTimeoutCallbackAndTimeoutCallback() {
		
		long timeoutInMilliSecs = 10;
	
		final WatchDog dog = new WatchDog(timeoutInMilliSecs, new TimeoutCallback() {

			@Override
			public void onTimeout() {
				assertTrue(" constructor test with timeout and callback", true);
				
			}
		    
		});
		
		dog.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		dog.stop();
	}
	

}
