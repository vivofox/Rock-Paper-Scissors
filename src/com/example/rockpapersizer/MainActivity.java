package com.example.rockpapersizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rockpapersizer.MultyplayerFrag.onMultyplayer;
import com.example.rockpapersizer.PlayerFrag.onUserClick;
import com.example.rockpapersizer.WelcomeFrag.onChoiceStart;

public class MainActivity extends ActionBarActivity implements onUserClick, onChoiceStart, onMultyplayer {

	public String TAG = "MainActivity";

	// private boolean isOnline;
	private static final String API = "http://rockpapaer.herokuapp.com/login";

	public String userNameOnline;
	public int gameId;
	public int gustHandRestponse;

	List<GameOfRps> gamesList;

	// a timer to keep pulling from the sever (every 2 seconds...)
	private Timer timer;

	private boolean isGameActivityRuninng = false;
	private static final int REFRESH_DELAY = 2000;

	@Override
	protected void onStart() {
		super.onStart();

		// create and start a repeating timer:
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// this will run on the timer thread...
				// async tasks must be started from the UI thread:
				runOnUiThread(new Runnable() {

					public void run() {
						// this will run on the ui thread.

						// refresh the list
						if (userNameOnline != null) {

							new CheckForNewGames().execute(API);

						}

					}
				});
			}
		}, 1, REFRESH_DELAY);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		isGameActivityRuninng = false;
		
		WelcomeFrag fragment = WelcomeFrag.newInstance();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.FramContiner, fragment, "WelcomeFrag");
		ft.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {

			gamesList = new ArrayList<GameOfRps>();

			WelcomeFrag fragment = WelcomeFrag.newInstance();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.FramContiner, fragment, "WelcomeFrag");
			ft.commit();

		}

	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		Log.d(TAG, "onPause");

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");

		// cancel timer:
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		// if the user pressed on the back button in the first fragment - do a logout!
		if (getSupportFragmentManager().getBackStackEntryCount() == 0 && !isGameActivityRuninng) {
			if (userNameOnline != null) {
				new LogoutUser().execute();
				
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public void onBackPressed() {

		// // if the user pressed on the back button in the first fragment - do
		// a logout!
		// if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
		// if (userNameOnline != null) {
		// new LogoutUser().execute();
		// }
		// }
		super.onBackPressed();
	}

	@Override
	public void userChoice(int btnChoise) {
		// TODO Auto-generated method stub

		FightFrag fragment = FightFrag.newInstance(btnChoise);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.FramContiner, fragment, "FightFrag");
		ft.addToBackStack(null);
		ft.commit();

	}

	@Override
	public void onChoiceSinglePlayer() {
		// TODO Auto-generated method stub

		PlayerFrag fragment = PlayerFrag.newInstance();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.FramContiner, fragment, "playerFrag");
		ft.addToBackStack(null);
		ft.commit();

	}

	@Override
	public void onChoiceMultyplayer() {
		// TODO Auto-generated method stub

		MultyplayerFrag fragment = MultyplayerFrag.newInstance(userNameOnline);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.FramContiner, fragment, "MultyplayerFrag");
		ft.addToBackStack(null);
		ft.commit();

	}

	@Override
	public void onChoiceExit() {
		// bye bye...

		if (userNameOnline != null) {
			new LogoutUser().execute();
		}
		finish();
	}

	@Override
	public void onChoiceGames() {
		// start the games activity

		Intent intent = new Intent(this, GamesActivity.class);
		intent.putExtra("userNameOnlineHost", userNameOnline);
		startActivity(intent);
		isGameActivityRuninng  = true;

	}

	@Override
	public void onChoiceLogout() {
		// TODO Auto-generated method stub

		if (userNameOnline != null) {

			AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

			alert.setTitle("you are online as the user : " + userNameOnline);
			alert.setMessage("Do you want to log off?");

			alert.setPositiveButton("logout", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					//
					new LogoutUser().execute();

				}
			});

			alert.setNegativeButton("stay as " + userNameOnline, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled
				}
			});

			alert.show();
		}
	}

	@Override
	public void onChoiceLogin() {
		// TODO Auto-generated method stub

		if (userNameOnline == null) {

			AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

			alert.setTitle("Login user");
			alert.setMessage("enter a user name");

			// Set an EditText view to get user input
			final EditText input = new EditText(MainActivity.this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					// Do something with value!
					MainActivity.this.userNameOnline = value;

					new LoginUser().execute(API);

				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();

		}

	}

	@Override
	public String isOnline() {
		//
		return userNameOnline;
	}

	class LoginUser extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(params[0]);

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			// send the user parameters
			postParameters.add(new BasicNameValuePair("userName", userNameOnline));
			postParameters.add(new BasicNameValuePair("userPass", "0000"));
			postParameters.add(new BasicNameValuePair("userEmail", "123"));
			// set the "reqAction" for the server to be "addUser"
			postParameters.add(new BasicNameValuePair("reqAction", "addUser"));

			UrlEncodedFormEntity formEntity = null;
			try {
				formEntity = new UrlEncodedFormEntity(postParameters, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return false;
			}

			request.setEntity(formEntity);

			try {
				client.execute(request);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;

		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result && userNameOnline != null) {
				Log.e(TAG, "user is online: " + userNameOnline);

				WelcomeFrag fragment = WelcomeFrag.newInstance();
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.FramContiner, fragment, "WelcomeFrag");
				ft.commit();

			}
		}

	}

	class LogoutUser extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// perform an HTTP DELETE request
			if (userNameOnline != null) {

				return delete(API, new String[] { "userName", userNameOnline });
			} else {

				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {

			try {
				if (result != null) {
					userNameOnline = null;

					WelcomeFrag fragment = WelcomeFrag.newInstance();
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.FramContiner, fragment, "WelcomeFrag");
					ft.commit();

				} else {
					Log.e(TAG, "error from web, can't logout");
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class CheckForNewGames extends AsyncTask<String, Void, String> {

		private static final String TAG = "RefreshGamesList";


		@Override
		protected String doInBackground(String... params) {

			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			String reqAction = "printAllGames";

			try {
				String queryString = "";

				queryString += "?reqAction=" + URLEncoder.encode(reqAction, "utf-8");

				// show on logCat the entire address
				Log.d(TAG, params[0] + queryString);

				// saving the address in a URL variable
				URL url = new URL(params[0] + queryString);

				// open a connection
				connection = (HttpURLConnection) url.openConnection();

				// check the result status of the conection:
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// not good.
					return null;
				}

				input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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

					JSONObject responseObject = new JSONObject(result);
					JSONArray resultsArray = responseObject.getJSONArray("games");

					// Iterate over the JSON array:
					for (int i = 0; i < resultsArray.length(); i++) {

						if (userNameOnline.equals(resultsArray.getJSONObject(i).getString("gustUserName"))) {

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

							GameOfRps game = new GameOfRps(gameId, gameTimeStamp, hostUserName, gustUserName, hostHand);

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

				// check for opened games:

				for (int i = 0; i < gamesList.size(); i++) {
					if (gamesList.get(i).getGameResult().equals("null") && gamesList.get(i).getGustUserName().equals(userNameOnline)) {

						// if find a game with gameResult : "null" so set the
						// result to "waiting"
						new PutTaskToWaitnig().execute();

						// pull a game from list
						GameOfRps game = gamesList.get(i);
						// get game id:
						gameId = game.getGameId();

						if (game.getGustHand() == 0) {

							// crate a dialog massage for user choose:
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									switch (which) {
									case Dialog.BUTTON_POSITIVE:

										gustHandRestponse = -1;
										new PutTaskToFinish().execute();
										break;

									case Dialog.BUTTON_NEGATIVE:
										gustHandRestponse = -2;
										new PutTaskToFinish().execute();
										break;

									case Dialog.BUTTON_NEUTRAL:
										gustHandRestponse = -3;
										new PutTaskToFinish().execute();
										break;
									}
								}
							};

							// alert dialog :
							AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setCancelable(false)
									.setTitle("Game from:" + game.getHostUserName()).setMessage("please select your hand")
									.setPositiveButton("Rock", listener).setNegativeButton("Paper", listener)
									.setNeutralButton("Scissors", listener).create();

							dialog.show();

						}

					}

				}
			}
		}
	}

	class ShowGameResultTask extends AsyncTask<String, Void, String> {

		private static final String TAG = "GetGameById";

		@Override
		protected String doInBackground(String... params) {

			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			String reqAction = "printGameById";

			try {
				String queryString = "";

				queryString += "?reqAction=" + URLEncoder.encode(reqAction, "utf-8");
				queryString += "&gameId=" + URLEncoder.encode(params[0], "utf-8");

				// show on logCat the entire address
				Log.d(TAG, API + queryString);

				// saving the address in a URL variable
				URL url = new URL(API + queryString);

				// open a connection
				connection = (HttpURLConnection) url.openConnection();

				// check the result status of the conection:
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// not good.
					return null;
				}

				input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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

					JSONObject responseObject = new JSONObject(result);
					JSONArray resultsArray = responseObject.getJSONArray("games");

					// Iterate over the JSON array:
					for (int i = 0; i < resultsArray.length(); i++) {

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

						GameOfRps game = new GameOfRps(gameId, gameTimeStamp, hostUserName, gustUserName, hostHand);

						game.setGustHand(gustHand);
						game.setUserNameWon(userNameWon);
						game.setUserNameLose(userNameLose);
						game.setGameResult(gameResult);


						///////////////////////////////Make the game toast result//////////////////////////////////////////
						LayoutInflater inflater = getLayoutInflater();
						View layout = inflater.inflate(R.layout.toast,(ViewGroup) findViewById(R.id.toast_layout_root));
						
						// find views from toast xml
						LinearLayout toastLayout = (LinearLayout) layout.findViewById(R.id.LinearLayoutError);
						TextView hostUserNameTV = (TextView) layout.findViewById(R.id.textViewToastHostUserName);
						TextView guestUserNameTV = (TextView) layout.findViewById(R.id.textViewToastGuestUserName);
						TextView gameResultTV = (TextView) layout.findViewById(R.id.textViewToastGameResult);
						ImageView hostUserHandIV = (ImageView) layout.findViewById(R.id.imageViewToastHostUserHand);
						ImageView guestUserHandIV = (ImageView) layout.findViewById(R.id.imageViewToastGuestUserHand);
						// set error massage to "gone"
						toastLayout.setVisibility(View.GONE);
						// set players names on toast:
						hostUserNameTV.setText(game.getHostUserName());
						guestUserNameTV.setText(game.getGustUserName());
						
						//set the host user hand image on toast:
						switch (game.getHostHand()) {
						case -1:
							hostUserHandIV.setImageResource(R.drawable.r);
							break;

						case -2:
							hostUserHandIV.setImageResource(R.drawable.p);
							break;

						case -3:
							hostUserHandIV.setImageResource(R.drawable.s);
							break;
						}
						
						//set the guest user hand image on toast:
						switch (game.getGustHand()) {
						case -1:
							guestUserHandIV.setImageResource(R.drawable.r);
							break;

						case -2:
							guestUserHandIV.setImageResource(R.drawable.p);
							break;

						case -3:
							guestUserHandIV.setImageResource(R.drawable.s);
							break;
						}
						
						//set the game result on toast :
						if (gameResult.equals("tie")) {

							gameResultTV.setText("it's a tie");

						} else if (userNameWon.equals(gustUserName)) {

							gameResultTV.setText("you win");

						} else {

							gameResultTV.setText("you lose");

						}
						
						// create and show the game toast :
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(layout);
						toast.show();
					
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

			}
		}
	}

	// task for PUT request
	class PutTaskToFinish extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			// answer for an open game;
			return putToFinish(API, new String[] { "gameId", gameId + "", "gustHand", gustHandRestponse + "", "reqAction",
					"setResultFinish" });
		}

		@Override
		protected void onPostExecute(String result) {

			// refresh screen
			if (result != null) {

				// get the game details from server and show on a dialog:
				new ShowGameResultTask().execute(gameId + "");

			} else {
				Log.e(TAG, "no result from server");
			}
		}

	}

	// task for PUT request
	class PutTaskToWaitnig extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// perform an HTTP PUT request
			return putToWaiting(API, new String[] { "gameId", gameId + "", "reqAction", "setResultWaiting" });
		}

		@Override
		protected void onPostExecute(String result) {
			// refresh screen
			new CheckForNewGames().execute(API);
		}

	}

	public static String delete(String address, String[]... params) {
		BufferedReader input = null;
		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		try {
			// build query string:
			String queryString = "?";

			for (String[] pair : params) {
				String name = URLEncoder.encode(pair[0], "utf-8");
				String value = URLEncoder.encode(pair[1], "utf-8");
				queryString += name + "=" + value + "&";
			}

			// remove the last "&" :
			queryString = queryString.substring(0, queryString.length() - 1);

			Log.d("queryString", queryString);

			// prepare a URL object :
			URL url = new URL(address + queryString);

			// open a connection
			connection = (HttpURLConnection) url.openConnection();

			// set the connection to HTTP DELETE
			connection.setRequestMethod("DELETE");

			// check the result status of the conection:

			int status = connection.getResponseCode();
			if (status < 200 || status > 299) {
				// not good.
				return null;
			}

			// get the input stream from the connection
			input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

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

	public static String putToFinish(String address, String[]... params) {
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
				String name3 = URLEncoder.encode(pair[4], "utf-8");
				String value3 = URLEncoder.encode(pair[5], "utf-8");
				queryString += name + "=" + value + "&";
				queryString += name2 + "=" + value2 + "&";
				queryString += name3 + "=" + value3 + "&";
			}

			// remove the last "&" :
			queryString = queryString.substring(0, queryString.length() - 1);

			// prepare a URL object :
			Log.d("TAG", address + queryString);
			URL url = new URL(address + queryString);

			// open a connection
			connection = (HttpURLConnection) url.openConnection();

			// set the connection to HTTP PUT
			connection.setRequestMethod("PUT");

			// check the result status of the conection:

			int status = connection.getResponseCode();
			if (status < 200 || status > 299) {
				// not good.
				return null;
			}

			// get the input stream from the connection
			input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

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

	public static String putToWaiting(String address, String[]... params) {
		BufferedReader input = null;
		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		try {
			// build query string:
			String queryString = "?";

			for (String[] pair : params) {
				// reqAction
				String nameReqAction = URLEncoder.encode(pair[0], "utf-8");
				String valueReqAction = URLEncoder.encode(pair[1], "utf-8");
				// gameId
				String nameGameId = URLEncoder.encode(pair[2], "utf-8");
				String valueGameId = URLEncoder.encode(pair[3], "utf-8");
				queryString += nameReqAction + "=" + valueReqAction + "&";
				queryString += nameGameId + "=" + valueGameId + "&";
			}

			// remove the last "&" :
			queryString = queryString.substring(0, queryString.length() - 1);

			// prepare a URL object :
			Log.d("TAG", address + queryString);
			URL url = new URL(address + queryString);

			// open a connection
			connection = (HttpURLConnection) url.openConnection();

			// set the connection to HTTP PUT
			connection.setRequestMethod("PUT");

			// check the result status of the conection:

			int status = connection.getResponseCode();
			if (status < 200 || status > 299) {
				// not good.
				return null;
			}

			// get the input stream from the connection
			input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

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

	boolean isUserOnWaiting(String userName) {

		for (int i = 0; i < gamesList.size(); i++) {
			GameOfRps game = gamesList.get(i);
			if (game.getGustUserName().equals(userName)) {
				return true;
			}
		}
		return false;
	}

}
