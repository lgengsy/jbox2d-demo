package com.nono.demo.box2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 
 * @author Nono
 *
 */
public class Gift2DTile {
	//矩形图形的位置、宽高与角度
	private float x, y, w, h, angle;
	private Bitmap bitmap;
	public int giftalpha = 255;
	public float giftscale = 1.0f;
	
	//矩形图形的初始化
	public Gift2DTile(float x, float y, int angle, float w, float h, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.bitmap = bitmap;
		this.angle = angle;
	}

	public Bitmap getBitmap(){return bitmap;}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public int getGiftalpha() {
		return giftalpha;
	}

	public void setGiftalpha(int giftalpha) {
		this.giftalpha = giftalpha;
	}

	public float getGiftscale() {
		return giftscale;
	}

	public void setGiftscale(float giftscale) {
		this.giftscale = giftscale;
	}

	public void drawStone(Canvas canvas, Paint paint) {
		canvas.save();
		//绕物体中心选折一定角度
		canvas.rotate(angle, x+w/2, y+h/2);
		int length = Utils.DpToPx(20);
		int left = (int)x+(int)((float)length * (1f-giftscale)/2f);
		int top = (int)y+(int)((float)length * (1f-giftscale)/2f);
		int right = (int)x+(int)((float)length * giftscale);
		int bottom = (int)y+(int)((float)length * giftscale);
		paint.setAlpha(giftalpha);
		canvas.drawBitmap(bitmap, null, new RectF(left, top, right, bottom), paint);
		canvas.restore();
	}
}
