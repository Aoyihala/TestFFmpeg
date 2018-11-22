package com.grandbroad.testffmpeg;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.hardware.camera2.*;
import android.widget.Toast;

import com.grandbroad.testffmpeg.jni.FFmpegInvoke;

import java.nio.ByteBuffer;
import java.util.Arrays;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    100);
//        }
//
//        TextView tvMessage = findViewById(R.id.tv_message);
//        tvMessage.setText(FFmpegInvoke.test());
//
////        ffmpegTest();
//        testPushStream();
////        findDevices();
//    }
//
//
//    private void ffmpegTest() {
//        new Thread(){
//            @Override
//            public void run() {
//                long startTime = System.currentTimeMillis();
//                String input = Environment.getExternalStorageDirectory()+"/Movies/1532934813895.mp4";
//                String output = Environment.getExternalStorageDirectory()+"/Movies/output.mp4";
//                //剪切视频从00：20-00：28的片段
//                String cmd = "ffmpeg -d -ss 00:00:20 -t 00:00:02 -i %s -vcodec copy -acodec copy %s";
//                Log.v("important",input);
//                cmd = String.format(cmd,input,output);
//                FFmpegInvoke.run(cmd.split(" "));
//                Log.d("FFmpegTest", "run: 耗时："+(System.currentTimeMillis()-startTime));
//            }
//        }.start();
//
//
//    }
//
//    private void testPushStream(){
//        new Thread(){
//            @Override
//            public void run() {
//                //ffmpeg -re -stream_loop -1 -i 2minTransformers6281_trailer1080p.flv -vcodec copy -f flv rtmp://192.168.1.26:1935/live/PFM_test_HD_test1
//                String in = Environment.getExternalStorageDirectory()+"/Movies/1532934813895.mp4";
//                String out = "rtmp://live.gaobohuike.com/hls/8766";
//                String cmd2 = "ffmpeg -re -stream_loop -1 -i %s -vcodec libx264 copy -f flv %s";
//
//                cmd2 = String.format(cmd2,in,out);
//                FFmpegInvoke.run(cmd2.split(" "));
//            }
//        }.start();
//    }
//
//    private void findDevices(){
//        //ffmpeg -list_devices true -f dshow -i dummy
//        new Thread()
//        {
//            @Override
//            public void run() {
//                String cmd3 = "ffmpeg -list_devices true -f dshow -i dummy";
//                FFmpegInvoke.run(cmd3.split(" "));
//            }
//        }.start();
//    }
//
//
//}
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button mTakeButton;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private CameraDevice.StateCallback cameraDevice_back;
    private SurfaceHolder mSurfaceHolder;
    private boolean isRecording = false;
    //摄像头id
    private String after_id = Integer.toString(CameraCharacteristics.LENS_FACING_FRONT);
    private CameraManager c_manager;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    //相机详细信息
    //获取帧数
    private ImageReader imageReader;
    private Handler read_hander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });
    private Handler cmera_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.surfaceView1);
        mTakeButton = findViewById(R.id.take_button);
        c_manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mSurfaceHolder = mSurfaceView.getHolder();
        //进入即推流
       // FFmpegInvoke.initialize(1080,1920,1024*1024, 60, "rtmp://live.gaobohuike.com/hls/433444");
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCaptureSession!=null)
                {
                    if (isRecording) {
                        mTakeButton.setText("Start");
                        mCamera.setPreviewCallback(null);
                        Toast.makeText(MainActivity.this, "encode done", Toast.LENGTH_SHORT).show();
                        isRecording = false;
                    }else {
                        mTakeButton.setText("Stop");

//                        FFmpegInvoke.initialize(mCamera.getParameters().getPreviewSize().width,mCamera.getParameters().getPreviewSize().height,1024*1024,"rtmp://live.gaobohuike.com/hls/433444");
                        isRecording = true;
                    }
                }
            }
        });
        //前置设置监听
        cameraDevice_back = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                takePrivew(camera);
            }

            @Override
            public void onDisconnected(CameraDevice camera) {

            }

            @Override
            public void onError(CameraDevice camera, int error) {

            }
        };
        imageReader = ImageReader.newInstance(1080,1090,ImageFormat.YUV_420_888,2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                //获取最近的一帧
                Image image = reader.acquireLatestImage();
                if (image!=null)
                {
                    int len = image.getPlanes().length;
                    byte[][] bytes = new byte[len][];
                    int count = 0;
                    for (int i = 0; i < len; i++) {
                        ByteBuffer buffer = image.getPlanes()[i].getBuffer();
                        int remaining = buffer.remaining();
                        byte[] data = new byte[remaining];
                        byte[] _data = new byte[remaining];
                        buffer.get(data);
                        //复制
                        System.arraycopy(data, 0, _data, 0, remaining);
                        bytes[i] = _data;
                        //传入
                        FFmpegInvoke.start(bytes[i]);
                        count += remaining;
                    }

                    Log.e("帧数获取","eee");
                    //一定要关
                    image.close();

                }

            }
        },read_hander);


        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //开始
                openpriview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }


    /**
     * 展示预览
     *
     * @param camera
     */
    private void takePrivew(final CameraDevice camera) {
    try
    {
        mPreviewRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        //设置了一个具有输出Surface的CaptureRequest.Builder。
        mPreviewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
        //添加至ImageReader
        mPreviewRequestBuilder.addTarget(imageReader.getSurface());
        //创建一个CameraCaptureSession来进行相机预览。
        camera.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(),imageReader.getSurface()),
                new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        // 相机已经关闭
                        if (null == camera) {
                            return;
                        }
                        // 会话准备好后，我们开始显示预览
                        mCaptureSession = cameraCaptureSession;
                        try {
                            // 自动对焦应
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            // 闪光灯
                            //setAutoFlash(mPreviewRequestBuilder);
                            // 开启相机预览并添加事件
                            mPreviewRequest = mPreviewRequestBuilder.build();
                            //发送请求
                            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                    null, null);

                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(
                            @NonNull CameraCaptureSession cameraCaptureSession) {
                        Log.e(TAG, " onConfigureFailed 开启预览失败");
                    }
                }, cmera_handler);
    } catch(CameraAccessException e)
    {
        Log.e(TAG, " CameraAccessException 开启预览失败");
        e.printStackTrace();
    }
}



    /**
     * 开启相机
     * @param holder
     */

    @SuppressLint("MissingPermission")
    private void openpriview(SurfaceHolder holder) {
        try {
            c_manager.openCamera(after_id,cameraDevice_back,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(9)
    @Override
    protected void onResume(){
        super.onResume();

    }


    @Override
    protected void onPause(){
        super.onPause();
        FFmpegInvoke.stop();
        FFmpegInvoke.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FFmpegInvoke.stop();
        FFmpegInvoke.close();
        if (mCaptureSession!=null)
        {
            mCaptureSession = null;
        }

    }

}

