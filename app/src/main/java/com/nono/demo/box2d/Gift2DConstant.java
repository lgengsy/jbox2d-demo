package com.nono.demo.box2d;

/**
 * 全局常量 管理类
 * @author Nono
 *
 */
public class Gift2DConstant {
	public static  final float stepTime = 1f / 10f;//时间步
	public static  final int iteraTions = 10;//模拟迭代次数。。一般这两个可以这个数值
	public static  final float RATE = 30f;//像素米的比例

	public static  final float MIN_SHOWTIME = 24;//最短展示时常(s)
	public static  float MAX_GIFTCOUNT = 40;//容纳最多礼物数量
	public static  final float OVERMAX_REMOVECOUNT = 3;//超出容纳后一次销毁的数量
	public static  final int GIFT_CRASH_REDUCEBORDER = 5;//物体碰撞边界缩小数值DP
	public static  final int GIFT_LENGTH = 20;//物体边长DP

}
