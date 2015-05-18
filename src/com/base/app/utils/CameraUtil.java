package com.base.app.utils;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by fangjilue on 14-10-3.
 */
public class CameraUtil {
    public static final float IMAGE_W = 800.0f;


    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     *
     * @param data
     * @param facing
     * @param image_max &lt; CameraUtil.IMAGE_W
     * @return
     */
    public static Bitmap createFromRawData(byte[] data, int facing, int image_max) {
        Log.i("Test", "raw data size:" + (data.length / 1000.0 / 1000.0) + "mb");
        try{
        	Bitmap b1 = BitmapFactory.decodeByteArray(data, 0, data.length);
        	Matrix m = new Matrix();
        	int b1w = b1.getWidth();
        	int b1h = b1.getHeight();
        	
        	m.setRotate(facing == Camera.CameraInfo.CAMERA_FACING_FRONT ? 270 : 90);
        	float scale = (Math.min(IMAGE_W,image_max)/ (float)(Math.max(b1w,b1h)));
        	
        	m.postScale(scale,scale);
        	Bitmap bitmap = Bitmap.createBitmap(b1, 0, 0, b1.getWidth(), b1.getHeight(), m, true);
        	if (bitmap != b1) {
        		b1.recycle();
        	}
        	return bitmap;
        	
        }catch(OutOfMemoryError e){
        	return null ;
        }catch(Exception e){
        	return null ;
        }
    }


    public static Camera.Size getOptimalPreviewSize(Activity activity, List<Camera.Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size
        DisplayMetrics display = DensityUtil.getWindowRect(activity);
        int targetHeight = Math.min(display.widthPixels, display.heightPixels);

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.v("test", "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.v("test", String.format("Optimal preview size is %sx%s",optimalSize.width, optimalSize.height));
        return optimalSize;
    }
}
