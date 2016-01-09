package com.nono.demo.box2d;

import android.app.Application;

/**
 * @author AleXQ
 * @Date 15/12/31

 */

public class LibApplication extends Application {

	private static LibApplication m_instance;

	public static  LibApplication getInstance(){
		return m_instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_instance = this;
	}
}
