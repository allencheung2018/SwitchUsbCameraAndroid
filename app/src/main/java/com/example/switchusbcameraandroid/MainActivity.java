package com.example.switchusbcameraandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.camera_view)
    public View mTextureView;
    @BindView(R.id.drawLines)
    public Button DrawBtn;
    @BindView(R.id.imageView)
    public ImageViewDrawable imageView;
    @BindView(R.id.button)
    public Button takePicture;
    @BindView(R.id.button2)
    public Button cancel;
    @BindView(R.id.button3)
    public Button confirm;
    @BindView(R.id.button4)
    public Button front;
    @BindView(R.id.button5)
    public Button back;
    @BindView(R.id.button6)
    public Button left;
    @BindView(R.id.button7)
    public Button right;


    UVCCameraTextureView mUvcCameraTextureView;
    UVCCameraHelper mCameraHelper;

    private boolean isPreview = false;
    private boolean isRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCamera();
//        initCamera_2();
    }

    private void initCamera_2() {
        boolean isExistCamera = false;
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == 0) {
                isExistCamera = true;
                break;
            }
        }

        String[] list = {};
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics chars
                        = manager.getCameraCharacteristics(cameraId);
                // Do something with the characteristics
                int deviceLevel = chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                Log.d("initCamera_2", " **** device ["+cameraId+"] level:"+deviceLevel);
            }
        } catch(CameraAccessException e){
            e.printStackTrace();
        }
        try {
            list = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(list[0]);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initCamera() {
//        mUvcCameraTextureView = findViewById(R.id.camera_view);
        mUvcCameraTextureView = (UVCCameraTextureView) mTextureView;
        mUvcCameraTextureView.setCallback(mCallback);
//        mUvcCameraTextureView.setSurfaceTextureListener(surfaceTextureListener);
        mCameraHelper = UVCCameraHelper.getInstance();
//        mCameraHelper.setDefaultPreviewSize(1280, 720);
        mCameraHelper.setDefaultPreviewSize(1280, 720);
        mCameraHelper.initUSBMonitor(this, mUvcCameraTextureView, listener);
        DrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonClick", "isCameraOpened:"+mCameraHelper.isCameraOpened());
                mUvcCameraTextureView.hasSurface();
                if (!isPreview && mCameraHelper.isCameraOpened()){
                    drawContent();
//                    crearPunto(100, 100, 200, 200, Color.WHITE);
//                    drawImage();
                    imageView.setShow(1);
                    imageView.invalidate();
                }
            }
        });

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setShow(2);
                imageView.invalidate();
                Log.d("clikc", "front button");
//                imageView.setImageDrawable(null);
//                imageView.setImageResource(android.R.color.transparent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setShow(3);
                imageView.invalidate();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setShow(4);
                imageView.invalidate();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setShow(5);
                imageView.invalidate();
            }
        });

        getAndroiodScreenProperty();

        String picPath = UVCCameraHelper.ROOT_PATH + "SwitchUSBCamera" +"/images/"
                + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelper.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String picPath) {
                        Log.d("takePicture", "Path:"+picPath);
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showPicture(picPath);
                            }
                        });
                    }
                });
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(picPath);
                FileOutputStream out;
                imageView.buildDrawingCache(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
                try {
                    BufferedOutputStream bos= new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setDrawingCacheEnabled(false);
                imageView.setImageDrawable(null);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
            }
        });
    }

    public void showPicture(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }

    public static final Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("handler", "msg:"+msg.what);
        }
    };

    private CameraViewInterface.Callback mCallback = new CameraViewInterface.Callback() {
        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            Log.d("onSurfaceCreated", "isCameraOpened:"+mCameraHelper.isCameraOpened());
            if (!isPreview && mCameraHelper.isCameraOpened()){
                mCameraHelper.startPreview(mUvcCameraTextureView);
                isPreview = true;
                Log.d("onSurfaceCreated", "isPreview:"+isPreview);
                drawContent();
            }

        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
            Log.d("onSurfaceChanged", "isCameraOpened:"+mCameraHelper.isCameraOpened());
        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            if (isPreview && mCameraHelper.isCameraOpened()) {
                mCameraHelper.stopPreview();
                isPreview = false;
            }
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {
        @Override
        public void onAttachDev(UsbDevice device) {
            Log.d("onAttachDev", "device:"+device.getDeviceName());
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    List<UsbDevice> list = mCameraHelper.getUsbDeviceList();
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
            }

        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            Log.d("onConnectDev", "device:"+device.getDeviceName());
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {

        }
    };

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("KeyDown", "keyCode:"+keyCode + " event:"+event.getDevice());
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_CAMERA:
                    Log.d("KeyDown", "keyCode:"+keyCode + " event:"+event.getDevice());
            }
        }


        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.d("KeyDown", "keyCode:"+keyCode + " event:"+event.getDevice());
        return super.onKeyShortcut(keyCode, event);
    }

    public void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }


    private void drawContent() {
        Canvas canvas = mUvcCameraTextureView.lockCanvas();
        Log.d("drawContent", "Canvas:"+canvas);
        //画内容
//        canvas.drawColor(Color.WHITE);
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawCircle(200,300,100,paint);
//        canvas.drawLine(0,110, 500, 110, paint);
//        canvas.drawCircle(110, 110, 10.0f, paint);
        mUvcCameraTextureView.unlockCanvasAndPost(canvas);
    }

    int imageWidth = 100;
    int imageHeight = 100;
    private void crearPunto(float x, float y, float xend, float yend, int color) {
        imageWidth = imageView.getWidth();
        imageHeight = imageView.getHeight();
        Log.d("Punto", " iW="+imageWidth + " iH="+imageHeight);

        Bitmap bmp = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        Paint p = new Paint();
        p.setColor(color);
        c.drawLine(x, y, xend, yend, p);
        c.drawText("这是写上去的字",500,500,p);
        imageView.setImageBitmap(bmp);
//        imageView.draw(c);
    }

    public void drawImage(){
        Bitmap bmp = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(350, 350, 10, paint);
//        imageView.draw(canvas);
        imageView.setImageBitmap(bmp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }
}