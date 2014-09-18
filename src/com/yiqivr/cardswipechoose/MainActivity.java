package com.yiqivr.cardswipechoose;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.yiqivr.cardswipechoose.widget.CardLayout;
import com.yiqivr.cardswipechoose.widget.CardLayout.CardSwipeListener;
import com.yiqivr.cardswipechoose.widget.CircleProgress;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CardLayout cl = (CardLayout) findViewById(R.id.cardlayout);
		cl.setCardSwipeListener(new CardSwipeListener() {
			
			@Override
			public void unlike() {
				Toast.makeText(MainActivity.this, "unlike", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void like() {
				Toast.makeText(MainActivity.this, "like", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void cancel() {
				Log.e("", "setCardSwipeListener cancel");
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
