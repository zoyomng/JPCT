package com.zoyo.core;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Camera;
import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer, CollisionListener {

    private RGBColor bgColor = new RGBColor(50, 50, 100);
    private final World world;
    private FrameBuffer frameBuffer;
    private Object3D mModel;
    private boolean hasObjectsCollision;

    public enum AXIS {
        X, Y, Z
    }

    public MyRenderer() {
        world = new World();
        world.setAmbientLight(25, 25, 25);
        Light light = new Light(world);
        light.enable();
        light.setIntensity(250, 250, 250);
        light.setPosition(new SimpleVector(0, 0, -15));

        Camera camera = world.getCamera();
        camera.setPosition(0, 0, 1);
        camera.moveCamera(Camera.CAMERA_MOVEOUT, 20);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = new FrameBuffer(gl, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        frameBuffer.clear(bgColor);
        world.renderScene(frameBuffer);
        world.draw(frameBuffer);
        frameBuffer.display();
    }

    /*
     * assets资源文件下的.obj文件和.mtl文件
     */
    public void addObject(Context context, String objName, String mtlName) {
        try {
            //加载obj文件,返回Object3D对象数组
            Object3D[] object3DS = Loader.loadOBJ(context.getResources().getAssets().open(objName), context.getResources().getAssets().open(mtlName), 0.05f);


//            //将返回的Object3D对象数组合并成一个Object3D
//            mModel = Object3D.mergeAll(object3DS);
//            mModel.setOrigin(new SimpleVector(0, 0, 50));
//            mModel.rotateZ(160.f);
//            mModel.setName(objName);
//
//            mModel.strip();
//            mModel.build();
//            mModel.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS | Object3D.COLLISION_CHECK_SELF);
//            mModel.setCollisionOptimization(true);
//            mModel.addCollisionListener(this);
//            world.addObject(mModel);

            //创建一个基础的Object3D对象
            mModel = new Object3D(0);

            for (int i = 0; i < object3DS.length; i++) {
                Object3D object3D = object3DS[i];
                mModel.addChild(object3D);
                world.addObject(object3D);
                object3D.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            }

            mModel.strip();
            mModel.build();
            mModel.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS | Object3D.COLLISION_CHECK_SELF);
            mModel.setCollisionOptimization(true);
            mModel.addCollisionListener(this);
            world.addObject(mModel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveCamera(SimpleVector direction, float speed) {
        world.getCamera().moveCamera(direction, speed);
    }

    @Override
    public void collision(CollisionEvent collisionEvent) {
//        mModel = collisionEvent.getObject();
    }

    @Override
    public boolean requiresPolygonIDs() {
        return false;
    }

    public int reproject(int xpos, int ypos) {
        SimpleVector ray = Interact2D.reproject2D3DWS(world.getCamera(), frameBuffer, xpos, ypos).normalize();
        Object[] objects = world.calcMinDistanceAndObject3D(world.getCamera().getPosition(), ray, 10000F);
        Object3D object = (Object3D) objects[1];
        if (object != null) {
            int id = object.getID();
            System.out.println("======================" + id);
            return id;
        }
        return -1;
    }

    /*
    旋转
    */
    public void setRotation(AXIS axis, float inc) {
        if (mModel != null) {
            if (axis == AXIS.X) {
                mModel.rotateX(inc);
            } else if (axis == AXIS.Y) {
                mModel.rotateY(inc);
            } else {
                mModel.rotateZ(inc);
            }
        }
    }

    /*
     * 位移
     */
    public void setTranslation(float offsetX, float offsetY, float incZ) {
        if (mModel != null) {
            SimpleVector objOrigin = mModel.getOrigin();
            float projectFix = Math.max((-3 * world.getCamera().getPosition().z / 2) - (objOrigin.z / 11), 1);
            float incX = offsetX / projectFix;
            float incY = offsetY / projectFix;
            float[] boundingBox = mModel.getMesh().getBoundingBox();
            mModel.checkForCollisionEllipsoid(new SimpleVector(incX, incY, incZ),
                    new SimpleVector((boundingBox[1] - boundingBox[0]) / 2, (boundingBox[3] - boundingBox[2]) / 2, (boundingBox[5] - boundingBox[4]) / 2), 5);
            mModel.translate(incX, incY, incZ);

            if (hasObjectsCollision) {
                mModel.setTransparency(Object3D.TRANSPARENCY_MODE_ADD);
                hasObjectsCollision = false;
            } else {
                mModel.setTransparency(-1);
            }
        }
    }

    /*
    放大缩小
    scale : 必须大于0.0f
    */
    public void setScale(float scale) {
        if (mModel != null && scale > 0)
            mModel.setScale(scale);
    }
}
