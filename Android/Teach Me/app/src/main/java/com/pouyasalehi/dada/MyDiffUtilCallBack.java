package com.pouyasalehi.dada;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;

public class MyDiffUtilCallBack extends DiffUtil.Callback {
    ArrayList<MessageObject> newList;
    ArrayList<MessageObject> oldList;

    public MyDiffUtilCallBack(ArrayList<MessageObject> newList, ArrayList<MessageObject> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getID() == oldList.get(oldItemPosition).getID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newList.get(newItemPosition).compareTo(oldList.get(oldItemPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        MessageObject newModel = newList.get(newItemPosition);
        MessageObject oldModel = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (newModel.getMediaUrlList() != (oldModel.getMediaUrlList())) {
            diff.putStringArrayList("media", newModel.getMediaUrlList());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
