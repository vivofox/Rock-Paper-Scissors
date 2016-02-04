package com.example.rockpapersizer;

import com.example.rockpapersizer.MainActivity.LogoutUser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeFrag extends Fragment implements OnClickListener{

	private String TAG = "WelcomeFrag";
	
	public interface onChoiceStart{
		void onChoiceSinglePlayer();
		void onChoiceMultyplayer();
		void onChoiceExit();
		void onChoiceGames();
		void onChoiceLogout();
		void onChoiceLogin();
		
		String isOnline();
	}
	
	public onChoiceStart listener;
	
	
	public static WelcomeFrag newInstance(){
		
		return new WelcomeFrag();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (onChoiceStart) activity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG , "please implment onChoiceStart");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 
		View view = inflater.inflate(R.layout.welcome_frag, container , false);
		
		Button btnSingle = (Button) view.findViewById(R.id.buttonSinglePlayer);
		Button btnMulty = (Button) view.findViewById(R.id.buttonMultyplayer);
		Button btnGames = (Button) view.findViewById(R.id.buttonGames);
		Button btnLogout = (Button) view.findViewById(R.id.buttonLogoutMain);
		Button btnLogin = (Button) view.findViewById(R.id.buttonLoginMain);
		Button btnExit = (Button) view.findViewById(R.id.buttonExit);
		
		if (listener.isOnline() != null) {
			// if the user is online so set the user name on a text view
			TextView textViewOnlineStatus = (TextView) view.findViewById(R.id.textViewOnlineStatus);
			textViewOnlineStatus.setText("online user : " + listener.isOnline());
		}
		
		btnSingle.setOnClickListener(this);
		btnMulty.setOnClickListener(this);
		btnGames.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		
		return view;
	}
	
	
	@Override
	public void onClick(View v) {
		// 
		
		switch (v.getId()) {
		
		case R.id.buttonSinglePlayer:
			listener.onChoiceSinglePlayer();
			break;

		case R.id.buttonMultyplayer:
			listener.onChoiceMultyplayer();
			break;
			
		case R.id.buttonGames:
			listener.onChoiceGames();
			break;
			
		case R.id.buttonLogoutMain:
			listener.onChoiceLogout();
			break;
			
		case R.id.buttonLoginMain:
			listener.onChoiceLogin();
			break;
			
		case R.id.buttonExit:
			listener.onChoiceExit();
			break;
			
			
		}
		
	}
}
