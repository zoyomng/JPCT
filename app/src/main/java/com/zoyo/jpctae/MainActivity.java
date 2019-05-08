package com.zoyo.jpctae;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private float scale = 0.5f;
    private float xpos;
    private float ypos;
    private MyRenderer myRenderer;
    private float currentDistance;
    private float lastDistance = -1;
    private long lastMultiTouchTime;
    private ScaleGestureDetector scaleGestureDetector;
    private float preMoveX;
    private float preMoveY;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceView);
        myRenderer = new MyRenderer();
        mGLSurfaceView.setRenderer(myRenderer);

        myRenderer.addObject(MainActivity.this, "watertruck.obj", "watertruck.mtl");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xpos = event.getX();
                ypos = event.getY();
                myRenderer.reproject((int) xpos, (int) ypos);
                System.out.println("=============ACTION_DOWN=================");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("=============ACTION_POINTER_DOWN=================");
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    //多点触摸放大缩小
                    //第一个拇指的坐标
                    float x1 = event.getX(0);
                    float y1 = event.getY(0);
                    //第二个拇指的坐标
                    float x2 = event.getX(1);
                    float y2 = event.getY(1);

                    float offsetX = x1 - x2;
                    float offsetY = y1 - y2;

                    currentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

                    //如果两指之间的距离没变则实现位移,有变化则实现缩放
                    float distance = currentDistance - lastDistance;
//                    if (Math.abs(distance) > 40) {
                        if (lastDistance < 0) {
                            lastDistance = currentDistance;
                        } else {
                            scale += (distance) / 1000;
                            myRenderer.setScale(scale);
                            lastDistance = currentDistance;  //放大
                        }
//                    } else {
//                        if (preMoveX == -1) {
//                            preMoveX = x1;
//                        }
//                        if (preMoveY == -1) {
//                            preMoveY = y1;
//                        }
//                        myRenderer.setTranslation((x1 - preMoveX) * 10, (y1 - preMoveY) * 10, 0);
//
//                        preMoveX = x1;
//                        preMoveY = y1;
//                    }
                } else {
                    //多点中的其中一点抬起后会造成突然的单点操作
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastMultiTouchTime > 300) {
                        //单点触摸旋转
                        float dx = event.getX() - xpos;  //左滑为负,右滑为正
                        float dy = event.getY() - ypos;  //上滑为负,下滑为正

                        //上下滑绕X轴旋转
                        if (Math.abs(dy) > 5)
                            myRenderer.setRotation(MyRenderer.AXIS.X, -dy / 100);
                        //左右滑绕Y轴旋转
                        if (Math.abs(dx) > 5)
                            myRenderer.setRotation(MyRenderer.AXIS.Y, -dx / 100);

                        xpos = event.getX();
                        ypos = event.getY();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                lastDistance = -1;
                ypos = -1;
                xpos = -1;
                System.out.println("=====ACTION_UP=======");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                lastMultiTouchTime = System.currentTimeMillis();
                preMoveX = -1;
                preMoveY = -1;
                System.out.println("=============ACTION_POINTER_UP=================");
                break;
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}
