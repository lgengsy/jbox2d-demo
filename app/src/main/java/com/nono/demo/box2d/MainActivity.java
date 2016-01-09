package com.nono.demo.box2d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends Activity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */

	Button add;
	Button newActivity;
	Gift2DView gameview;

	Handler mHandler = new Handler();

	Bitmap bmpStone1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		LinearLayout container = (LinearLayout) findViewById(R.id.container);
		gameview = new Gift2DView(this);
		container.addView(gameview);

		add = (Button) findViewById(R.id.addbtn);
		newActivity = (Button) findViewById(R.id.newActivity);

		add.setOnClickListener(this);
		newActivity.setOnClickListener(this);

		bmpStone1 = BitmapFactory.decodeResource(getResources(), R.drawable.star);

		mHandler.postDelayed(r, 1000);
	}

	Runnable r = new Runnable() {
		@Override
		public void run() {
			mHandler.postDelayed(r, 100);
			gameview.AddGift(bmpStone1);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View view) {

		if (view == add) {
			gameview.AddGift(bmpStone1);
		} else if (view == newActivity) {
			Intent intent = new Intent();
			intent.setClass(this, testActivity.class);
			startActivity(intent);
		}
	}
}