package com.beabox.hjy.tt;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.PostTDCodeService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.main.settings.handler.SweepHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.umeng.message.PushAgent;
import com.zxing.client.android.AmbientLightManager;
import com.zxing.client.android.BeepManager;
import com.zxing.client.android.CaptureActivityInterface;
import com.zxing.client.android.DecodeFormatManager;
import com.zxing.client.android.DecodeHintManager;
import com.zxing.client.android.FinishListener;
import com.zxing.client.android.InactivityTimer;
import com.zxing.client.android.IntentSource;
import com.zxing.client.android.Intents;
import com.zxing.client.android.ViewfinderView;
import com.zxing.client.android.camera.CameraManager;
import com.zxing.client.android.result.ResultHandler;
import com.zxing.client.android.result.ResultHandlerFactory;

/**
 * Created by fangjilue on 14-4-21.
 */
public class SweepActivity extends BaseActivity implements SurfaceHolder.Callback, CaptureActivityInterface, View.OnClickListener {

    private static final String TAG = SweepActivity.class.getSimpleName();

    private static final String _PRE_STRING = "skinrun://merchant/";


    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(
            ResultMetadataType.ISSUE_NUMBER,
            ResultMetadataType.SUGGESTED_PRICE,
            ResultMetadataType.ERROR_CORRECTION_LEVEL,
            ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;

    private SweepHandler handler;

    private Result savedResultToShow;

    private ViewfinderView viewfinderView;

    /**
     * 扫描提示，例如"请将条码置于取景框内扫描"之类的提示
     */
    //private TextView statusView;


    private Result lastResult;

    private boolean hasSurface;

    private IntentSource source;

    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】
     * 编码类型，该参数告诉扫描器采用何种编码方式解码，即EAN-13，QR Code等等
     * 对应于DecodeHintType.POSSIBLE_FORMATS类型
     * 参考DecodeThread构造函数中如下代码：hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
     */
    private Collection<BarcodeFormat> decodeFormats;

    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】
     * 字符集，告诉扫描器该以何种字符集进行解码
     * 对应于DecodeHintType.CHARACTER_SET类型
     * 参考DecodeThread构造器如下代码：hints.put(DecodeHintType.CHARACTER_SET, characterSet);
     */
    private String characterSet;

    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】
     * 该参数最终会传入MultiFormatReader，上面的decodeFormats和characterSet最终会先加入到decodeHints中
     * 最终被设置到MultiFormatReader中
     * 参考DecodeHandler构造器中如下代码：multiFormatReader.setHints(hints);
     */
    private Map<DecodeHintType, ?> decodeHints;

    /**
     * 活动监控器。如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
     * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
     */
    private InactivityTimer inactivityTimer;

    /**
     * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
     */
    private BeepManager beepManager;

    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    private AmbientLightManager ambientLightManager;

    private boolean light = false;

    /**
     * 打开系统图库
     */
    public static final int REQ_PICK_THUMB = 1;

    //扫描图片
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.decode_succeeded:
                    handleDecode((Result) msg.obj,true);
                    break;
                case R.id.decode_failed:
                    Toast.makeText(SweepActivity.this, getString(R.string.not_find_barcode), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.sweep);
    	postInit();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                if (!isFinishing()) {
                    finish();
                }
                break;
            case R.id.lightBtn:
                if(cameraManager.isOpen()){
                    cameraManager.setTorch(!light);
                    light = !light;
                }
                break;
            case R.id.imgLibBtn:
                openImgLib();
                break;
        }
    }

    void openImgLib(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, REQ_PICK_THUMB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQ_PICK_THUMB:
                    //获取选中图片的路径
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(picturePath);
                            if (result != null) {
                                Message message = Message.obtain(myHandler, R.id.decode_succeeded, result);
                                Bundle bundle = new Bundle();
                                message.setData(bundle);
                                message.sendToTarget();

                            } else {
                                Message message = Message.obtain(myHandler, R.id.decode_failed);
                                message.sendToTarget();
                            }
                        }
                    }).start();
                    break;
            }
        }
    }


    /**
     * 扫描二维码图片的方法
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0){
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = getSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RGBLuminanceSource getSource(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        return new RGBLuminanceSource(width,height,pixels);
    }

    protected void postInit() {
        // 在扫描功能开启后，保持屏幕处于点亮状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.lightBtn).setOnClickListener(this);
        findViewById(R.id.imgLibBtn).setOnClickListener(this);


        // 这里仅仅是对各个组件进行简单的创建动作，真正的初始化动作放在onResume中
        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        /**
         * 上面这段话的意思是说，相机初始化的动作需要开启相机并测量屏幕大小，这些操作
         * 不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的
         * 话，会导致扫描窗口的尺寸计算有误的bug
         */
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        //statusView = (TextView) findViewById(R.id.status_view);

        handler = null;
        lastResult = null;

        // 重置状态窗口，扫描窗口和结果窗口的状态
        resetStatusView();

        // 摄像头预览功能必须借助SurfaceView，因此也需要在一开始对其进行初始化
        // 如果需要了解SurfaceView的原理，参考:http://blog.csdn.net/luoshengyang/article/details/8661317
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            // 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
            surfaceHolder.addCallback(this);
        }

        // 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
        beepManager.updatePrefs();

        // 启动闪光灯调节器
        ambientLightManager.start(cameraManager);

        // 恢复活动监控器
        inactivityTimer.onResume();

        Intent intent = getIntent();

        // 加载配置，这些配置可以在应用启动后，在设置中进行设置，设置的结果会保存到SharePreferences中
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       /* copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
                && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));*/

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        // Barcode Scanner可以作为其他app的子组件来用，只需要通过Intent来调用即可
        // 如果需要设置一些参数来控制相机，可以将参数放在intent中
        if (intent != null) {
            String action = intent.getAction();

            // 调用方app通过设置Intent为com.google.zxing.client.android.SCAN来开启Barcode Scan的扫描功能
            // 这个设置动作在android-integation的IntenIntegrator中已设为默认的动作
            if (Intents.Scan.ACTION.equals(action)) {
                // Scan the formats the intent requested, and return the result
                // to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    //statusView.setText(customPromptMessage);
                    Toast.makeText(this, customPromptMessage, Toast.LENGTH_LONG).show();
                }
            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        // 暂停活动监控器
        inactivityTimer.onPause();

        // 停止闪光灯控制器
        ambientLightManager.stop();

        // 关闭摄像头
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止活动监控器
        inactivityTimer.shutdown();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
           /* case KeyEvent.KEYCODE_BACK: // 拦截返回键
                if (source == IntentSource.NATIVE_APP_INTENT) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                if ((source == IntentSource.NONE)
                        && lastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;*/
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light 开灯关灯
            /*case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;*/
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 向CaptureActivityHandler中发送消息，并展示扫描到的图像
     *
     * @param bitmap
     * @param result
     */
    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param sweepImg    是否为读取相册中的图片
     */
    public void handleDecode(Result rawResult,boolean sweepImg) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        beepManager.playBeepSoundAndVibrate();

        handleDecodeInternally(rawResult, resultHandler, sweepImg);
    }

    /**
     * 该方法会将最终处理的结果展示到result_view上。并且如果选择了"检索更多信息"则会内部对结果进行进一步的查询
     *
     * @param rawResult
     * @param resultHandler
     * @param sweepImg
     */
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler,boolean sweepImg) {
        //statusView.setVisibility(View.GONE);
       // viewfinderView.setVisibility(View.GONE);
        final String content = resultHandler.getDisplayContents().toString();
        
        log.e(TAG, "===============扫描结果："+content);
       // AppToast.toastMsg(this, "扫描结果："+content).show();
        try{
        	 new PostTDCodeService(this, HttpTagConstantUtils.POST_TDCODE, new HttpAysnResultInterface() {
				
				@Override
				public void dataCallBack(Object tag, int statusCode, Object result) {
					switch((Integer)tag){
					case HttpTagConstantUtils.POST_TDCODE:
						if(statusCode==201||statusCode==200){
							AppToast.toastMsg(SweepActivity.this, "关注商家成功！").show();
							finish();
						}else{
							AppToast.toastMsg(SweepActivity.this, "关注商家失败！").show();
							 restartPreviewAfterDelay(0L);
						}
						break;
					}
					
				}
			}).upLoad(DBService.getUserEntity().getToken(), content);
        }catch(Exception e){
        	e.printStackTrace();
        }
       
        final SweepActivity _this = this;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new SweepHandler(this, decodeFormats, decodeHints,
                        characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.zxing_msg_camera_framework_bug));
        builder.setPositiveButton(R.string.zxing_button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    /**
     * 在经过一段延迟后重置相机以进行下一次扫描。
     * 成功扫描过后可调用此方法立刻准备进行下次扫描
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    /**
     * 展示状态视图和扫描窗口，隐藏结果视图
     */
    private void resetStatusView() {

        //statusView.setText(R.string.zxing_msg_default_status);
        //statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
	}

	@Override
	public void setupView() {
		
	}

	@Override
	public void addListener() {
		
	}

	@Override
	public void sendMessageHandler(int messageCode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected String getActivityName() {
		return "扫一扫";
	}
}
