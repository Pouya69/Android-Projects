package com.pouyasalehi.dada;

import android.content.Context;

import java.util.ArrayList;

public class MessageObject implements Cloneable {

    String  messageId,
            senderId,
            message,profilePic;
    String uid;
    int id;

    ArrayList<String> mediaUrlList;
    boolean sender;
    Context context;

    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrlList, boolean sender, String profilePic, Context context,int id,String uid){
        this.messageId = messageId;
        this.profilePic = profilePic;
        this.id=id;
        this.senderId = senderId;
        this.uid=uid;
        this.sender=sender;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.context=context;

    }
    public int compareTo(Object o) {
        MessageObject compare = (MessageObject) o;

        if (compare.getID() == this.getID() && compare.getSenderId().equals(this.getSenderId()) && compare.getMediaUrlList() == (this.getMediaUrlList())) {
            return 0;
        }
        return 1;
    }
    public MessageObject clone() {

        MessageObject clone;
        try {
            clone = (MessageObject) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }
    public boolean getSender() {
        return sender;
    }
    public Context getContext1() {
        return context;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getSenderId() {
        return senderId;
    }
    public String getUid() {
        return uid;
    }
    public int getID() {
        return id;
    }
    public String getProfilePic() {
        return profilePic;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
}
