package com.example.rockpapersizer;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FightFrag extends Fragment{
	
	private static final String TAG = "FightFrag";
	
	public FightFrag() {
		// must have an empty constructor
		
	}
	
	public static FightFrag newInstance(int btnChoise){
		
		FightFrag fightFrag = new FightFrag();
		
		Bundle bundle = new Bundle();
		bundle.putInt("btnChoise", btnChoise);
		
		fightFrag.setArguments(bundle);
		
		return fightFrag;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
			
		View view = inflater.inflate(R.layout.fight_frag, container , false);
		
		Bundle args = getArguments();
		int btnChoise = args.getInt("btnChoise");
		
			ImageView imageViewUserHand = (ImageView) view.findViewById(R.id.imageViewPlayer1Choise);
			ImageView imageViewAppHand = (ImageView) view.findViewById(R.id.imageViewAppChoise);
			

			String userChoice = null;
			String appChoice = null;
			
			// user hand choice
			switch (btnChoise) {
			
			case R.id.buttonRock:
				imageViewUserHand.setImageResource(R.drawable.r);
				userChoice = "Rock";

				break;

			case R.id.buttonPaper:
				userChoice = "Paper";
				imageViewUserHand.setImageResource(R.drawable.p);

				break;
				
			case R.id.buttonScissors:
				userChoice = "Scissors";
				imageViewUserHand.setImageResource(R.drawable.s);

				break;
			}
			
			// application hand choice
			switch ( randomPick()) {
			case 1:
				appChoice = "Rock";
				imageViewAppHand.setImageResource(R.drawable.r);

				break;

			case 2:
				appChoice = "Paper";
				imageViewAppHand.setImageResource(R.drawable.p);
				
				break;

			case 3:
				appChoice = "Scissors";
				imageViewAppHand.setImageResource(R.drawable.s);

				break;
			}
			
			
			
			TextView textViewFinal = (TextView) view.findViewById(R.id.textViewGameFinal);
			
			if (!userChoice.equals(appChoice)) {
				if (userChoice.equals("Rock") && appChoice.equals("Scissors")) {
					
					textViewFinal.setText("you win!");
					
				}else if (userChoice.equals("Rock") && appChoice.equals("Paper")) {
				
					textViewFinal.setText("you lose...");
					
				}else if (userChoice.equals("Paper") && appChoice.equals("Rock")){
					
					textViewFinal.setText("you win!");
					
				}else if (userChoice.equals("Paper") && appChoice.equals("Scissors")){
					
					textViewFinal.setText("you lose...");
					
				}else if (userChoice.equals("Scissors") && appChoice.equals("Paper")){
					
					textViewFinal.setText("you win!");
					
				}else if (userChoice.equals("Scissors") && appChoice.equals("Rock")){
					
					textViewFinal.setText("you lose...");
					
				}
				
				
				
			}else{
				textViewFinal.setText("it's a tie!!");
			}
		
		
		
		return view;
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	
	public int randomPick(){
		
		int randomNum = randInt(1, 3); 
		
		return randomNum;
	}
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

}
