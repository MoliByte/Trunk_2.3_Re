package com.idongler.image;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ScrollView;

import com.base.app.utils.DensityUtil;

/**
 * Created by fangjilue on 14-5-6.
 */
public class ImageUtil {


    /**
     * @param options
     * @param reqWidth
     * @return
     */
    private static int calculateInWidthSize(BitmapFactory.Options options, int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 如果最大值没有超过 max 原比例输出
     *
     * @param options
     * @param max
     * @return
     */
    private static int calculateRatio(BitmapFactory.Options options, int max) {
        // 源图片的宽度
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (width > max || height > max) {

            if (width >= height) {
                // 计算出实际宽度和目标宽度的比率
                final int widthRatio = Math.round((float) width / (float) max);
                inSampleSize = widthRatio;
            } else {
                // 计算出实际宽度和目标宽度的比率
                final int heightRatio = Math.round((float) height / (float) max);
                inSampleSize = heightRatio;
            }
        }
        return inSampleSize;
    }


    /**
     * 按宽度等比缩放，reqWidth <= 0 图片不缩放
     *
     * @param pathName
     * @param reqWidth
     * @return
     */
    @Deprecated
    public static Bitmap decodeBitmapWidthFromResource(String pathName, int reqWidth) {
        if (reqWidth > 0) {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInWidthSize(options, reqWidth);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(pathName, options);
        } else {
            return BitmapFactory.decodeFile(pathName);
        }
    }

    /**
     * 按最大值等比缩放，如果高或宽没有超过最大值按原图显示
     *
     * @param pathName
     * @param max
     * @return
     */
    @Deprecated
    public static Bitmap decodeBitmapFromResource(String pathName, int max) {
        if (max > 0) {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateRatio(options, max);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(pathName, options);
        } else {
            return BitmapFactory.decodeFile(pathName);
        }
    }

    /**
     * 创建缩略图
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createScaleBitmap(Bitmap src, int width, int height) {
        Matrix m = new Matrix();

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float scale = 1;
        //竖图
        if (srcWidth < srcHeight) {
            scale = width / (float) srcWidth;
        } else {
            scale = height / (float) srcHeight;
        }

        m.postScale(scale, scale);

        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);

        return bitmap;
    }


    /**
     * 创建缩略图
     *
     * @param pathName
     * @param max
     * @return
     */
    public static Bitmap createScaleBitmap(String pathName, int max) {
        File file = new File(pathName);
        if (!file.exists()) {
            return null;
        }
        if (max > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathName);

            return createScaleBitmap(bitmap, max);
        } else {
            return BitmapFactory.decodeFile(pathName);
        }
    }

    /**
     * 创建缩略图
     *
     * @param src
     * @param max
     * @return
     */
    public static Bitmap createScaleBitmap(Bitmap src, int max) {
        Matrix m = new Matrix();
        if(src!=null){
	        int srcWidth = src.getWidth();
	        int srcHeight = src.getHeight();
	
	        float scale = (max / (float) (Math.max(srcWidth, srcHeight)));
	        m.postScale(scale, scale);
	
	        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	
	        return bitmap;
        }
        return null;
    }

    public static Bitmap createScaleBitmapWithWidth(Bitmap src, int width) {
        Matrix m = new Matrix();

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float scale = (width / (float) srcWidth);
        m.postScale(scale, scale);

        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);

        return bitmap;
    }

    /**
     * 裁剪图片意图
     *
     * @param activity
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    public static void cropImage(Activity activity, Uri uri, int outputX, int outputY, int requestCode) {
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        //图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 根据 outUri 生成裁剪后的图片
     *
     * @param activity
     * @param uri
     * @param outUri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    public static void cropImage(Activity activity, Uri uri, Uri outUri, int outputX, int outputY, int requestCode) {
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //裁剪框的比例，1：1
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        //图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 创建带有角标数字的圆形
     *
     * @param context
     * @param num
     * @param bgColor getResources().getColor(R.color.bg_red);
     * @return
     */
    public static Drawable createCircleFlag(Context context, String num, int bgColor) {
        int textSize = DensityUtil.sp2px(context, 25);
        int r = textSize;

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(2 * r, 2 * r, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //创建圆
        Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bgColor);
        paint.setAntiAlias(true);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(r, r, r, paint);

        //写字
        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int h = (int) (fontMetrics.bottom - fontMetrics.top) / 4;

        canvas.drawText(num, r, r + h, paint);
        Drawable dr = new BitmapDrawable(null, output);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        return dr;
    }

    /**
     * 截屏方法，截到的界面是传到getViewBitmap方法中的View。
     */
    public static Bitmap getScreenShot(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    //压缩
    public static Bitmap compress(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 85, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        //Base64.encodeToString(bytes, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    //截取ScrollView的屏幕
    
    public static Bitmap getBitmapByView(ScrollView scrollView) {  
        int h = 0;  
        Bitmap bitmap = null;  
        // 获取scrollview实际高度  
        for (int i = 0; i < scrollView.getChildCount(); i++) {  
            h += scrollView.getChildAt(i).getHeight();  
            scrollView.getChildAt(i).setBackgroundColor(  
                    Color.parseColor("#ffffff"));  
        }  
        // 创建对应大小的bitmap  
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,  
                Bitmap.Config.RGB_565);  
        final Canvas canvas = new Canvas(bitmap);  
        scrollView.draw(canvas);  
        return bitmap;  
    } 
    //肌肤测试分享截图
    public static Bitmap getScreenBitmap(View v){
    	Bitmap bitmap=Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
    	Canvas canvas=new Canvas(bitmap);
    	v.draw(canvas);
    	return bitmap;
    }
}
