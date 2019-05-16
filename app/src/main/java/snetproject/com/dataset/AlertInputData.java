package snetproject.com.dataset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by overm on 2018-11-14.
 */

public class AlertInputData {

    private static AlertInputData instance = null;
    private byte type = 0;
    private short length = 0;
    private int messageId = 0;
    private int vehicleId = 0;
    private byte interfaceType = 0;
    private long eventId = 0;
    private long timeStamp = 0;

    private AlertInputData(){
    }

    public static AlertInputData getInstance(){
        if(instance == null){
            instance = new AlertInputData();
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

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCombine(String alertMessage) {

//        ByteBuffer buff = null;
        //buff.order(ByteOrder.BIG_ENDIAN);

        setType(Byte.parseByte(alertMessage.substring(0, 2), 16)); //type

//        buff = ByteBuffer.allocate(2);
//        buff.put(positionByte[1]);
//        buff.put(positionByte[2]);
//        setLength(buff.getShort()); //length
//        setLength(Short.parseShort(alertMessage[1] + alertMessage[2])); //length
        setLength(Short.parseShort(alertMessage.substring(2, 6), 16)); //length

//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[3]);
//        buff.put(positionByte[4]);
//        buff.put(positionByte[5]);
//        buff.put(positionByte[6]);
//        setMessageId(buff.getInt()); //messageid
//        setMessageId(Integer.parseInt(alertMessage[3] + alertMessage[4] + alertMessage[5] + alertMessage[6])); //messageid
        setMessageId(Integer.parseInt(alertMessage.substring(6, 14), 16)); //messageid

//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[7]);
//        buff.put(positionByte[8]);
//        buff.put(positionByte[9]);
//        buff.put(positionByte[10]);
//        setVehicleId(buff.getInt()); //vehicleid
//        setVehicleId(Integer.parseInt(alertMessage[7] + alertMessage[8] + alertMessage[9] + alertMessage[10])); //vehicleid
        setVehicleId(Integer.parseInt(alertMessage.substring(14, 22), 16)); //vehicleid

//        setInterfaceType(positionByte[11]); //interfacetype
//        setInterfaceType(Byte.parseByte(alertMessage[11])); //interfacetype
        setInterfaceType(Byte.parseByte(alertMessage.substring(22, 24), 16)); //interfacetype

//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[12]);
//        buff.put(positionByte[13]);
//        buff.put(positionByte[14]);
//        buff.put(positionByte[15]);
//        setEventId(Integer.parseInt(alertMessage[12] + alertMessage[13] + alertMessage[14] + alertMessage[15])); //eventid
        setEventId(Long.parseLong(alertMessage.substring(24, 32), 16)); //eventid

//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[16]);
//        buff.put(positionByte[17]);
//        buff.put(positionByte[18]);
//        buff.put(positionByte[19]);
//        setTimeStamp(buff.getInt()); //timestamp
//        setTimeStamp(Hex.stringToBytes(alertMessage[16] + alertMessage[17] + alertMessage[18] + alertMessage[19])); //timestamp
        setTimeStamp(Long.parseLong(alertMessage.substring(32, 48), 16)); //timestamp

    }

    @Override
    public String toString() {
        String data = "";
        data += getType() + " : ";
        data += getLength() + " : ";
        data += getMessageId() + " : ";
        data += getVehicleId() + " : ";
        data += getInterfaceType() + " : ";
        data += getEventId() + " : ";
        data += getTimeStamp() + " : ";
        return data;
    }
}
