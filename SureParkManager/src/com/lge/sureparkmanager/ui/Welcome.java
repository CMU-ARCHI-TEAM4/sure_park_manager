package com.lge.sureparkmanager.ui;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.manager.WatchDog;
import com.lge.sureparkmanager.utils.Log;

@WebServlet("/welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        SystemManager.getInstance().init();
        //com.lge.test.Main.main(new String[] { "test" });
        WatchDog wd = new WatchDog(5000, new WatchDog.TimeoutCallback() {
            
            @Override
            public void onTimeout() {
                Log.d("kysant", "onTimeout");
            }
        });
        wd.start();
        try {
            Thread.sleep(2000);
            wd.reset();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
