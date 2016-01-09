package com.nono.demo.box2d;

import android.graphics.Bitmap;

import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * @author xueqing
 */
public class Gift2DBodyFactory {
	/**
	 * 创建多边形物体
	 *
	 * @param world 物理世界
	 * @param x     墙壁X轴起始位置
	 * @param y     墙壁Y轴起始位置
	 * @param w     墙壁宽度
	 * @param h     墙壁高度
	 * @param r     墙壁恢复力
	 * @param d     墙壁密度
	 */
	public static Body createPolygon(World world, float x, float y, float w, float h, float r, float d) {

		PolygonDef pd = new PolygonDef();
		pd.density = d;//密度
		pd.friction = 0.05f;//摩擦力
		pd.restitution = r;//恢复力
		pd.setAsBox(w / 2 / Gift2DConstant.RATE, h / 2 / Gift2DConstant.RATE);

		BodyDef bd = new BodyDef();
		bd.position.set((x + w / 2) / Gift2DConstant.RATE, (y + h / 2) / Gift2DConstant.RATE);

		Body body = world.createBody(bd);
		body.m_userData = new Gift2DRect(x, y, w, h);
		body.createShape(pd);
		body.setMassFromShapes();
		return body;

	}

	/**
	 * 创建礼物物体
	 *
	 * @param world 物理世界
	 * @param x     物体X轴起始位置
	 * @param y     物体Y轴起始位置
	 * @param w     物体宽度
	 * @param h     物体高度
	 * @param left  左抛进或右抛进
	 * @param bmp   物体图片
	 */
	public static Body createGift(World world, float x, float y, float w,
	                              float h, boolean left, Bitmap bmp) {
		PolygonDef def = new PolygonDef();
		def.density = 0.1f;//密度
		def.friction = 0.07f;//摩擦力
		def.restitution = 0.15f;//恢复力

		def.setAsBox(Utils.DpToPx(Gift2DConstant.GIFT_LENGTH + Gift2DConstant.GIFT_CRASH_REDUCEBORDER) / 2 / Gift2DConstant.RATE, Utils.DpToPx(Gift2DConstant.GIFT_LENGTH + Gift2DConstant.GIFT_CRASH_REDUCEBORDER) / 2 / Gift2DConstant.RATE);

		BodyDef bodyDef = new BodyDef();


		Vec2 vec2 = new Vec2();

//		float yPosRandom = (float)Math.random()*2.0f+8.0f;

		float thrownXRandom = (float) Math.random() * 4.0f + 13.0f;
		float thrownYRandom = (float) Math.random() * 5.0f + 5.0f;
		if (left) {
			vec2.set(thrownXRandom, thrownYRandom);
			bodyDef.position.set((x + Utils.DpToPx(Gift2DConstant.GIFT_LENGTH + Gift2DConstant.GIFT_CRASH_REDUCEBORDER) / 2) / Gift2DConstant.RATE, 0);

		} else {
			vec2.set(thrownXRandom, thrownYRandom);
			bodyDef.position.set((x - Utils.DpToPx(Gift2DConstant.GIFT_LENGTH + Gift2DConstant.GIFT_CRASH_REDUCEBORDER) / 2) / Gift2DConstant.RATE, 0);
		}

//		bodyDef.position.set((x + w / 2) / Constant.RATE, 0);

		Body body = world.createBody(bodyDef);
		if (body != null) {
			int angle = (int) (Math.random() * 90);
			body.m_userData = new Gift2DTile(
					x,
					y,
					angle,
					Utils.DpToPx(Gift2DConstant.GIFT_LENGTH - Gift2DConstant.GIFT_CRASH_REDUCEBORDER),
					Utils.DpToPx(Gift2DConstant.GIFT_LENGTH - Gift2DConstant.GIFT_CRASH_REDUCEBORDER),
					bmp);
			body.createShape(def);
			body.setLinearVelocity(vec2);
			body.setMassFromShapes();
		}
		return body;

	}


}
