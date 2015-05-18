package com.base.app.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by beabox on 14-11-20.
 */
public class CommontUtil {

    public static int dip2px( Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip( Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 根据手机分辨率从sp转成px
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String getColorStr(String needString,String colorString){
        final String SPAN_START = "<font color=\""+ colorString + "\">";
        final String SPAN_END = "</font>";
        String newString = new StringBuilder().append(SPAN_START).append(needString).append(SPAN_END).toString();
        return newString;
    }

    public static String getNewStr(String result,String needReplaceStr,String replaceStr){
        if (result != null && result.length() > 0) {
            return result.replaceAll(needReplaceStr, replaceStr);
        }
        return "";
    }

    public static String getStringFileName(String needString) {
        return needString.substring(needString.lastIndexOf('/'));
    }

    public static String getFileExtName(String fileName) {
        String ret = "";
        if (fileName == null) {
            return ret;
        }

        int pos = fileName.lastIndexOf(".");
        if (pos == -1) {
            return ret;
        }

        return fileName.substring(pos + 1);
    }

    public static String getFirstLowerToCase(String fildeName){
        byte[] items = fildeName.getBytes();
        items[0] = (byte)((char)items[0]-'a'+'A');;
        return new String(items);
    }

    public static String getCostTime(int time) {
        if (time == 0) {
            return "0秒";
        } else if (time < 60) {
            return time + "秒";
        } else if (time >= 60 && time < 3600) {
            int min = (time %(60*60)) /(60);
            int second =  (time %(60));
            return min + "分" + second + "秒";
        } else if (time >= 3600) {
            int hour =  time /(60*60);
            int min = (time %(60*60)) /(60);
            int second =  (time %(60));
            return hour + "时" + min + "分" + second + "秒";
        } else {
            return "";
        }
    }

    //打开软键盘
    public static void showSoftInput(Context context,View view){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    //关闭软键盘
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static List<String> getWordList(String wordString) {
        List<String> list = new ArrayList<String>();
        String[] s = wordString.split(",");
        for (String numString : s) {
            list.add(numString);
        }
        return list;
    }

    /**
     * 返回byte的数据大小对应的文本
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        DecimalFormat format = new DecimalFormat("###.00");
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return format.format(kbsize) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return format.format(mbsize) + "M";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return format.format(gbsize) + "GB";
        } else {
            return "size:error";
        }
    }

    public static String getWordString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String word : list) {
            sb.append(",").append(word);
        }
        return sb.toString().substring(1);
    }

    public static boolean wifiAvailable (Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static int getScreenWidth(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    public static String getChapterIdString(Map<Integer,String> map) {
        StringBuilder sb = new StringBuilder();
        for (Integer key : map.keySet()) {
            sb.append("," + key.toString());
        }
        return sb.toString().substring(1);
    }

    /**
     69. * Get the value of the data column for this Uri. This is useful for
     70. * MediaStore Uris, and other file-based ContentProviders.
     71. *
     72. * @param context The context.
     73. * @param uri The Uri to query.
     74. * @param selection (Optional) Filter used in the query.
     75. * @param selectionArgs (Optional) Selection arguments used in the query.
     76. * @return The value of the _data column, which is typically a file path.
     77. */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     103. * @param uri The Uri to check.
     104. * @return Whether the Uri authority is ExternalStorageProvider.
     105. */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     111. * @param uri The Uri to check.
     112. * @return Whether the Uri authority is DownloadsProvider.
     113. */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     119. * @param uri The Uri to check.
     120. * @return Whether the Uri authority is MediaProvider.
     121. */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     127. * @param uri The Uri to check.
     128. * @return Whether the Uri authority is Google Photos.
     129. */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String selectImage(Context context,Intent data){
        Uri selectedImage = data.getData();
        if(selectedImage!=null){
            String uriStr=selectedImage.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        } else {
            return 0;
        }
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * Gets the content:// URI  from the given corresponding path to a file
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Uri uri = null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            uri = Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);

                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }

        cursor.close();
        return uri;
    }

    private static int px2dip(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static final String APPLICATION_PACKAGE_NAME = "com.beabox.hjy.tt";

    public static boolean isInIm(Context context) {
        try {
            if (context instanceof Activity) {
                ActivityManager am = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).size() > 0 ? am
                        .getRunningTasks(1).get(0).topActivity : null;
                if (cn != null && cn.getClassName().contains(APPLICATION_PACKAGE_NAME)) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static int getElementSzie(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screenHeight = px2dip(dm.heightPixels, context);
            int screenWidth = px2dip(dm.widthPixels, context);
            int size = screenWidth / 6;
            if (screenWidth >= 800) {
                size = 60;
            } else if (screenWidth >= 650) {
                size = 55;
            } else if (screenWidth >= 600) {
                size = 50;
            } else if (screenHeight <= 400) {
                size = 30;
            } else if (screenHeight <= 480) {
                size = 35;
            } else if (screenHeight <= 520) {
                size = 40;
            } else if (screenHeight <= 570) {
                size = 45;
            } else if (screenHeight <= 640) {
                if (dm.heightPixels <= 960) {
                    size = 45;
                } else if (dm.heightPixels <= 1000) {
                    size = 55;
                }
            }
            return size;
        }
        return 40;
    }

    public static int getDefaultPannelHeight(Context context) {
        if (context != null) {
            int size = (int) (getElementSzie(context) * 5.5);
            return size;
        } else {
            return 300;
        }
    }


}

