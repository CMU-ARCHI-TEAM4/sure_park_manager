package com.lge.sureparkmanager.manager;

import java.util.HashMap;

import com.lge.sureparkmanager.utils.Log;

public final class SystemManager {
    private static final String TAG = SystemManager.class.getSimpleName();

    public static final int NETWORK_MANAGER = 1;
    public static final int DATABASE_MANAGER = 2;
    public static final int CONFIGURATION_MANAGER = 3;
    public static final int COMMAND_MANAGER = 4;

    private static SystemManager mInstance;

    private HashMap<Integer, SystemManagerBase> mManagers =
            new HashMap<Integer, SystemManagerBase>();
    private CommandQueue mCommandQueue;

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
            Log.d(TAG, "init");

            // Initialize DataBaseManager.
//            DataBaseManager dataBaseManager = new DataBaseManager();
//            dataBaseManager.init();
//            mManagers.put(DATABASE_MANAGER, dataBaseManager);
//
//            // Initialize CommandManager.
//            CommandManager commandManager = new CommandManager();
//            commandManager.init();
//            mManagers.put(COMMAND_MANAGER, commandManager);
//
//            // Initialize CommandDispatcher.
//            mCommandQueue = new CommandQueue();
//            Thread commandDispatcherThread = new Thread(mCommandQueue);
//            commandDispatcherThread.start();
//
//            // Initialize ConfigurationManager.
//            ConfigurationManager configurationManager = new ConfigurationManager();
//            configurationManager.init();
//            mManagers.put(CONFIGURATION_MANAGER, configurationManager);
//
//            // Initialize NetworkManager.
//            NetworkManager networkManager = new NetworkManager();
//            networkManager.init();
//            mManagers.put(NETWORK_MANAGER, networkManager);
        } else {
            throw new RuntimeException("SystemManager has been initialized already");
        }
    }

    public SystemManagerBase getManager(int manager) {
        return mManagers.get(manager);
    }

    public CommandQueue getCommandQueue() {
        return mCommandQueue;
    }
}
