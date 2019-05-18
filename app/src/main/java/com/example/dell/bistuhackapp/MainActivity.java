package com.example.dell.bistuhackapp;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private List<Integer> mWaitAction = new LinkedList<>();
    private boolean isTaking = false;   //是否处于拍照中
    boolean issend=false;
    EditText filename11;
    String jjgg;
    int i=0;

    public void takePicture() {   //对外暴露的方法，连续拍照时调用
        if (isTaking) {   //判断是否处于拍照，如果正在拍照，则将请求放入缓存队列
            mWaitAction.add(1);
        } else {
            doTakeAction();
        }}


    private void doTakeAction() {   //拍照方法
        isTaking = true;
        mCamera.takePicture(null, null, mPicture);
    }
    Camera.PictureCallback mPicture = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

                new webup(data, filename11.getText().toString() + "\\" + i + ".jpg", "message-train.jsp").start();
                i++;
                mCamera.startPreview();

        }
    };
    Camera.PictureCallback mPicture1 = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            new webup(data,"3.jpg","message-predict.jsp").start();

            mCamera.startPreview();
        }
    };
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            doTakeAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        Button selectbutton=(Button)findViewById(R.id.button_select);
        filename11=(EditText)findViewById(R.id.edit);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera

                                mCamera.takePicture(null,null,mPicture);


                    }
                }
        );
        selectbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture1);
                    }
                }
        );
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("bislog", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("bislog", "Error starting camera preview: " + e.getMessage());
            }
        }
    }
    public class webdown extends Thread{
        public webdown(){}
        public void run()
        {
            try {
                // 统一资源
                URL url = new URL("http://192.168.137.1:8080/data.txt");
                // 连接类的父类，抽象类
                URLConnection urlConnection = url.openConnection();
                // http的连接类
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                // 设定请求的方法，默认是GET
                httpURLConnection.setRequestMethod("GET");
                // 设置字符编码
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。

                // 文件大小
                int fileLength = httpURLConnection.getContentLength();
                Log.i("bislog","filelength:"+fileLength);
                // 文件名
                String filePathUrl = httpURLConnection.getURL().getFile();
                Log.i("bislog","filename"+filePathUrl);

                Log.i("bislog","filename"+"connect success");
                Log.i("bislog","code is " + httpURLConnection.getResponseCode());

                BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
                Log.i("bislog","filename:"+"inputstream success");

                int size = 0;
                int len = 0;
                byte[] buf = new byte[1024];
                while ((size = bin.read(buf)) != -1) {
                    len += size;
                    Log.i("bislog",buf.toString());
                    // 打印下载百分比
                    // System.out.println("下载了-------> " + len * 100 / fileLength +
                    // "%\n");
                }
                bin.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                Log.i("bislog",e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("bislog",e.toString());
            } finally {
            }
        }
    }

    public class webup extends Thread {
        HttpURLConnection connection = null;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        DataOutputStream ds = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        byte[] data;
        String filename;
        String jspname;
        public webup(byte[] data,String filename,String jspname) {
            this.data=data;
            this.filename=filename;
            this.jspname=jspname;
        }

        public void run() {

            try {

                    // 上传文件测试
                    String Boundary = UUID.randomUUID().toString(); // 文件边界

                    // 1.开启Http连接
                    HttpURLConnection conn = (HttpURLConnection) new URL("http://192.168.137.1:8080/" + jspname).openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setDoOutput(true); // 允许输出

                    // 2.Http请求行/头
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Charset", "utf-8");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + Boundary);

                    // 3.Http请求体
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeUTF("--" + Boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
                            + "Content-Type: image/jpg; charset=utf-8" + "\r\n");
                    //InputStream in = new FileInputStream("d:/3.jpg");
                    byte[] b = new byte[1024];
                    int l = 0;
                    out.write(data, 0, data.length); // 写入文件
                    out.writeUTF("\r\n--" + Boundary + "--\r\n");
                    out.flush();
                    out.close();
                    //in.close();

                    // 4.Http响应
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line = null;
                    jjgg="";
                    while ((line = bf.readLine()) != null) {
                        jjgg+=line;
                    }
                filename11.setText(jjgg);
                } catch(Exception e){
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally{

                }


        }
    }
}

