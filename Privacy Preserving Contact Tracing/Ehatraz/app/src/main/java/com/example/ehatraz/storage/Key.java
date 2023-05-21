package com.example.ehatraz.storage;

import java.io.Serializable;
import java.util.ArrayList;

public class Key implements Serializable {

    private String key;
    private String value;

    /*
       ---varabiles
            key: String
            value: String
     */
    public Key()
    {

    }
    public Key(String key)
    {
        this.key = key;
    }

    private Key(String key,String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public static boolean contains(String key, ArrayList<Key> keys)
    {
        boolean flage = false;
        for(Key k: keys)
        {
            if(k.getKey().equals(key)){
                flage = true;
                break;
            }
        }
        return flage;
    }

}
