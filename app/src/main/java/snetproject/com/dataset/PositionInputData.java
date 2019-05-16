package snetproject.com.dataset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by overm on 2018-11-14.
 */

public class PositionInputData {

    private static PositionInputData instance = null;
    private byte type = 0;
    private short length = 0;
    private long messageId = 0;
    private long vehicleId = 0;
    private byte interfaceType = 0;
    private long latitude = 0;
    private long longitude = 0;
    private short course = 0;
    private short speed = 0;
    private long timeStamp = 0;

    private PositionInputData(){
    }

    public static PositionInputData getInstance(){
        if(instance == null){
            instance = new PositionInputData();
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

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public byte getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(byte interfaceType) {
        this.interfaceType = interfaceType;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public short getCourse() {
        return course;
    }

    public void setCourse(short course) {
        this.course = course;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCombine(String positionMessage) {

        setType(Byte.parseByte(positionMessage.substring(0, 2), 16)); //type

        setLength(Short.parseShort(positionMessage.substring(2, 6), 16)); //length

        setMessageId(Long.parseLong(positionMessage.substring(6, 14), 16)); //messageid

        setVehicleId(Long.parseLong(positionMessage.substring(14, 22), 16)); //vehicleid

        setInterfaceType(Byte.parseByte(positionMessage.substring(22, 24), 16)); //interfacetype

        setLatitude(Long.parseLong(positionMessage.substring(24, 32), 16)); //latitude

        setLongitude(Long.parseLong(positionMessage.substring(32, 40), 16)); //longitude

        setCourse(Short.parseShort(positionMessage.substring(40, 44), 16));

        setSpeed(Short.parseShort(positionMessage.substring(44, 48), 16));

        setTimeStamp(Long.parseLong(positionMessage.substring(48, 56), 16)); //timestamp

//        ByteBuffer buff = null;
        //buff.order(ByteOrder.BIG_ENDIAN);

//        setType(positionByte[0]); //type
//
//        buff = ByteBuffer.allocate(2);
//        buff.put(positionByte[1]);
//        buff.put(positionByte[2]);
//        setLength(buff.getShort()); //length
//
//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[3]);
//        buff.put(positionByte[4]);
//        buff.put(positionByte[5]);
//        buff.put(positionByte[6]);
//        setMessageId(buff.getInt()); //messageid
//
//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[7]);
//        buff.put(positionByte[8]);
//        buff.put(positionByte[9]);
//        buff.put(positionByte[10]);
//        setVehicleId(buff.getInt()); //vehicleid
//
//        setInterfaceType(positionByte[11]); //interfacetype
//
//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[12]);
//        buff.put(positionByte[13]);
//        buff.put(positionByte[14]);
//        buff.put(positionByte[15]);
//        setLatitude(buff.getInt()); //latitude
//
//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[16]);
//        buff.put(positionByte[17]);
//        buff.put(positionByte[18]);
//        buff.put(positionByte[19]);
//        setLongitude(buff.getInt()); //longitude
//
//        buff = ByteBuffer.allocate(4);
//        buff.put(positionByte[20]);
//        buff.put(positionByte[21]);
//        buff.put(positionByte[22]);
//        buff.put(positionByte[23]);
//        setTimeStamp(buff.getInt()); //timestamp

    }


    @Override
    public String toString() {
        String data = "";
        data += getType() + " : ";
        data += getLength() + " : ";
        data += getMessageId() + " : ";
        data += getVehicleId() + " : ";
        data += getInterfaceType() + " : ";
        data += getLatitude() + " : ";
        data += getLongitude() + " : ";
        data += getTimeStamp() + " : ";
        return data;
    }

}
