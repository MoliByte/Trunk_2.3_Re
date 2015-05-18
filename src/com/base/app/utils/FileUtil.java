package com.base.app.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.app.base.entity.UserEntity;

/**
 * Lerry
 */
public class FileUtil {
	
	/** 
     * sd卡的根目录 
     */  
    private String mSdRootPath = null;
    /** 
     * 手机的缓存根目录 
     */  
    private String mDataRootPath = null;  
    /** 
     * 保存Image的目录名 
     */  
    private final static String IMG_FOLDER_NAME = "/hjy/jpg";

    //旧文件目录
    private final static String Old_IMG_FOLDER_NAME = "/hjy/img";
    /**
     * 保存app的目录名 
     */ 
    private final static String APP_FOLDER_NAME = "/hjy/app";
      
      
    public FileUtil(Context context){  
        mDataRootPath = context.getCacheDir().getPath();  
        mSdRootPath = Environment.getExternalStorageDirectory().getPath(); 
    }  
      
  
    /** 
     * 获取储存Image的目录 
     * @return 
     */  
    public String getImageStorageDirectory(){  
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?  
                mSdRootPath + IMG_FOLDER_NAME : mDataRootPath + IMG_FOLDER_NAME;  
    }

    //旧文件目录
    private String getOldImageStorageDirectory(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + Old_IMG_FOLDER_NAME : mDataRootPath + Old_IMG_FOLDER_NAME;
    }
    
    /** 
     * 获取储存app的目录 
     * @return 
     */  
    public String getAppStorageDirectory(){  
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?  
                mSdRootPath + APP_FOLDER_NAME : mDataRootPath + APP_FOLDER_NAME;  
    }  
      
    /** 
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录 
     * @param fileName 文件名
     * @param bitmap
     * @param quality 0 ~ 100 default 100
     * @return 完整路径
     * @throws java.io.IOException
     */  
    public String savaBitmap(String fileName, Bitmap bitmap, int quality) throws IOException{
        if(bitmap == null){ 
        	Log.e("FileUtil", "=========保存图片时候bitmap为空");
            return "";
        }

        if(quality < 0 || quality > 100){
            quality = 100;
        }

        String path = getImageStorageDirectory();  
        File folderFile = new File(path);  
        if(!folderFile.exists()){  
            folderFile.mkdirs();  
        }
        String filePath = path + File.separator + fileName;
        File file = new File(filePath);
        file.createNewFile();  
        FileOutputStream fos = new FileOutputStream(file);  
        bitmap.compress(CompressFormat.PNG, quality, fos);
        fos.flush();  
        fos.close();
        return filePath;
    }

    /**
     * 从手机或sd卡获取bitmap base64
     * @param filePath 完整路径
     * @return
     */
    public String getBitmapBase64(String filePath){

        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();

            byte[] base64 = Base64.encodeBase64(buffer);

            return new String(base64,"utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
      
    /** 
     * 从手机或者sd卡获取Bitmap 
     * @param fileName 文件名
     * @return 
     */  
    public Bitmap getBitmap(String fileName){  
        return BitmapFactory.decodeFile(getImageStorageDirectory() + File.separator + fileName);  
    }

    /**
     * 从文件路径获取Bitmap
     * @param filePath 全路径
     * @return
     */
    public Bitmap getBitmapByPath(String filePath){
        return BitmapFactory.decodeFile(filePath);
    }

    /** 
     * 判断image文件是否存在 
     * @param fileName 文件名
     * @return 
     */  
    public boolean isImageFileExists(String fileName){  
        return new File(getImageStorageDirectory() + File.separator + fileName).exists();  
    }

    /**
     * 删除文件
     * @param fileName 文件名
     */
    public void deleteImageFile(String fileName){
        if(isImageFileExists(fileName)){
            File file = new File(getImageStorageDirectory() + File.separator + fileName);
            file.delete();
        }
        return;
    }

    /**
     *
     * @param fileName 文件名
     * @return
     */
    public Uri getImageFileUri(String fileName){
        File file = new File(getImageStorageDirectory());
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getImageStorageDirectory() + File.separator + fileName);
        return Uri.fromFile(file);
    }

      
    /** 
     * 获取image文件的大小 
     * @param fileName 文件名
     * @return 
     */  
    public long getImageFileSize(String fileName) {  
        return new File(getImageStorageDirectory() + File.separator + fileName).length();  
    }
    
    /** 
     * 删除SD卡或者手机的老的缓存图片和目录
     */  
    public void deleteOldImageFile() {
        File dirFile = new File(getOldImageStorageDirectory());
        if(! dirFile.exists()){  
            return;  
        }  
        if (dirFile.isDirectory()) {  
            String[] children = dirFile.list();  
            for (int i = 0; i < children.length; i++) {  
                new File(dirFile, children[i]).delete();
            }
        }  
          
        dirFile.delete();
    }
    
    
    /**
     * 将图片下载到SD卡缓存起来。
     * @param imageUrl 图片的URL地址
     * @param localPath 图片本地全路径
     */
    public boolean downloadImage(String imageUrl,String localPath) {
        boolean ok = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //AppLog.debug("monted sdcard");
        } else {
            //AppLog.debug("has no sdcard");
        }
        HttpURLConnection con = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File imageFile = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setDoInput(true);
            //加x-token头信息
            //Session session = Session.getInstance();
            UserEntity session = DBService.getDB().findAll(UserEntity.class).get(0) ;
            if (session != null && session.getToken() != null) {
                con.setRequestProperty("X-Token", session.getToken());
            }
            //con.setDoOutput(true);//设置为true 不能显示图片
            //con.setRequestMethod("GET");
            bis = new BufferedInputStream(con.getInputStream());
            if(localPath == null){
                localPath = getImageLocalPath(imageUrl);
            }
            imageFile = new File(localPath);
            fos = new FileOutputStream(imageFile);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
                bos.flush();
            }
            ok = true;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("img download error :", imageUrl);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return ok;
    }

    /**
     * 获取图片的本地存储路径。
     * 根据imageUrl 作hashCode
     * 
     * @param imageUrl
     *            图片的URL地址。
     * @return 图片的本地存储路径。
     */
    public String getImageLocalPath(String imageUrl) {
        /*int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageName = imageUrl.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory().getPath() + "/PhotoWallFalls/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        return imagePath;
        */
        File file = new File(getImageStorageDirectory());
        if (!file.exists()) {
            file.mkdirs();
        }

        return getImageStorageDirectory() + File.separator + imageUrl.hashCode()+".p";

    }
	
	/**
	 * 将文件写到SD卡或内存，优先写到SD卡
	 * @param fileName
	 * @param input
	 */
	public void writeFile(String fileName, InputStream input) {
		String filePath = getAppStorageDirectory();
		File folderFile = new File(filePath);  
        if(!folderFile.exists()){  
            folderFile.mkdirs();  
        }
		File file = new File(folderFile, fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			byte[] b = new byte[2048];
			int j = 0;
			while ((j = input.read(b)) != -1) {
				fos.write(b, 0, j);
			}
			fos.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static Intent getFileIntent(File file) {
		Uri uri = Uri.fromFile(file);
		String type = getMIMEType(file.getName());
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, type);
		return intent;
	}

	private static String getMIMEType(String fName) {
		String type = "";
		/* 取得扩展名 */
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("pdf")) {
			type = "application/pdf";//
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			// /*如果无法直接打开，就跳出软件列表给用户选择 */
			type = "*/*";
		}
		return type;
	}

    /**
     * nio 文件拷贝
     * @param source
     * @param target
     * @return
     */
    public static boolean nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
