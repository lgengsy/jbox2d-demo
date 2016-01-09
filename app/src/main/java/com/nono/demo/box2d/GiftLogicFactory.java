package com.nono.demo.box2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by alex_xq on 14-10-10.
 */
public class GiftLogicFactory {

	private static GiftLogicFactory instance = null;
	private Object lockStar = new Object();
	private long lastSendStar = System.currentTimeMillis();
	private int STAR_FPS = 100;
	private WeakReference<Gift2DView> gift2dview = null;
	public Queue<StarListItemInfo> starQueue = new LinkedList<StarListItemInfo>();
	public StarThread starThread = null;

	class StarListItemInfo {
		StarListItemInfo(boolean isSelf) {
			this.isSelf = isSelf;
		}

		boolean isSelf = false;
	}

	public static GiftLogicFactory getInstance() {
		if (instance == null) {
			synchronized (GiftLogicFactory.class) {
				if (instance == null) {
					instance = new GiftLogicFactory();
				}
			}
		}
		return instance;
	}

	public GiftLogicFactory setGift2dview(Gift2DView gift2dview) {
		this.gift2dview = new WeakReference<Gift2DView>(gift2dview);
		return this;
	}

	/*送礼暂时不暂时碰撞，需求没有确定
	public void showGift(final GiftMessage giftmsg) {

		int count = Integer.valueOf(giftmsg.getBodyValueByKey(StarMessage.BODY_GIFT_NUM));
		final int showcount = calculateCount(count);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < showcount; i++) {
					try {

						String path = FileUtils.getInstance().getGiftsDirPath() + File.separator + Gifts.GIFT_BASE + giftmsg.getBodyValueByKey(GiftMessage.BODY_GIFT_ID);
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(path);
							Bitmap bitmap = zoomBitmap(BitmapFactory.decodeStream(fis), Utils.DpToPx(25), Utils.DpToPx(25));
							gift2dview.AddGift(bitmap);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}*/

	public void showStarGift(final boolean isSelf) {

		final int count = 1;
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lockStar) {
					for (int i = 0; i < count; i++)
						starQueue.add(new StarListItemInfo(isSelf));
				}
			}
		}).start();
	}

	public void startStarLogic() {
		starThread = new StarThread();
		starThread.start();
	}

	public void stopStarLogic() {
		if (starThread != null)
			starThread.flag = false;
	}

	private class StarThread extends Thread {
		public boolean flag = true;

		@Override
		public void run() {
			super.run();
			while (flag) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				synchronized (lockStar) {
					if (System.currentTimeMillis() - lastSendStar >= STAR_FPS) {
						if (!starQueue.isEmpty()) {
							StarListItemInfo starInfo = starQueue.poll();
							try {

								int drawableID = R.drawable.star;
								Bitmap bitmap = ImageUtils.drawableToBitmap(
                                        zoomDrawable(LibApplication.getInstance().getResources().getDrawable(drawableID), Utils.DpToPx(80), Utils.DpToPx(80))
                                );
								gift2dview.get().AddGift(bitmap);

							} catch (Exception e) {
								e.printStackTrace();
							}

							lastSendStar = System.currentTimeMillis();
						}
					}
				}
			}
		}
	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	private Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(null, newbmp);
	}

	private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	private int calculateCount(int count) {
		int rescount = count;
		return rescount;
	}
}
