package com.example.rockpapersizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GamesFrag2 extends Fragment implements OnItemClickListener{
	
	
	private static final String TAG = "GamesFrag";
	private static final String API = "http://rockpapaer.herokuapp.com/login";
	
	GameAdapter adapter;
	
	List<GameOfRps> gamesList;
	
	GameOfRps GameOfRps = new GameOfRps();
	
	String userNameOnlineHost;
	
	int gustHandRestponse;
	
	int gameId;

	public static GamesFrag2 newInstance(String userName){
		
		GamesFrag2 frag = new GamesFrag2();
		
		Bundle args = new Bundle();
		args.putString("userName", userName);
		
		frag.setArguments(args);
		
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 
		Log.d(TAG, "onCreateView");
		
		View view = inflater.inflate(R.layout.games_frag, container, false);
		gamesList = new ArrayList<GameOfRps>();
		adapter = new GameAdapter();

		ListView listViewAllGames = (ListView) view.findViewById(R.id.listViewAllGames);
//		TextView textViewGamesList = (TextView) view.findViewById(R.id.textViewAllGames2);
//		textViewGamesList.setText("Games from me!!");

		listViewAllGames.setAdapter(adapter);	
		listViewAllGames.setOnItemClickListener(this);
		
		Bundle args = getArguments();
		
		userNameOnlineHost = args.getString("userName");
		
		new RefreshGamesList().execute(API);
		
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick");

//		GameOfRps game = gamesList.get(position);
//		gameId = game.getGameId();
		


	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.context, menu);
		
	}

	class RefreshGamesList extends AsyncTask<String, Void, String> {

		private static final String TAG = "RefreshGamesList";

		@Override
		protected void onPreExecute() {
			// no dialog in this action
		}

		@Override
		protected String doInBackground(String... params) {

			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();
			
			String reqAction = "printAllGames";

			try {
				String queryString = "";

				queryString += "?reqAction=" + URLEncoder.encode(reqAction, "utf-8");

				//show on logCat the entire address
				Log.d(TAG, params[0] + queryString);
				
				//saving the address in a URL variable
				URL url = new URL(params[0] + queryString);

				// open a connection
				connection = (HttpURLConnection) url.openConnection();

				// check the result status of the conection:
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// not good.
					return null;
				}

				input = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));

				// read from the buffered stream line by line:
				String line = "";
				while ((line = input.readLine()) != null) {
					// append to the string builder:
					response.append(line + "\n");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				// close the stream if it exists:
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// close the connectin if it exists:
				if (connection != null) {
					connection.disconnect();
				}
			}

			return response.toString();
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null || result.length() == 0) {
				// no result:

			} else {

				try {

					gamesList.clear();

					Log.d(TAG, result);
					JSONObject responseObject = new JSONObject(result);
					JSONArray resultsArray = responseObject.getJSONArray("games");

					// Iterate over the JSON array:
					for (int i = 0; i < resultsArray.length(); i++) {
						
						if (userNameOnlineHost.equals(resultsArray.getJSONObject(i).getString("hostUserName"))) {
							
							JSONObject userObject = resultsArray.getJSONObject(i);

							// get the primitive values in the object

							int gameId = Integer.parseInt(userObject.getString("gameId"));

							long gameTimeStamp = Long.parseLong(userObject.getString("gameTimeStamp"));

							String hostUserName = userObject.getString("hostUserName");
							String gustUserName = userObject.getString("gustUserName");

							int hostHand = Integer.parseInt(userObject.getString("hostHand"));

							int gustHand = Integer.parseInt(userObject.getString("gustHand"));

							String userNameWon = userObject.getString("userNameWon");
							String userNameLose = userObject.getString("userNameLose");
							String gameResult = userObject.getString("gameResult");

							GameOfRps game = new GameOfRps(
									gameId, 
									gameTimeStamp,
									hostUserName, 
									gustUserName, 
									hostHand);
							
							game.setGustHand(gustHand);
							game.setUserNameWon(userNameWon);
							game.setUserNameLose(userNameLose);
							game.setGameResult(gameResult);
							
							// add to the list:
							gamesList.add(game);
							
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					 e.printStackTrace();
				}

				// notify adapter:
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	// task for PUT request
	class PutTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// perform an HTTP PUT request
			return put(API , new String[]{"gameId" , gameId+"",  "gustHand", gustHandRestponse+""});
		}
		
		@Override
		protected void onPostExecute(String result) {
			// refresh screen 
			new RefreshGamesList().execute(API);
		}
		
		
	}
	
	// custom BaseAdapter adapter class
		class GameAdapter extends BaseAdapter{

			public int getCount() {
				return gamesList.size(); // the list's size
			}

			public Object getItem(int position) {
				
				//return null; //it's ok
				return null;
			}

			public long getItemId(int position) {
				return 0; //it's ok
			}

			
			public View getView(int position, View convertView, ViewGroup parent) {
				
				// create / recycle the view :
				View v = convertView;
				if (v==null){
					v=getActivity().getLayoutInflater().inflate(R.layout.game_list_from_web, parent, false);
				}
				
				//get the game (in the position) :
				GameOfRps game = gamesList.get(position);
				
				TextView textViewHostUserName = (TextView) v.findViewById(R.id.TextViewHostUserName);
				ImageView imageViewHostHand = (ImageView) v.findViewById(R.id.imageViewHostHand);
				TextView textViewGuestUserName = (TextView) v.findViewById(R.id.TextViewGuestUserName);
				ImageView imageViewGuestHand = (ImageView) v.findViewById(R.id.imageViewGuestHand);
				TextView textViewGameId = (TextView) v.findViewById(R.id.textViewGameId);
				TextView textViewGameResult = (TextView) v.findViewById(R.id.textViewGameResult);

				
				
				textViewGameId.setText("game id : " + game.getGameId());
				textViewHostUserName.setText("host : " + game.getHostUserName());
				textViewGuestUserName.setText("guest : " + game.getGustUserName());
				
				checkForHand(imageViewHostHand, game.getHostHand());
				checkForHand(imageViewGuestHand, game.getGustHand());
				
				
				if (game.getGameResult() == null) {
					
					textViewGameResult.setText("waiting...");
					
				}else if (game.getGameResult().equals("tie")) {
					
					textViewGameResult.setText("it's a tie!");
				
				}else {
					
					textViewGameResult.setText("the winner is : " + game.getUserNameWon());
				}
				
				
				
				return v;
			}
		}
		
		public void checkForHand(ImageView imageView , int userHand) {
			
			imageView.setVisibility(View.VISIBLE);
			
			switch (userHand) {
			case -1:
				
				imageView.setImageResource(R.drawable.r);
				break;
				
			case -2:

				imageView.setImageResource(R.drawable.p);
				break;
				
			case -3:

				imageView.setImageResource(R.drawable.s);
				break;

			default:
				imageView.setVisibility(View.GONE);
				break;
			}
		}
		
		public static String put(String address, String[]...params){
			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			try {
				// build query string:
				String queryString = "?";

				for (String[] pair : params) {
					String name = URLEncoder.encode(pair[0], "utf-8");
					String value = URLEncoder.encode(pair[1], "utf-8");
					String name2 = URLEncoder.encode(pair[2], "utf-8");
					String value2 = URLEncoder.encode(pair[3], "utf-8");
					queryString += name + "=" + value + "&";
					queryString += name2 + "=" + value2 + "&";
				}

				// remove the last "&" :
				queryString = queryString.substring(0, queryString.length() - 1);
				
				// prepare a URL object :
				Log.d("TAG", address + queryString);
				URL url = new URL( address + queryString);
				
				// open a connection
				connection = (HttpURLConnection) url.openConnection();
				
				// set the connection to HTTP PUT 
				connection.setRequestMethod("PUT");
				
				// check the result status of the conection:
				
				int status = connection.getResponseCode();
				if (status<200 || status>299) {
					// not good.
					return null;
				}

				// get the input stream from the connection
				input = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

				// read from the buffered stream line by line:
				String line = "";
				while ((line = input.readLine()) != null) {
					// append to the string builder:
					response.append(line + "\n");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {

				// close the stream if it exists:
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// close the connectin if it exists:
				if (connection != null) {
					connection.disconnect();
				}
			}

			// get the string from the string builder:
			return response.toString();
			
		}	
		
		
//		public static String delete(String address, String[]...params){
//			BufferedReader input = null;
//			HttpURLConnection connection = null;
//			StringBuilder response = new StringBuilder();
//
//			try {
//				// build query string:
//				String queryString = "?";
//
//				for (String[] pair : params) {
//					String name = URLEncoder.encode(pair[0], "utf-8");
//					String value = URLEncoder.encode(pair[1], "utf-8");
//					
//					
//					queryString += name + "=" + value + "&";
//				}
//
//				// remove the last "&" :
//				queryString = queryString.substring(0, queryString.length() - 1);
//				
//				Log.d("queryString" , queryString);
//				
//				// prepare a URL object :
//				URL url = new URL( address + queryString);
//				
//				// open a connection
//				connection = (HttpURLConnection) url.openConnection();
//				
//				// set the connection to HTTP DELETE 
//				connection.setRequestMethod("DELETE");
//				
//				// check the result status of the conection:
//				
//				int status = connection.getResponseCode();
//				if (status<200 || status>299) {
//					// not good.
//					return null;
//				}
//
//				// get the input stream from the connection
//				input = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
//
//				// read from the buffered stream line by line:
//				String line = "";
//				while ((line = input.readLine()) != null) {
//					// append to the string builder:
//					response.append(line + "\n");
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//				return null;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			} finally {
//
//				// close the stream if it exists:
//				if (input != null) {
//					try {
//						input.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//
//				// close the connectin if it exists:
//				if (connection != null) {
//					connection.disconnect();
//				}
//			}
//
//			// get the string from the string builder:
//			return response.toString();
//			
//		}
		
}
