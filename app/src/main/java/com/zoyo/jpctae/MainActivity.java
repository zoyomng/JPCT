package com.zoyo.jpctae;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.zoyo.core.CustomGLSurfaceView;

public class MainActivity extends AppCompatActivity {


    private CustomGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = (CustomGLSurfaceView) findViewById(R.id.gl_surfaceView);

        mGLSurfaceView.addObject(MainActivity.this, "watertruck.obj", "watertruck.mtl");

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
