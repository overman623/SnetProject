package snetproject.com.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import snetproject.com.dataset.AlertOutputData;
import snetproject.com.dataset.PositionOutputData;
import snetproject.com.main.MainActivity;

public class ClientService extends Thread{

    public static final String TAG = "ClientService";

    private Socket socket;

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private int connectionTimeout = 300;
    private boolean isConnect = false;

    private String ip;
    private int port;

    private String readMessage = null;

    private Handler handler; //네트워크 수신상태를 알려주는 핸들러.

    private Handler mainHandler;
    //Main Activity와 연결된 핸들러.
    //핸들러 변수의 초기화는 MainActivity에서 해주며 수신된 MiniPc의 정보를 수신함.

    private Bundle data = new Bundle();

    public static final int NOT_CONNECTED = 0;
    public static final int LISTEN = 1;
    public static final int CONNECTED = 2;

    private int state = NOT_CONNECTED;

    public Thread positionThread = null;
    boolean threadFlag = false;


    public ClientService(String ip, int port, Handler handler) {
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }

    public void setConnectState(int state){
        this.state = state;
        handler.sendEmptyMessage(this.state);
    }

    public int getConnectState(){
        return this.state;
    }

    public void connect(){
        try {
            setConnectState(LISTEN);
            socket = new Socket(MainActivity.IP, MainActivity.PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            setConnectState(CONNECTED);
            Log.d(TAG, "connected");
            //isConnect = true;
        } catch (IOException e) {
            setConnectState(NOT_CONNECTED);
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void setMainHandler(Handler mainHandler){
        this.mainHandler = mainHandler;
    }

    public void connectionLost(){
        Log.d(TAG, "disconnected");
        try{
            setMainHandler(null);
            //positionService.shutdown();
            positionThread = null;
            threadFlag = true;

            if(inputStream != null){
                inputStream.close();
                inputStream = null;
            }
            if(outputStream != null){
                outputStream.close();
                outputStream = null;
            }
            if(socket != null){
                socket.close();
                socket = null;
            }

            setConnectState(NOT_CONNECTED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    int remainSize = 0;

    @Override
    public void run() {
        this.connect(); //접속은 한번
        //while(true) {
            try {
                while (true) {
                    //Mlog.d(TAG, "main thread live");
                    //this.connect();
                    byte[] buffer = new byte[512];
                    int bytes;
                    if(getConnectState() == NOT_CONNECTED){
                        continue;
                    }
                    if(inputStream == null){
                        break;
                    }
                    bytes = inputStream.read(buffer);

                    if(mainHandler == null){
                        Log.d(TAG, "handler null");
                        continue;
                    }

                    Log.d(TAG, "read : " +  byteArrayToHex(buffer, bytes));
                    dataSeperater(buffer, bytes);
                }//while end

            } catch (IOException e) {
                connectionLost();
            }

       // }
    }//run end

    public String getReadMessage(){
        return this.readMessage;
    }

    public Message setData(String inputString){
        data.putString("data", inputString);
        Message msg = new Message();
        msg.setData(data);
        return msg;
    }

    private void dataSeperater(byte[] a, int size) {
        ByteBuffer buff = null;

        int length = 0;
        int what = 0;
        int start = 0;

        if (a[start] == 0x01) {
            buff = ByteBuffer.allocate(32);
            length = 32;
            what = 1;
        }else if (a[start] == 0x02) {
            buff = ByteBuffer.allocate(24);
            length = 24;
            what = 2;
        }

        if(buff == null) return;

        buff.order(ByteOrder.BIG_ENDIAN);

        int i = 0;
        for (; i < size; i++) {

            if (i == start + length) {
                remainSize = size - (i + 1);
                mainHandler.obtainMessage(what, length, -1, buff.array()).sendToTarget();
                start = start + length;
                if (a[start] == 0x01) {
                    buff = ByteBuffer.allocate(32);
                    length = 32;
                    what = 1;
                } else if (a[start] == 0x02) {
                    buff = ByteBuffer.allocate(24);
                    length = 24;
                    what = 2;
                }
            }
            buff.put(a[i]);
        } // for end

        remainSize = remainSize % length;
        if (i == start + length) {
            mainHandler.obtainMessage(what, length, -1, buff.array()).sendToTarget();
            if(remainSize < length && length == 32){

            }else if(remainSize < length && length == 24){

            }
        }

    }

    int getStreamDataLength(byte[] a){
        //데이터가 없으면 0 을 반환
        //데이터가 있으면 길이를 반환
        for(int i = 0; i < a.length; i++){
            if(i == 2 && a[i] != 0){ //i가 2일때 조건 검색
                return (int)a[i];
            }
        }
        return 0;
    }

    public void sendAlertMessage(final AlertOutputData alertOutputData){  //주기 설정 필요.
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlertOutputData data = alertOutputData;
                ByteBuffer buff = ByteBuffer.allocate(20);
                buff.order(ByteOrder.BIG_ENDIAN);
                buff.put(data.getType());
                buff.putShort(data.getLength());
                buff.putInt(data.getMessageId());
                buff.putInt(data.getVehicleId());
                buff.put(data.getInterfaceType());
                buff.putInt(data.getEventId());
                buff.putInt((int)(System.currentTimeMillis() / 1000L));
                byte[] buffer = buff.array();
                try {
                    if(getConnectState() == CONNECTED) {
                        outputStream.write(buffer);
                        Log.d(TAG, "sendAlertMessage ok");
                    }else{
                        Log.d(TAG, "sendAlertMessage not connected");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void sendPositionMessage(final PositionOutputData positionOutputData){
 //   public void sendPositionMessage(final PositionOutputData positionOutputData, long time){
/*        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PositionOutputData data = positionOutputData;
                ByteBuffer buff = ByteBuffer.allocate(24);
                buff.order(ByteOrder.BIG_ENDIAN);
                buff.put(data.getType());
                buff.putShort(data.getLength());
                buff.putInt(data.getMessageId());
                buff.putInt(data.getVehicleId());
                buff.put(data.getInterfaceType());
                buff.putInt(data.getLatitude());
                buff.putInt(data.getLongitude());
                buff.putInt((int)(System.currentTimeMillis() / 1000L));

                byte[] buffer = buff.array();
                try {
                    if(getConnectState() == CONNECTED) {
                        outputStream.write(buffer);
                        Mlog.d(TAG, "sendPositionMessage ok");
                    }else{
                        Mlog.d(TAG, "sendPositionMessage not connected");
                    }
                } catch (IOException e) {
//                        Mlog.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        positionService.scheduleAtFixedRate(runnable, 0, time, TimeUnit.MILLISECONDS);*/

     positionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //Mlog.d(TAG, "send thread live");
                    if(threadFlag){
                        threadFlag = false;
                       break;
                    }

                    PositionOutputData data = positionOutputData;
                    ByteBuffer buff = ByteBuffer.allocate(24);
                    buff.order(ByteOrder.BIG_ENDIAN);
                    buff.put(data.getType());
                    buff.putShort(data.getLength());
                    buff.putInt(data.getMessageId());
                    buff.putInt(data.getVehicleId());
                    buff.put(data.getInterfaceType());
                    buff.putInt(data.getLatitude());
                    buff.putInt(data.getLongitude());
                    buff.putInt((int)(System.currentTimeMillis() / 1000L));

                    byte[] buffer = buff.array();
                    try {
                        if(getConnectState() == CONNECTED) {
                            outputStream.write(buffer);
                            //Mlog.d(TAG, "sendPositionMessage ok");
                        }else{
                           // Mlog.d(TAG, "sendPositionMessage not connected");
                        }
                    } catch (IOException e) {
//                        Mlog.d(TAG, e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        positionThread.start();
    }

    String byteArrayToHex(byte[] a, int size) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i++){
            sb.append(String.format("%02x,", a[i] & 0xff));
        }
        return sb.toString();
    }

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < a.length; i++){
            sb.append(String.format("%02x,", a[i] & 0xff));
        }
        return sb.toString();
    }

}