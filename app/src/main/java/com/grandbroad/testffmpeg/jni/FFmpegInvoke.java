package com.grandbroad.testffmpeg.jni;

/**
 * Created by DELL on 2018-11-05.
 */

public class FFmpegInvoke {
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("postproc");
        System.loadLibrary("ffmpeg-invoke");
//        System.loadLibrary("x264");
    }

    private static native int run(int cmdLen, String[] cmd);
    public static native String test();

    public static int run(String[] cmd){
        return run(cmd.length,cmd);
    }

    public static native int initialize(int width, int height, int bit_rate, int fps,int max_rate,int min_rate,int code, String url);
    public static native int start(byte[] cameraData);
    public static native int stop();
    public static native int close();

}
