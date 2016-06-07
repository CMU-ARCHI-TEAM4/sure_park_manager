package com.lge.sureparkmanager.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
        ws.setName("WiFiSocket-thread");
        ws.start();
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void reportDeath() {

    }

    public void sendMessageToClients(String msg) {
        for (SocketObject so : mSocketObjects) {
            so.mSender.sendMessageToClient(msg);
        }
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
        private String mName;
        private Socket mSocket;
        private DataOutputStream mOut;

        Sender(Socket socket) {
            mSocket = socket;
            try {
                mOut = new DataOutputStream(mSocket.getOutputStream());
                mName = "[" + mSocket.getInetAddress() + ":" + mSocket.getPort() + ":" + "] ";
            } catch (Exception e) {
            }
        }

        public void sendMessageToClient(String msg) {
            if (mOut != null) {
                try {
                    Log.d(TAG, "sendMessageToClient " + msg);
                    mOut.writeUTF(mName + msg);
                } catch (IOException e) {
                    e.printStackTrace();
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
        private DataInputStream mIn;

        Receiver(Socket socket) {
            mSocket = socket;
            try {
                mIn = new DataInputStream(mSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (mIn != null) {
                try {
                    System.out.println(mIn.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
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

    private final class SocketObject implements SocketOperations {
        private Sender mSender;
        private Receiver mReceiver;

        public SocketObject(Sender sender, Receiver receiver) {
            mSender = sender;
            mReceiver = receiver;
        }

        @Override
        public void close() {
            mSender.close();
            mReceiver.close();
        }
    }

    private interface SocketOperations {
        public void close();
    }
}
