package com.rance.chatui.util;

import android.os.Environment;
import android.util.Base64;
import org.apache.commons.codec.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class TransCode {
    public static String byteToImage(String base64){

        InputStream in = new ByteArrayInputStream(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        File fileDir = new File(Environment.getExternalStorageDirectory(), "ReceivePic");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File file = new File(fileDir, System.currentTimeMillis() + ".jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] tmp = new byte[1024];
            int len = 0;
            while((len = in.read(tmp)) != -1){
                out.write(tmp, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static String byteToVoice(String base64){

        InputStream in = new ByteArrayInputStream(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        File fileDir = new File(Environment.getExternalStorageDirectory(), "ReceiveVoi");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File file = new File(fileDir, System.currentTimeMillis() + ".amr");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] tmp = new byte[1024];
            int len = 0;
            while((len = in.read(tmp)) != -1){
                out.write(tmp, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static String sourceToByte(String filePath){
        byte[] data = null;
        FileInputStream input;
        try {
            input = new FileInputStream(new File(filePath));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return new String(Base64.encode(data, Base64.DEFAULT));

    }
}
