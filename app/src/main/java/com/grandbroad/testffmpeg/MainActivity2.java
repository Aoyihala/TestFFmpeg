package com.grandbroad.testffmpeg;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grandbroad.testffmpeg.jni.FFmpegInvoke;
import com.grandbroad.testffmpeg.utils.SpUtils;
import com.grandbroad.testffmpeg.wedgit.ResizeAbleSurfaceView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

/**
 * 1080p：1920*1080
 * 720p：1280*720
 * 480p：640*480
 * 360p: 480*360
 */
public class MainActivity2 extends AppCompatActivity {

    private static final String TAG= "MainActivity";
    private Button mTakeButton;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean isRecording = false;
    private MyOrientoinListener myOrientoinListener;

    //预定参数
    private int fps;
    private int speed;
    private int qualty;
    private String url;
    private String screen_size;
    private boolean first=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //获取配置
        initConfig();
        openCamera();
        myOrientoinListener = new MyOrientoinListener(this);
        boolean autoRotateOn = (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
        //检查系统是否开启自动旋转，无需检查
        //if (autoRotateOn) {
            myOrientoinListener.enable();
       // }
        final Camera.PreviewCallback mPreviewCallbacx=new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] arg0, Camera arg1) {
                /*
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:SS",Locale.SIMPLIFIED_CHINESE);
                Log.e(simpleDateFormat.format(SystemClock.currentThreadTimeMillis()),arg0+"");
                */
                //arg0 = NV21_rotate_to_90(arg0,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height);
                FFmpegInvoke.start(arg0);
            }
        };

        mTakeButton=(Button)findViewById(R.id.take_button);

        PackageManager pm=this.getPackageManager();
        boolean hasCamera=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD;
        if(!hasCamera)
            mTakeButton.setEnabled(false);
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(mCamera!=null)
                {
                    if (isRecording) {
                        FFmpegInvoke.stop();
                        FFmpegInvoke.close();
                        mTakeButton.setText("Start");
                        mCamera.setPreviewCallback(null);
                        Toast.makeText(MainActivity2.this, "encode done", Toast.LENGTH_SHORT).show();
                        isRecording = false;
                    }else {
                        mTakeButton.setText("Stop");
                        //1.
                        FFmpegInvoke.initialize(
                                mCamera.getParameters().getPreviewSize().width,
                                mCamera.getParameters().getPreviewSize().height,
                                speed*1024,fps,speed*1024,(speed*1024)/10,qualty,url);
//                        FFmpegInvoke.initialize(mCamera.getParameters().getPreviewSize().width,mCamera.getParameters().getPreviewSize().height,1024*1024,"rtmp://live.gaobohuike.com/hls/433444");
                        //2.
                        mCamera.setPreviewCallback(mPreviewCallbacx);
                        mCamera.cancelAutoFocus();
                        isRecording = true;
                    }
                }
            }
        });
        //surfaceholder的展示类型
        mSurfaceView=findViewById(R.id.surfaceView1);
        SurfaceHolder holder=mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
                // TODO Auto-generated method stub
                if(mCamera!=null)
                {
                    mCamera.stopPreview();
                    mSurfaceView = null;
                    mSurfaceHolder = null;
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                // TODO Auto-generated method stub
                try{
                    if(mCamera!=null){
                        mCamera.setPreviewDisplay(arg0);
                        mSurfaceHolder=arg0;
                    }
                }catch(IOException exception){
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                Log.e("view状态","变形");
                if(mCamera==null) return;

                try{
                    mCamera.startPreview();
                    mSurfaceHolder=arg0;
                }catch(Exception e){
                    Log.e(TAG, "could not start preview", e);
                    mCamera.release();
                    mCamera=null;
                }
            }
        });

    }

    /**
     * 获取设置
     */
    private void initConfig() {
        fps = SpUtils.getFps(getApplicationContext());
        speed = SpUtils.getSpeed(getApplicationContext());
        qualty = SpUtils.getQultey(getApplicationContext());
        url = SpUtils.getUrl(getApplicationContext());
        screen_size = SpUtils.getScreenSize(getApplicationContext());
    }

    /**
     * 处理分辨率
     * @return
     */
    private Map<String,String> parseScreenSoize()
    {
        Map<String,String> handw = new HashMap<>();
        if (screen_size.equals("")||TextUtils.isEmpty(screen_size))
        {
            return null;
        }
        handw.put("height",screen_size.split("-")[1]);
        handw.put("wight",screen_size.split("-")[0]);
        return handw;
    }

    /**
     * 打开摄像头
     */
    public void openCamera()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD)
        {
            mCamera=Camera.open(0);
            //获取支持的
            List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
            for (Camera.Size size:sizeList)
            {
                Log.e("支持的分辨率",size.width+"*"+size.height);
            }

        }
        else
        {
            mCamera=Camera.open();
        }
        Camera.Parameters parameters=mCamera.getParameters();
        //parameters.setPreviewSize(640,480);
        //parameters.setPictureSize(640,480);
        Map<String,String> params = parseScreenSoize();
        if (params!=null)
        {
            int height = Integer.parseInt(params.get("height"));
            int wight = Integer.parseInt(params.get("wight"));
            parameters.setPreviewSize(wight,height);
            parameters.setPictureSize(wight,height);
        }
        //自动聚焦模式 不设置则模糊
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);

    }

    @TargetApi(9)
    @Override
    protected void onResume(){
        super.onResume();
      if (first)
      {
          first=false;
      }
      else
      {
          openCamera();
      }
    }

    @Override
    protected void onPause(){
        super.onPause();
        FFmpegInvoke.stop();
        FFmpegInvoke.close();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
            FFmpegInvoke.stop();
            FFmpegInvoke.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FFmpegInvoke.stop();
        FFmpegInvoke.close();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
            FFmpegInvoke.stop();
            FFmpegInvoke.close();
        }
        //销毁时取消监听
        myOrientoinListener.disable();
    }


    /**
     * 编码时正常调用
     * 旋转为正常角度
     * @param nv21_data
     * @param width
     * @param height
     * @return
     */
    private byte[] NV21_rotate_to_90(byte[] nv21_data, int width, int height)
    {
        int y_size = width * height;
        int buffser_size = y_size * 3 / 2;
        byte[] nv21_rotated = new byte[buffser_size];
// Rotate the Y lum

        int i = 0;
        int startPos = (height - 1)*width;
        for (int x = 0; x < width; x++)
        {
            int offset = startPos;
            for (int y = height - 1; y >= 0; y--){
                nv21_rotated[i] = nv21_data[offset + x];
                i++;
                offset -= width;
            }
        }

// Rotate the U and V color components
        i = buffser_size - 1;
        for (int x = width - 1; x > 0; x = x - 2){
            int offset = y_size;
            for (int y = 0; y < height / 2; y++){
                nv21_rotated[i] = nv21_data[offset + x];
                i--;
                nv21_rotated[i] = nv21_data[offset + (x - 1)];
                i--;
                offset += width;
            }
        }
        return nv21_rotated;
    }


    /**
     *
     * 旋转代码，自动旋转
     *
     */
    class MyOrientoinListener extends OrientationEventListener {

        public MyOrientoinListener(Context context) {
            super(context);
        }

        public MyOrientoinListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Log.d(TAG, "orention" + orientation);
            int screenOrientation = getResources().getConfiguration().orientation;
            if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    Log.d(TAG, "设置竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else if (orientation > 225 && orientation < 315) { //设置横屏
                Log.d(TAG, "反向横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
            } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
                Log.d(TAG, "设置横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } else if (orientation > 135 && orientation < 225) {
                Log.d(TAG, "反向竖屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);

                }
            }
        }
    }

}

