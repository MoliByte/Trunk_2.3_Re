package com.base.app.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * Created by lidewen on 14-9-14.
 */
public class DesUtil {

    private final static String transformation = "DES/CBC/PKCS5Padding";
    private final static String DES = "DES";

    public static void main(String[] args) throws Exception {
        String key = BizConstant.data_file_key;
        String value="含量在1%以下的控油";

        String a=encrypt(value, key);
        System.out.println("加密后的数据为:"+a);
        String b=decrypt(a, key);
        System.out.println("解密后的数据:"+b);
//        testRead("/Users/lidewen/Downloads/t_base_skincontent.csv","/Users/lidewen/Downloads/result.csv");

    }

    public static void testRead(String oldFileName,String newFileName) throws Exception {
        String key = BizConstant.data_file_key;
        FileOutputStream fos=new FileOutputStream(new File(newFileName));
        FileInputStream in = new FileInputStream(new File(oldFileName));
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuffer bufSqlLine = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
            fos.write(encrypt(line,key).getBytes("UTF-8"));
            fos.write("\n".getBytes());
        }
        fos.flush();
        fos.close();

    }

    //解密数据
    public static String decrypt(String message,String key) throws Exception {
        //message = java.net.URLEncoder.encode(message, "utf-8");
        byte[] bytesrc =convertHexString(message);
        //创建Cipher对象
        Cipher cipher = Cipher.getInstance(transformation);
        //从原始密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        //创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // 用密钥初始化Cipher对象
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    public static String encrypt(String message, String key)
            throws Exception {
        //创建Cipher对象
        Cipher cipher = Cipher.getInstance(transformation);

        //从原始密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        //创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // 用密钥初始化Cipher对象
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] datas = cipher.doFinal(message.getBytes("UTF-8"));

        return toHexString(datas);
    }

    public static byte[] convertHexString(String ss)
    {
        byte digest[] = new byte[ss.length() / 2];
        for(int i = 0; i < digest.length; i++)
        {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte)byteValue;
        }

        return digest;
    }
    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }

        return hexString.toString();
    }
}
