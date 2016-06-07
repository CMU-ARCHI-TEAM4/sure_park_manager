package com.lge.sureparkmanager.manager;

import java.util.HashMap;

import com.lge.sureparkmanager.utils.Log;

public final class SystemManager {
    private static final String TAG = SystemManager.class.getSimpleName();

    public static final int NETWORK_MANAGER = 1;
    public static final int DATABASE_MANAGER = 2;

    private static SystemManager mInstance;

    private HashMap<Integer, SystemManagerBase> mManagers =
            new HashMap<Integer, SystemManagerBase>();

    private boolean mIsInit = false;

    private SystemManager() {
        
    }

    public static synchronized SystemManager getInstance() {
        if (mInstance == null) {
            mInstance = new SystemManager();
        }
        return mInstance;
    }

    public void init() {
        if (!mIsInit) {
            Log.d(TAG, "SystemManager init...");
            // Init NetworkManager.
            NetworkManager networkManager = new NetworkManager();
            networkManager.init();
            mManagers.put(NETWORK_MANAGER, networkManager);

            // Init DataBaseManager.
            DataBaseManager dataBaseManager = new DataBaseManager();
            dataBaseManager.init();
            mManagers.put(DATABASE_MANAGER, dataBaseManager);
        } else {
            throw new RuntimeException("SystemManager has been initialized already");
        }
    }

    public SystemManagerBase getManager(int manager) {
        return mManagers.get(manager);
    }
}
