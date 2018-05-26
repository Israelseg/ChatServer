package com.example.hp.groupchat.shared;


import java.io.Serializable;

/**
 * @author hp
 */
public class Message implements Serializable {

    private final String from;
    private String to;
    private String time;
    private int type;

    private String msg;
    private byte[] content;
    private String jsonString;

    public Message(String from, int type, String msg) {
        this.from = from;
        this.type = type;
        if (msg != null) {
            this.msg = msg;
        } else {
            this.msg = "";
        }

        time = ServerUtils.dateLog();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (type != message.type) return false;
        if (!from.equals(message.from)) return false;
        if (to != null ? !to.equals(message.to) : message.to != null) return false;
        return time.equals(message.time);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + time.hashCode();
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Type msg : %d, Time : %s - %s", type, time, msg);
    }
}
