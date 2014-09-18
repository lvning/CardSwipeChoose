CardSwipeChoose
===============
Swipe up or down to choose a card.

How to use:
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
