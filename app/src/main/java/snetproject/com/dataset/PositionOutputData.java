package snetproject.com.dataset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by overm on 2018-11-14.
 */

public class PositionOutputData {

    private static PositionOutputData instance = null;
    private byte type = 1; //type 고정
    private short length = 24; //길이고정
    private int messageId = 1; //메시지 Id 고정
    private int vehicleId = 1; //map fragment에서 가변
    private byte interfaceType = 3; //UI 고정.
    private int latitude = 999999999;
    private int longitude = 0000000000;

    private byte[] combine = new byte[24];

    private PositionOutputData(){

    }

    public static PositionOutputData getInstance(){
        if(instance == null){
            instance = new PositionOutputData();
        }
        return instance;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public byte getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(byte interfaceType) {
        this.interfaceType = interfaceType;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public void setByteData(byte type, short length, int messageId, int vehicleId, byte interfaceType, int latitude, int longitude){
        ByteBuffer buff = ByteBuffer.allocate(24);
        buff.order(ByteOrder.BIG_ENDIAN);
        //[02][00][14][00][00][00][01][00][00][00][01][03][00][00][02][0F][5B][EA][12][B8]
        //buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.put(type);
        buff.putShort(length);
        buff.putInt(messageId);
        buff.putInt(vehicleId);
        buff.put(interfaceType);
        buff.putInt(latitude);
        buff.putInt(longitude);
        buff.putInt((int)(System.currentTimeMillis() / 1000L)); //timeStamp 고정
        this.combine = buff.array();
    } //이 함수는 클라이언트 클래스에서 쓰는게 더 좋을것 같다.

    public byte[] outputByteData(){
        return this.combine;
    }

    @Override
    public String toString() {
        String data = "";
        for (int i = 0; i < combine.length; i++) {
            //System.out.printf("[%02X]", combine[i]);
            data += String.format("[%02X]", combine[i]);
        }
        return data;
    }

}
