package com.frostsowner.magic.weather.event;

import java.io.Serializable;

/**
 * Created by yulijun on 2016/11/12.
 */

public class ActionEvent extends Attributes implements Serializable{

    private static final long serialVersionUID = -1L;

    private int type;
    private Object data;

    public ActionEvent(int type){
        this.type = type;
    }

    public ActionEvent(int type, Object data){
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getSimpleName());
        buffer.append("[type=").append(getType()).append(",");
        buffer.append("attrs=").append(getAttrs()).append("]");
        return buffer.toString();
    }

    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
