package com.lge.sureparkmanager.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import com.lge.sureparkmanager.utils.Log;

public final class NetworkManager extends SystemManagerBase {
    private static final String TAG = NetworkManager.class.getSimpleName();

    private ArrayList<SocketObject> mSocketObjects = new ArrayList<SocketObject>();

    public NetworkManager() {

    }

    @Override
    protected void init() {
        super.init();

        Log.d(TAG, "init");

        WiFiSocket ws = new WiFiSocket(7777);
        ws.start();
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void reportDeath() {

    }

    public void sendBroadcast(String msg) {
        for (SocketObject so : mSocketObjects) {
            so.mSender.sendMessageToClient(msg);
        }
    }

    private void sendMessageToTarget(SocketObject so, String msg) {
        for (SocketObject s : mSocketObjects) {
            if (Objects.equals(s, so)) {
                s.mSender.sendMessageToClient(msg);
                return;
            }
        }
    }

    private SocketObject getSocketObject(Sender sender) {
        for (SocketObject so : mSocketObjects) {
            if (Objects.equals(so.mSender, sender)) {
                return so;
            }
        }
        return null;
    }

    private SocketObject getSocketObject(Receiver receiver) {
        for (SocketObject so : mSocketObjects) {
            if (Objects.equals(so.mReceiver, receiver)) {
                return so;
            }
        }
        return null;
    }

    private final class WiFiSocket extends Thread {
        private int mPort;

        WiFiSocket(int port) {
            mPort = port;
        }

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(mPort);

                while (!Thread.currentThread().isInterrupted()) {
                    socket = serverSocket.accept();
                    Log.d(TAG, "accepted client: " + socket);

                    Sender sender = new Sender(socket);
                    Receiver receiver = new Receiver(socket);
                    receiver.start();

                    mSocketObjects.add(new SocketObject(sender, receiver));
                }
            } catch (Exception e) {
            }
        }
    }

    private final class Sender implements SocketOperations {
        private Socket mSocket;
        private BufferedWriter mOut;

        Sender(Socket socket) {
            mSocket = socket;
            try {
                mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendMessageToClient(String msg) {
            if (mOut != null) {
                final SocketObject so = getSocketObject(this);
                try {
                    Log.d(TAG, "sendMessageToClient: " + msg);
                    mOut.write(msg);
                    mOut.flush();
                } catch (IOException e) {
                    Log.d(TAG, "connection terminated...");
                    if (so != null) {
                        so.close();
                    }
                }
            }
        }

        @Override
        public void close() {
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
                if (mOut != null) {
                    mOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final class Receiver extends Thread implements SocketOperations {
        private Socket mSocket;
        private BufferedReader mIn;

        Receiver(Socket socket) {
            mSocket = socket;
            try {
                mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            SocketObject so = null;
            String line = null;

            try {
                while ((line = mIn.readLine()) != null) {
                    Log.d(TAG, line);
                    so = getSocketObject(this);
                    sendMessageToTarget(so, line);
                }
            } catch (IOException e) {
                Log.d(TAG, "connection terminated...");
                if (so != null) {
                    so.close();
                }
            }
        }

        @Override
        public void close() {
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
                if (mIn != null) {
                    mIn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final class SocketObject {
        private Sender mSender;
        private Receiver mReceiver;

        public SocketObject(Sender sender, Receiver receiver) {
            mSender = sender;
            mReceiver = receiver;
        }

        public void close() {
            mSender.close();
            mReceiver.close();
            mSocketObjects.remove(this);
        }
    }

    private interface SocketOperations {
        public void close();
    }
}
