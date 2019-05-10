package com.zoyo.core;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/*
* 自定义GLSURfaceView
* 在Activity调用onPause() , onResume()方法
*  @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
* 调用方式:
* myRenderer.addObject(MainActivity.this, "watertruck.obj", "watertruck.mtl");
*
*/
public class CustomGLSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {


    private float scale = 0.5f;
    private float minScale = 0.5f;
    private float xpos;
    private float ypos;
    private float currentDistance;
    private float lastDistance = -1;
    private long lastMultiTouchTime;
    private float preMoveX;
    private float preMoveY;
    private MyRenderer myRenderer;
    private Context context;
    private ScaleGestureDetector scaleGestureDetector;
    private TouchListener mTouchListener;


    public CustomGLSurfaceView(Context context) {
        this(context, null);
    }

    public CustomGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        //使用此方法创建与OpenGL es2.0兼容的上下文,必须在setRenderer()之前
//        setEGLContextClientVersion(2); //设置版本后报错,具体原因未知
        //设置最顶端
        setZOrderOnTop(true);
        //必须在setRenderer()之前
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //设置像素格式
        getHolder().setFormat(PixelFormat.RGBA_8888);
        myRenderer = new MyRenderer();
        setRenderer(myRenderer);

        scaleGestureDetector = new ScaleGestureDetector(context.getApplicationContext(), this);
    }


    /*
     * 1.点击迅速抬起: onDown-onSingleTapUp-onSingleTapConfirmed
     * 2.短按抬起: onDown-onShowPress-onSingleTapUp-onSingleTapConfirmed
     * 3.长按抬起: onDown-onShowPress-onLongPress
     * 4.单指滑动: onDown-onShowPress-onScroll-onFling
     * 5.单指长按后滑动: onDown-onShowPress-onLongPress(长按抬起)-无后续方法调用
     * 6.双指滑动: onDown-onShowPress-onScroll-onFling(单指滑动)-无后续方法调用
     * 7.双指按下后滑动: onDown-onScroll
     * */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xpos = event.getX();
                ypos = event.getY();
                int reproject = myRenderer.reproject((int) xpos, (int) ypos);
                if (mTouchListener != null)
                    mTouchListener.onTouchObject3D(reproject);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
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
//
//                    currentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
//
//                    //如果两指之间的距离没变则实现位移,有变化则实现缩放
//                    float distance = currentDistance - lastDistance;
////                    if (Math.abs(distance) > 40) {
//                    if (lastDistance < 0) {
//                        lastDistance = currentDistance;
//                    } else {
//                        scale += (distance) / 1000;
//                        myRenderer.setScale(scale);
//                        System.out.println("==========================" + scale);
//
//                        lastDistance = currentDistance;  //放大
//                    }
//                    } else {
//                    if (preMoveX == -1) {
//                        preMoveX = x1;
//                    }
//                    if (preMoveY == -1) {
//                        preMoveY = y1;
//                    }
//                    myRenderer.setTranslation((x1 - preMoveX) * 10, (y1 - preMoveY) * 10, 0);
//                    System.out.println((x1 - preMoveX) * 10 + "==========================" + (y1 - preMoveY) * 10);
//
//                    preMoveX = x1;
//                    preMoveY = y1;
//                    }


                    if (!scaleGestureDetector.isInProgress()) {

                        if (preMoveX == -1) {
                            preMoveX = x1;
                        }
                        if (preMoveY == -1) {
                            preMoveY = y1;
                        }
                        myRenderer.setTranslation((x1 - preMoveX), (y1 - preMoveY), 0);

                        preMoveX = x1;
                        preMoveY = y1;
                        System.out.println("============位移=====================");
                    }

                } else {
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
                        System.out.println("============旋转=====================");

                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                lastDistance = -1;
                ypos = -1;
                xpos = -1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                lastMultiTouchTime = System.currentTimeMillis();
                preMoveX = -1;
                preMoveY = -1;
                break;
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void addObject(Context context, String objName, String mtlName) {
        myRenderer.addObject(context, objName, mtlName);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float div = detector.getCurrentSpan() - detector.getPreviousSpan();
        div /= 300;
        scale += div;
//        if (scale < minScale) {
//            Toast.makeText(context, "已达到最小比例值", Toast.LENGTH_SHORT).show();
//        } else {
//            myRenderer.setScale(scale);
//        }

        myRenderer.setScale(scale);

        System.out.println("============缩放=====================");
        lastMultiTouchTime = System.currentTimeMillis();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /*设置最小缩放比例*/
    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }
    public interface TouchListener{
        void onTouchObject3D(int id);
    }

    public void setTouchLisenter(TouchListener listener){
        mTouchListener = listener;
    }
}
