package com.example.rockpapersizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayerFrag extends Fragment implements OnClickListener{
	
	private static final String TAG = "PlayerFrag";
	
	public interface onUserClick{
		void userChoice(int btnChoise);
	}
	public onUserClick listener;
	
	public static PlayerFrag newInstance(){
		
		return new PlayerFrag();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (onUserClick) activity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "please implment onUserClick");
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.player_frag, container , false);
		
		Button btnRock = (Button) view.findViewById(R.id.buttonRock);
		Button btnPaper = (Button) view.findViewById(R.id.buttonPaper);
		Button btnScissors = (Button) view.findViewById(R.id.buttonScissors);
		
		btnRock.setOnClickListener(this);
		btnPaper.setOnClickListener(this);
		btnScissors.setOnClickListener(this);
		return view;
	}
	@Override
	public void onClick(View v) {
		// 
		listener.userChoice(v.getId());
		
	}

}
