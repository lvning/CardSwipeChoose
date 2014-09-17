package com.yiqivr.cardswipechoose;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.yiqivr.cardswipechoose.widget.CardLayout;
import com.yiqivr.cardswipechoose.widget.CardLayout.CardSwipeListener;
import com.yiqivr.cardswipechoose.widget.CircleProgress;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final CircleProgress cp = (CircleProgress) findViewById(R.id.progress);
		final CircleProgress cp1 = (CircleProgress) findViewById(R.id.progress1);
		CardLayout cl = (CardLayout) findViewById(R.id.cardlayout);
		cl.setCardSwipeListener(new CardSwipeListener() {

			@Override
			public void unlike(int percent) {
				cp1.setCurProgress(percent);

			}

			@Override
			public void like(int percent) {
				cp.setCurProgress(percent);

			}

			@Override
			public void cancel() {
				cp.setCurProgress(0);
				cp1.setCurProgress(0);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
