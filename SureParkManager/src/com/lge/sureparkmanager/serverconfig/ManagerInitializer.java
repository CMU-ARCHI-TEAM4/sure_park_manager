package com.lge.sureparkmanager.serverconfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Log;

public class ManagerInitializer extends HttpServlet {
    private static final String TAG = ManagerInitializer.class.getSimpleName();

	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		Log.d(TAG, "init");
		SystemManager.getInstance().init();
	}

}
