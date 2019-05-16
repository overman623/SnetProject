package snetproject.com.dataset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.Format;

/**
 * Created by overm on 2018-11-14.
 */

public class AlertOutputData {

    private static AlertOutputData instance = null;
    private byte type = 2; //고정
    private short length = 20; //고정
    private int messageId = 1; //고정
    private int vehicleId = 1; //가변
    private byte interfaceType = 3; //고정
    private int eventId = 527; //고정
    private byte[] combine = new byte[24];

    private AlertOutputData(){

    }

    public static AlertOutputData getInstance(){
        if(instance == null){
            instance = new AlertOutputData();
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

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setByteData(byte type, short length, int messageId, int vehicleId, byte interfaceType, int eventId){
        ByteBuffer buff = ByteBuffer.allocate(24);
        buff.order(ByteOrder.BIG_ENDIAN);

        //[02][00][14][00][00][00][01][00][00][00][01][03][00][00][02][0F][5B][EA][12][B8]
        //buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.put(type);
        buff.putShort(length);
        buff.putInt(messageId);
        buff.putInt(vehicleId);
        buff.put(interfaceType);
        buff.putInt(eventId);
//        buff.putInt((int)(System.currentTimeMillis() / 1000L));
        buff.putLong(System.currentTimeMillis()); //타임 스탬프를 바꾸었음.
        this.combine = buff.array();
    }

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
