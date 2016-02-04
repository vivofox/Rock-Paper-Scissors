package com.example.rockpapersizer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GamesActivity extends ActionBarActivity implements OnClickListener {

	String userNameOnlineHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		Intent callIntend = getIntent();
		
		userNameOnlineHost = callIntend.getStringExtra("userNameOnlineHost");
		
		Button btnHostGames = (Button) findViewById(R.id.btnHostGames);
		Button btnGuestGames = (Button) findViewById(R.id.btnGuestGames);

		
		GamesFrag fragment = GamesFrag.newInstance(userNameOnlineHost);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.FrameLayoutContinerGames, fragment, "GamesFrag");
		ft.commit();
		
		
		btnHostGames.setOnClickListener(this);
		btnGuestGames.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.games, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		switch (v.getId()) {
		case R.id.btnGuestGames:
			
			GamesFrag fragment = GamesFrag.newInstance(userNameOnlineHost);
			
			ft.setCustomAnimations(
					R.anim.anim_slide_in_right,R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			
			ft.replace(R.id.FrameLayoutContinerGames, fragment, "GamesFrag");
			
			break;
			
		case R.id.btnHostGames:
			
			GamesFrag2 fragment2 = GamesFrag2.newInstance(userNameOnlineHost);
			
			ft.setCustomAnimations(
					R.anim.anim_slide_in_left,R.anim.anim_slide_out_left,
					R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
			
			ft.replace(R.id.FrameLayoutContinerGames, fragment2, "GamesFrag2");
			
			break;

		}
		
		ft.commit();
		
	}
}
