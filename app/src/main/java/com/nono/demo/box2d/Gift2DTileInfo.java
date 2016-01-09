package com.nono.demo.box2d;

import org.jbox2d.dynamics.Body;

/**
 * Created by alex_xq on 14-9-30.
 */
public class Gift2DTileInfo {
	public Gift2DTileInfo(Body body, float time){
		mBody = body;
		mTime = time;
	}
	public Body mBody = null;
	public float mTime = 0;
}
