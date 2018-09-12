package com.scinc.sczbar;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;

/*** 这个类是二维码扫描界面
 *
 * @author Zhang Chao
 * @date 2018-9-12 17:00
 * @version 1
 */
public class ScanActivity extends AppCompatActivity {

    private static final String TAG = ScanActivity.class.getName();

    public static String path;

    private TextureView tvPreview;

    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private Handler mainHandler;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraSession;

    private ImageReader mImageReader;
    private ImageScanner mImageScanner;

//    private volatile Size capSize = new Size(1080, 1980);

    private volatile boolean isFinish = false;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        tvPreview = findViewById(R.id.tv_preview);
        mImageScanner = new ImageScanner();
        //noinspection ConstantConditions
        path = getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 初始化线程
     */
    private void init() {
        cameraThread = new HandlerThread("camera");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        mainHandler = new Handler(getMainLooper());
    }

    /**
     * 初始化图片解析对象
     */
    private void initImageReader() {
        mImageReader = ImageReader.newInstance(1080, 1920, YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Log.d(TAG, "onImageAvailable: ");
                Image image = imageReader.acquireNextImage();
                y800Decode(image);
            }
        }, cameraHandler);
    }

    /**
     * 使用y800格式解析二维码图片
     *
     * @param image 摄像头获取的实时图片
     */
    private void y800Decode(Image image) {
        ByteBuffer buffer0 = image.getPlanes()[0].getBuffer();
        byte[] b0 = new byte[buffer0.remaining()];
        buffer0.get(b0);
        net.sourceforge.zbar.Image i = new net.sourceforge.zbar.Image(1440, 1080, "Y800");
        i.setData(b0);
        int result = mImageScanner.scanImage(i);
        if (result != 0) {
            SymbolSet syms = mImageScanner.getResults();
//            for (Symbol sym : syms) {
//                Log.d(TAG, "result: " + sym.getData());
//                stopScan();
//                if (isFinish) {
//                    return;
//                }
//                isFinish = true;
//                ScanResultDealer.deal(ScanActivity.this, sym.getData());
//                image.close();
//                return;
//            }
            String codeString = ((Symbol) syms.toArray()[0]).getData();
            Log.d(TAG, "result: " + codeString);
            stopScan();
            if (isFinish) {
                return;
            }
            isFinish = true;
            ScanResultDealer.deal(ScanActivity.this, codeString);
            image.close();
            return;
        } else {
            Log.d(TAG, "result: failed");
        }
        image.close();
    }

//    private void decode(Image image) {
//        String format = "";
//        ByteBuffer buffer0 = image.getPlanes()[0].getBuffer();
//        ByteBuffer buffer1 = image.getPlanes()[1].getBuffer();
//        ByteBuffer buffer2 = image.getPlanes()[2].getBuffer();
//
//        byte[] b0 = new byte[buffer0.remaining()];
//        buffer0.get(b0);
//        byte[] b1 = new byte[buffer1.remaining()];
//        buffer1.get(b1);
//        byte[] b2 = new byte[buffer2.remaining()];
//        buffer2.get(b2);
//
//        byte[] data = makeDataArray(b0, b1, b2);
//
//        net.sourceforge.zbar.Image i = new net.sourceforge.zbar.Image(1440, 1080, format);
//        i.setData(data);
//        int result = mImageScanner.scanImage(i);
//        if (result != 0) {
//            SymbolSet syms = mImageScanner.getResults();
//            for (Symbol sym : syms) {
//                Log.d("onImageAvailable", "result: " + sym.getData());
//                func(sym.getData());
//            }
//        } else {
//            Log.d("onImageAvailable", "result: failed");
//        }
//        image.close();
//    }
//
//    private byte[] makeDataArray(byte[] a, byte[] b, byte[] c) {
//        byte[] temp = new byte[a.length + b.length + c.length];
//        System.arraycopy(a, 0, temp, 0, a.length);
//        System.arraycopy(b, 0, temp, a.length, b.length);
//        System.arraycopy(c, 0, temp, a.length + b.length, c.length);
//        return temp;
//    }
//
//    private void func(String result) {
//        stopScan();
//        if (listener != null) {
//            listener.decode(result);
//        }
//        finish();
//    }

    /**
     * 界面有焦点时开始扫描
     * 初始化后台线程与图片解析对象
     */
    @Override
    protected void onResume() {
        super.onResume();
        init();
        initImageReader();
        tvPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int w, int h) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

    /**
     * 打开摄像头
     */
    @SuppressLint("MissingPermission")
    public void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        if (manager == null) {
            return;
        }
        try {
            manager.openCamera("0", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    if (mCameraDevice != null) {
                        mCameraDevice.close();
                    }
                    mCameraDevice = cameraDevice;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    if (mCameraDevice != null) {
                        mCameraDevice.close();
                        mCameraDevice = null;
                    }
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    if (mCameraDevice != null) {
                        mCameraDevice.close();
                        mCameraDevice = null;
                    }
                }
            }, null);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
            StreamConfigurationMap map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP);
            initCapSize(map);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 配置预览参数
     *
     * @param map 系统支持的预览尺寸的map
     */
    private void initCapSize(StreamConfigurationMap map) {
        Size[] allSizes = null;
        if (map != null) {
            allSizes = map.getOutputSizes(YUV_420_888);
        }
        if (allSizes == null || allSizes.length == 0) {
            return;
        }
        int max = 0;
        for (Size size : allSizes) {
            int cur = size.getHeight() * size.getWidth();
            if (cur > max) {
                max = cur;
            }
        }
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        Log.d(TAG, "startPreview: ");
        try {
            Surface pre = new Surface(tvPreview.getSurfaceTexture());
            final CaptureRequest.Builder builder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(pre);
            builder.addTarget(mImageReader.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(pre, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mCameraSession = cameraCaptureSession;
                    CaptureRequest request = builder.build();
                    try {
                        cameraCaptureSession.setRepeatingRequest(request, null, mainHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止扫描方法
     */
    private void stopScan() {
        if (mCameraSession == null) {
            return;
        }
        try {
            mCameraSession.stopRepeating();
        } catch (CameraAccessException | IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            mCameraSession.abortCaptures();
        } catch (CameraAccessException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出扫描方法
     */
    private void quit() {
        if (cameraThread != null && cameraThread.isAlive()) {
            cameraThread.quitSafely();
        }
        if (cameraHandler != null) {
            cameraHandler.removeCallbacksAndMessages(null);
        }
        if (mCameraSession != null) {
            mCameraSession.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        quit();
    }
}
