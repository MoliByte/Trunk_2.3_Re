package com.beabox.hjy.tt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import com.app.base.init.MyApplication;
import com.base.app.utils.CameraUtil;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.FileUtil;
import com.base.app.utils.KVOEvents;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.image.ImageUtil;
import com.skinrun.trunk.facial.mask.test.PhotoType;
import com.zxing.client.android.camera.AutoFocusManager;
import com.zxing.client.android.camera.CameraConfigurationManager;


public class TakePhotoActivity extends Activity implements OnClickListener,
		Observer {
	private SurfaceView surfaceView;
	private Camera camera;
	//private boolean usePreview = false;
	private boolean isPreview;
	private AutoFocusManager autoFocusManager = null;
	private CameraConfigurationManager cameraConfigurationManager = null;
	boolean openCamera = true;
	private int testPreDiff = 0;
	private int testScore = 0;
	private boolean usePreview = false;
	private int crop_size = 300; // 裁剪大小

	final static int REQ_PICK_THUMB = 101;
	final static int CROP_PICTURE = 102;

	private Uri outTempUri = null;

	final static double IMAGE_SIZE = 0.6;// 大于0.6m 时压缩

	private int dis_width;// 相机屏幕宽
	private int dis_height;// 相机屏幕高

	private String shareTitle;
	private String shareContent;
	private String shareLinkUrl;
	float waterValues = 0, oilValues = 0, flexibleValues= 0;
	private int TestId;
	private int partFlag;
	// 默认取后置摄像头
	private int facing = Camera.CameraInfo.CAMERA_FACING_BACK;
	private boolean light = true;// 灯没有开需要打开
	private FileUtil fileUtil;


	private static int photoType = PhotoType.FACIAL_PHOTO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo2);
		initView();
		register();
	}

	private void initView() {
		findViewById(R.id.backBtn).setOnClickListener(this);
		findViewById(R.id.lightBtn).setOnClickListener(this);
		findViewById(R.id.cameraBtn).setOnClickListener(this);
		findViewById(R.id.openImgBtn).setOnClickListener(this);
		findViewById(R.id.takePhotoBtn).setOnClickListener(this);

		// 传递回来的值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			testPreDiff = bundle.getInt("testPreDiff");
			testScore = bundle.getInt("testScore");
			shareTitle = bundle.getString("shareTitle");
			shareContent = bundle.getString("shareContent");
			shareLinkUrl = bundle.getString("shareLinkUrl");
			waterValues=bundle.getFloat("waterValues");
			oilValues=bundle.getFloat("oilValues");
			flexibleValues=bundle.getFloat("flexibleValues");
			TestId=bundle.getInt("TestId");
			partFlag=bundle.getInt("partFlag");
			
			photoType = bundle.getInt("photoType");

		} else {
			testPreDiff = 0;
			testScore = 45;
			shareTitle = "";
			shareContent = "";
			shareLinkUrl = "";

		}
		fileUtil = new FileUtil(this);
		cameraConfigurationManager = new CameraConfigurationManager(this);

		DisplayMetrics dis = DensityUtil.getWindowRect(this);
		dis_width = dis.widthPixels ;
		dis_height = dis.heightPixels - DensityUtil.dip2px(this, 120);
		crop_size = Math.min(dis_width / 2, dis_height / 2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.lightBtn:
			torch(light);
			break;
		case R.id.cameraBtn:
			switchCamera();
			break;
		case R.id.openImgBtn:
			openPhotoAlbum();
			break;
		case R.id.takePhotoBtn:
			takePhoto();
			break;

		}
	};

	private void register() {
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHARE_SUCCESS, this);
	}

	private void initCamera() {
		closeCamera();

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			surfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setKeepScreenOn(true);// 屏幕常亮
		// surfaceHolder.setFixedSize(surfaceView.getMeasuredWidth(),
		// surfaceView.getMeasuredHeight());

		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 打开摄像头
				openCamera();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (camera != null) {
					Camera.Parameters parameters = camera.getParameters();
					if (176 == parameters.getPictureSize().width
							&& 144 == parameters.getPictureSize().height) {
						usePreview = true;
					}
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 释放摄像头
				closeCamera();
			}
		});
	}

	private void openCamera() {
		try{
			if (camera != null) {
				return;
			}
			int numberOfCameras = Camera.getNumberOfCameras();
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			int cameraId = 0;
			for (int i = 0; i < numberOfCameras; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == facing) {
					cameraId = i;
					
					camera = Camera.open(i);
					break;
				}
			}
			if (camera == null) {
				try {
					camera = Camera.open();
					facing = Camera.CameraInfo.CAMERA_FACING_BACK;
				} catch (Exception ex) {
					Log.i("error", ex.getMessage());
				}
			}

			if (camera == null) {
				// 无摄像头
				return;
			}

			CameraUtil.setCameraDisplayOrientation(this, cameraId, camera);
			if (camera != null && !isPreview) {
				Camera.Parameters parameters = camera.getParameters();
				// AppLog.debug("parameters1 = " + parameters.flatten());

				parameters.setPreviewFpsRange(4, 10);
				parameters.setPictureFormat(ImageFormat.JPEG);
				parameters.setJpegQuality(85);
				parameters.setPreviewFormat(ImageFormat.NV21);

				/*
				 * AppLog.debug("surfaceView: size = " + surfaceView.getWidth() +
				 * "," + surfaceView.getHeight()); List<Camera.Size> previewSizes =
				 * parameters.getSupportedPreviewSizes(); Camera.Size optimalSize =
				 * CameraUtil.getOptimalPreviewSize(this, previewSizes, (double)
				 * surfaceView.getWidth() / surfaceView.getHeight()); if
				 * (optimalSize != null) {
				 * parameters.setPreviewSize(optimalSize.width, optimalSize.height);
				 * AppLog.debug("surfaceView: previewSize = " + optimalSize.width +
				 * "," + optimalSize.height); }
				 * 
				 * List<Camera.Size> pictureSizes =
				 * parameters.getSupportedPictureSizes(); Camera.Size optimalSize2 =
				 * CameraUtil.getOptimalPreviewSize(this, pictureSizes, (double)
				 * parameters.getPreviewSize().width /
				 * parameters.getPreviewSize().height); if(optimalSize2 != null){
				 * parameters.setPictureSize(optimalSize2.width,
				 * optimalSize2.height); AppLog.debug("surfaceView: pictureSize = "
				 * + optimalSize2.width + "," + optimalSize2.height); }
				 * 
				 * try { camera.setParameters(parameters); } catch (Exception e) {
				 * AppLog.debug("camera#setParameters",e); }
				 */

				// AppLog.debug("parameters2 = " + parameters.flatten());
				try {
					camera.setPreviewDisplay(surfaceView.getHolder());
				} catch (IOException e) {
					Log.i("error", e.getMessage());
				}
				camera.startPreview();

				autoFocusManager = new AutoFocusManager(this, camera);
				isPreview = true;
			}
		}catch(Exception e){
			e.printStackTrace();
			AppToast.toastMsg(this, "亲，需要照相机权限哦~").show();
			finish();
			
		}
		
	}
	private void closeCamera() {
		// 释放摄像头
		if (camera != null) {
			autoFocusManager.stop();
			camera.setPreviewCallback(null);
			camera.setOneShotPreviewCallback(null);
			if (isPreview) {
				camera.stopPreview();
			}
			camera.release();
			camera = null;
		}
		isPreview = false;
	}

	// 切换摄像头
	private void switchCamera() {
		if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
			facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
		} else {
			facing = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		closeCamera();
		openCamera();
	}

	@Override
	public void onEvent(String event, Object... args) {
		if (KVOEvents.SHARE_SUCCESS.equals(event)) {
			if (!isFinishing()) {
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (openCamera) {
			initCamera();
		} else {
			openCamera = true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeCamera();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.SHARE_SUCCESS, this);
	}

	// 开灯关灯
		private synchronized void torch(boolean newSetting) {
			if (camera != null
					&& newSetting != cameraConfigurationManager
							.getTorchState(camera)) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				cameraConfigurationManager.setTorch(camera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
			light = !newSetting;
		}

		// 拍照逻辑
		private void takePhoto() {
			if (camera != null && isPreview) {
				if (!usePreview) {
					camera.takePicture(null, null, jpeg);
				} else {
					camera.setOneShotPreviewCallback(preview);
				}
			}
		}

		private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				double length = (data.length / 1000.0 / 1000.0);

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Bundle bundle = new Bundle();
					Bitmap currentBitmap = CameraUtil.createFromRawData(data,
							facing, Math.max(dis_width, dis_height));
					String fileName = fileUtil.savaBitmap(format.format(new Date())
							+ "_1.png", currentBitmap, length > IMAGE_SIZE ? 80 : 85);
					bundle.putString("fileName", fileName);
					if(currentBitmap==null){
						return;
					}
					if (!currentBitmap.isRecycled()) {
						currentBitmap.recycle();
					}

					putMarginParams(bundle);
					bundle.putInt("photoType", photoType);
					Intent intent = new Intent(TakePhotoActivity.this,
							PhotoShareActivity2.class);
					intent.putExtras(bundle);
					startActivity(intent);
					TakePhotoActivity.this.overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					finish();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// camera.startPreview(); // 拍完照后，重新开始预览
			}
		};

		Camera.PreviewCallback preview = new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera arg1) {
				double length = (data.length / 1000.0 / 1000.0);

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Camera.Parameters parameters = camera.getParameters();
					Camera.Size size = parameters.getPreviewSize();

					YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,
							size.width, size.height, null);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					yuvimage.compressToJpeg(new Rect(0, 0, arg1.getParameters()
							.getPreviewSize().width, arg1.getParameters()
							.getPreviewSize().height), 100, baos);

					Bundle bundle = new Bundle();
					Bitmap currentBitmap = CameraUtil.createFromRawData(
							baos.toByteArray(), facing,
							Math.max(dis_width, dis_height));
					if(currentBitmap==null){
						return;
					}
					
					String fileName = fileUtil.savaBitmap(format.format(new Date())
							+ "_2.png", currentBitmap, length > IMAGE_SIZE ? 80 : 85);

					bundle.putInt("photoType", photoType);
					bundle.putString("fileName", fileName);
					if (currentBitmap!=null&&!currentBitmap.isRecycled()) {
						currentBitmap.recycle();
					}

					putMarginParams(bundle);
					Intent intent = new Intent(TakePhotoActivity.this,
							PhotoShareActivity2.class);
					intent.putExtras(bundle);
					startActivity(intent);
					TakePhotoActivity.this.overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					
					finish();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

	private void putMarginParams(Bundle bundle) {
		bundle.putInt("testPreDiff", testPreDiff);
		bundle.putInt("testScore", testScore);

		bundle.putInt("photoType", photoType);
		bundle.putString("shareTitle", shareTitle);
		bundle.putString("shareContent", shareContent);
		bundle.putString("shareLinkUrl", shareLinkUrl);
		
		bundle.putFloat("waterValues", waterValues);
		bundle.putFloat("oilValues", oilValues);
		bundle.putFloat("flexibleValues", flexibleValues);
		bundle.putInt("TestId", TestId);
		bundle.putInt("partFlag", partFlag);
	}
	// 打开系统图库
		private void openPhotoAlbum() {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQ_PICK_THUMB);
			Log.e("", "===========调用相册");
		}

		@SuppressLint("NewApi")
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			openCamera = false;

			if (REQ_PICK_THUMB == requestCode && resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				File file = new File(picturePath);
				if (!file.exists()) {
					return;
				}
				long bytes = file.length();
				double size = bytes / 1000.0;
				/*
				 * if(size < 100.0){
				 * Toast.makeText(this,R.string.choose_image_small_tip
				 * ,Toast.LENGTH_LONG).show(); return; }
				 */

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				Uri outUri = fileUtil.getImageFileUri(format.format(new Date())
						.toString() + "_3.j");
				outTempUri = outUri;
				ImageUtil.cropImage(this, Uri.fromFile(file), outUri, crop_size,
						crop_size, CROP_PICTURE);
			} else if (CROP_PICTURE == requestCode && resultCode == RESULT_OK) {

				if (outTempUri == null) {
					return;
				}
				Bitmap photo = fileUtil.getBitmapByPath(outTempUri.getPath());
				if (photo == null) {
					return;
				}

				try {
					double length = 0;
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
						length = (photo.getRowBytes() * photo.getHeight()) / 1000.0 / 1000.0;
					} else {
						length = photo.getByteCount() / 1000.0 / 1000.0;
					}

					SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
					String fileName = fileUtil.savaBitmap(format.format(new Date())
							+ "_3.png", photo, length > IMAGE_SIZE ? 80 : 85);
					Bundle bundle = new Bundle();
					bundle.putString("fileName", fileName);
					bundle.putInt("photoType", photoType);

					putMarginParams(bundle);
					Intent intent = new Intent(TakePhotoActivity.this,
							PhotoShareActivity2.class);
					intent.putExtras(bundle);

					startActivity(intent);
					TakePhotoActivity.this.overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					finish();
				} catch (Exception ex) {
				}
			}
		}

		@Override
		public void finish() {
			super.finish();
			/*overridePendingTransition(R.anim.activity_enter_from_left,
					R.anim.activity_exit_to_right);*/
		}

}
