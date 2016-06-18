package com.lge.sureparkmanager.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Log;

@WebServlet("/welcome")
public class Welcome extends HttpServlet {
    private static final String TAG = Welcome.class.getSimpleName();
	private static final long serialVersionUID = 1L;

    public Welcome() {
        super();
        //com.lge.test.Main.main(new String[] { "test" });
        /*WatchDog wd = new WatchDog(5000, new WatchDog.TimeoutCallback() {
            @Override
            public void onTimeout() {
                Log.d("kysant", "onTimeout");
            }
        });
        //wd.start();
        try {
            Thread.sleep(2000);
            wd.reset();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
        Log.d(TAG, "doGet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
        Log.d(TAG, "doPost");
    }

    
}
