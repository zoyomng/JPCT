package com.zoyo.jpctae;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zoyo.core.CustomGLSurfaceView;

public class MainActivity extends AppCompatActivity {


    private CustomGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = (CustomGLSurfaceView) findViewById(R.id.gl_surfaceView);

        //传的文件在本地必须存在不然会加载失败
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
