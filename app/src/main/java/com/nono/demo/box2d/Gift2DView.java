package com.nono.demo.box2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 礼物堆积效果视图
 *
 * @author xueqing
 */
public class Gift2DView extends SurfaceView implements SurfaceHolder.Callback {
    private static String TAG = "Gift2DView";

    private final int MSG_ANIM_DESTORY = 0x01;

    boolean isLeft = false;

    private SurfaceHolder surfaceHolder;
    private Paint paint;
    final static int FPS = 30;       //设置帧数

    private World world;

    private OnSurfaceViewCreated mSurfaceviewCB = null;

    private Body ground;
    private Body ceiling;
    private Body leftwall;
    private Body rightwall;

    MyThread myThread = null;  //ui线程

    private boolean mIsPortrait = true;

    private HashMap<Body, Gift2DTileInfo> mGift = new HashMap<Body, Gift2DTileInfo>();
    private ArrayList<Body> mGiftarray = new ArrayList<Body>();

    public static final Object lock1 = new Object();
    private SensorEventListener sel;
    private SensorManager sm;
    private Sensor sensor;

    private boolean isDestoryed = false;
    private boolean isCreated = false;

    public Gift2DView(Context context) {
        super(context);
//		根据CPU的最大频率，调整小星星特效的数量
        /*小星星个数控制业务逻辑
		* low 外层逻辑不展示小星星
		* mid   15
		* h     25
		* vh    35
		* xh    45
		* */
        Gift2DConstant.MAX_GIFTCOUNT = 60;

        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);  //添加回调

//		setZOrderOnTop(true);
        setZOrderMediaOverlay(true);

//      设置画布  背景透明
        getHolder().setFormat(PixelFormat.TRANSPARENT);

//		this.setKeepScreenOn(true);//保持屏幕常亮
        paint = new Paint();//实例化画笔
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        //创建一个物理世界
        final AABB aabb = new AABB();
        aabb.lowerBound = new Vec2(-100, -100);
        aabb.upperBound = new Vec2(100, 100);
        Vec2 gravity = new Vec2(0, 9.8f * 1.5f);
        world = new World(aabb, gravity, false);

        //获得重力感应硬件控制器
        sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //添加重力感应侦听，并实现其方法，
        sel = new SensorEventListener() {
            public void onSensorChanged(SensorEvent se) {
                float x = se.values[SensorManager.DATA_X];
                float y = se.values[SensorManager.DATA_Y];
                Vec2 vec2 = mIsPortrait ? new Vec2(-1.0f * x, y + 2.0f) : new Vec2(y, 1.0f * x + 2.0f);
                if (world != null)
                    world.setGravity(vec2);
            }

            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }
        };
        //注册Listener，SENSOR_DELAY_GAME为检测的精确度，
        sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public interface OnSurfaceViewCreated {
        public void OnCreated();
    }

    public void setOnSurfaceViewCreated(OnSurfaceViewCreated surfaceviewCB) {
        mSurfaceviewCB = surfaceviewCB;
    }

    /*public void setPortrait(boolean isPortrait) {
        //mIsPortrait = isPortrait;
    }*/

    @Override
    protected void onDetachedFromWindow() {

        isDestoryed = true;
        super.onDetachedFromWindow();
        sm.unregisterListener(sel);
        world = null;

    }

    public void AddGift(final Bitmap bmp) {

        isLeft = !isLeft;
        long start_time1 = System.currentTimeMillis();
        synchronized (lock1) {
            long start_time = System.currentTimeMillis();
            CreateGift(isLeft, bmp);
            long end_time = System.currentTimeMillis();
            long spend_time = end_time - start_time;
//			Log.i("Gift2D", "CreateGift:" + spend_time);

        }

        long end_time1 = System.currentTimeMillis();
        long spend_time1 = end_time1 - start_time1;
//		Log.i("Gift2D", "AddGift:" + spend_time1);
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				CreateGift(isLeft, bmp);
//			}
//		});

    }

    /**
     * 绘图
     */
    private void dodraw(Canvas canvas) {

//		非常重要：用背景擦除
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        try {
            //遍历绘制Body

            Body body = world.getBodyList();
            for (int i = 1; i < world.getBodyCount(); i++) {

                if (isDestoryed)
                    return;

                if ((body.m_userData) instanceof Gift2DTile) {
                    Gift2DTile title = (Gift2DTile) (body.m_userData);
                    title.drawStone(canvas, paint);
                }
                body = body.m_next;
            }
        } catch (Exception e) {

        }

    }

    /**
     * 逻辑
     */
    private void logic(Canvas canvas) {

//		Log.d(TAG,"logic begin");
        world.step(Gift2DConstant.stepTime, Gift2DConstant.iteraTions);
        ///遍历Body，进行Body与图形之间的传递数据
        Body body = world.getBodyList();

        for (int i = 1; i < world.getBodyCount(); i++) {
            //判定m_userData中的数据是否为MyRect实例
            if ((body.m_userData) instanceof Gift2DRect) {
                Gift2DRect rect = (Gift2DRect) (body.m_userData);
                rect.setX(body.getPosition().x * Gift2DConstant.RATE - rect.getW() / 2);
                rect.setY(body.getPosition().y * Gift2DConstant.RATE - rect.getH() / 2);
//				rect.setAngle((float) (body.getAngle() * 180 / Math.PI));
            } else if ((body.m_userData) instanceof Gift2DTile) {
                //判定m_userData中的数据是否为MyTile实例
                Gift2DTile tile = (Gift2DTile) (body.m_userData);
                tile.setX(body.getPosition().x * Gift2DConstant.RATE - tile.getW() / 2);
                tile.setY(body.getPosition().y * Gift2DConstant.RATE - tile.getH() / 2);
//				tile.setAngle((float) (body.getAngle() * 180 / Math.PI));
            }

            Gift2DTileInfo info = mGift.get(body);

//          移动到下一个对象
            body = body.m_next;
//			消失时间判断
            if (info != null) {
                if (info.mTime > Gift2DConstant.MIN_SHOWTIME * 2) {
                    if (mGift.containsKey(info.mBody))
                        DestoryGift(info.mBody);

                    //没有要展示的小星星了，退出运行线程
                    if (mGift.size() == 0) {
                        myThread.flag = false;
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    }

                } else {
                    info.mTime += Gift2DConstant.stepTime;
                }
            }

        }
//		Log.d(TAG,"logic end");
    }

    private void CreateGift(boolean left, Bitmap bmpStone) {

        if (!isCreated)
            return;

        if (mGift.size() == 0) {
            if (myThread != null) {
                close();
                myThread = null;
            }

            myThread = new MyThread();
            myThread.setName("Gift2DView logic thread");
            myThread.flag = true;
            myThread.start();
        }

        if (mGift.size() > Gift2DConstant.MAX_GIFTCOUNT) {
            int index = 0;

            for (; index < Gift2DConstant.OVERMAX_REMOVECOUNT; index++) {
                Body body = mGiftarray.get(index);
                if (mGift.containsKey(body))
                    DestoryGift(body);
            }

        }

        float xPos = 0.0f;
        if (!left)
            xPos = getWidth();
        try {
            Body gift = Gift2DBodyFactory.createGift(world, xPos, bmpStone.getHeight(), bmpStone.getWidth(), bmpStone.getHeight(),
                    left, bmpStone);
            mGift.put(gift, new Gift2DTileInfo(gift, 0.0f));
            mGiftarray.add(gift);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void DestoryGift(Body body) {

        AnimationDestory(body);

    }

    private void drawCanvas(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isCreated = true;
        Log.d(TAG, "surfaceCreated");
        Log.d(TAG, "getWidth:"+getWidth()+",getHeight:"+getHeight());
        //天花板
        ceiling = Gift2DBodyFactory.createPolygon(world, 0, -18, getWidth(), 2, 0.45f, 0);
        //地面
        ground = Gift2DBodyFactory.createPolygon(world, 0, getHeight(), getWidth(), 2, 0.45f, 0);
        //左墙壁
        leftwall = Gift2DBodyFactory.createPolygon(world, 0, 0, 2, getHeight() * 2, 0.4f, 0);
        //右墙壁
        rightwall = Gift2DBodyFactory.createPolygon(world, getWidth(), 0, 2, getHeight() * 2, 0.4f, 0);

        if (mSurfaceviewCB != null)
            mSurfaceviewCB.OnCreated();

        drawCanvas(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.d(TAG, "surfaceChanged");
        try {
            Log.d(TAG, "getWidth:"+getWidth()+",getHeight:"+getHeight());
            Log.d(TAG, "i2:"+i2+",i3:"+i3);
            //天花板
            Body body = Gift2DBodyFactory.createPolygon(world, 0, -18, getWidth(), 2, 0.2f, 0);
            world.destroyBody(ceiling);
            ceiling = body;
            //地面
            body = Gift2DBodyFactory.createPolygon(world, 0, getHeight(), getWidth(), 2, 0.45f, 0);
            world.destroyBody(ground);
            ground = body;
            //左墙壁
            body = Gift2DBodyFactory.createPolygon(world, 0, 0, 2, getHeight() * 2, 0.4f, 0);
            world.destroyBody(leftwall);
            leftwall = body;
            //右墙壁
            body = Gift2DBodyFactory.createPolygon(world, getWidth(), 0, 2, getHeight() * 2, 0.4f, 0);
            world.destroyBody(rightwall);
            rightwall = body;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (myThread != null) {
            myThread.flag = false;
            myThread.interrupt();
        }
        isCreated = false;
    }

    public void close() {
        if (myThread != null) {
            myThread.flag = false;
            try {
                myThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyThread extends Thread {
        public boolean flag;

        public void run() {
            long lastTime = SystemClock.uptimeMillis();
            Canvas canvas;
            while (flag) {

                long firstTime = SystemClock.uptimeMillis() - lastTime;
                if (firstTime > FPS) {

                    canvas = surfaceHolder.lockCanvas();
                    synchronized (lock1) {

                        try {
                            logic(canvas);
                        }
                        catch(Exception e){
                            try {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                            catch (Exception e1){}
                        }


                    }

                    if (isDestoryed) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }catch (Exception e){}
                        continue;
                    }

                    try {
                        dodraw(canvas);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e){}

                } else {
                    try {
                        Thread.sleep(Math.max(2, FPS - firstTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void AnimationDestory(final Body body) {

        mGift.remove(body);
        mGiftarray.remove(body);

        animateDispire(body);
    }


    public void animateDispire(final Body body) {
        world.destroyBody(body);
//		Message msg = new Message();
//		msg.what = MSG_ANIM_DESTORY;
//		msg.obj = body;
//		mHandle.sendMessage(msg);
    }



}
