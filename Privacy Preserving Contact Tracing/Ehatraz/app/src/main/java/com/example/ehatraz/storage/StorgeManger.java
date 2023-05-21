package com.example.ehatraz.storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StorgeManger
{
    private Context mContext;

    public static final String TEK_FILE = "TEK";
    public static final String KEY_FILE = "Key_RPI";
    public static final String USER_ID = "UID";
    public static final String LOCATION_FILE = "Locations";
    private static final String TAG = "StorgeManger";

    private static StorgeManger instance = null;
/*
    -----Varabilse
         + TEK_FILE: String
         + KEY_FILE: String
         + USER_ID: String
         + LOCATION_FILE: String
         + TAG: String
    ----Methods
        +writeText(text: String,fname: String):void
        +loadText(fname: String):String
        +checkFile(fname: String):void
    ----relations
        with MangeBeacons

 */
    private StorgeManger(Context context)
    {
        this.mContext = context;
        checkFile(TEK_FILE);
        checkFile(KEY_FILE);
        checkFile(USER_ID);
        checkFile(LOCATION_FILE);

    }
    public static StorgeManger getInstance(Context context){
        if(instance == null)
            instance = new StorgeManger(context);
        return instance;
    }
    public void writeText(String text,String fname)
    {
        FileOutputStream fos = null;
        try
        {
            fos = instance.mContext.openFileOutput(fname,Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            Log.i(TAG,"write to file: "+fname+" succfully");
        }catch (Exception e)
        {
            Log.i(TAG,"Unable to write to file: "+fname+" "+ e.toString());
        }finally {
            if(fos !=null)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String loadText(String fname)
    {
        FileInputStream fin = null;
        StringBuilder sb = null;
        try {
            fin = instance.mContext.openFileInput(fname);
            InputStreamReader inputStream = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(inputStream);
            sb = new StringBuilder();
            String text;
            while((text = br.readLine()) != null)
            {
                sb.append(text).append("\n");
            }
        }catch (Exception e)
        {
            Log.i(TAG,"Unable to load from file: "+ fname+" "+e.toString());
        }finally {
            if(fin != null)
            {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
    public void checkFile(String fname)  {
        fname = mContext.getFilesDir().getAbsolutePath() + File.separator +fname;
                File file = new File(fname);
        if(!file.exists())
        {
            try {
                file.createNewFile();
                Log.i(TAG,"File created sccussfully");
            }catch (IOException e)
            {
                Log.i(TAG,"Unable to create file: "+fname+" "+e.toString());
            }
        }
    }
    public void writeBeacons(List<Key> keys)
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {

             fos =  mContext.openFileOutput(KEY_FILE, Context.MODE_PRIVATE);
             oos = new ObjectOutputStream(fos);
             oos.writeObject(keys);

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }finally {
            if(fos != null)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oos != null)
            {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public List<Key>  readBeacons()
    {
        List<Key> keys = new ArrayList<>();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try
        {

             fis = mContext.openFileInput(KEY_FILE);
             ois = new ObjectInputStream(fis);

            keys = (ArrayList<Key>) ois.readObject();

            ois.close();
            fis.close();
        }
        catch (IOException ioe)
        {
           ioe.printStackTrace();

        }
        catch (ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();

        }finally {
            if(fis != null)
            {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ois != null)
            {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return keys;
    }

}
