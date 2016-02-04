package com.example.rockpapersizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MultyplayerFrag extends Fragment implements OnItemClickListener, OnClickListener {

	private static final String TAG = "MultyplayerFrag";
	private static final String API = "http://rockpapaer.herokuapp.com/login";

	public int gameId;
	public int gustHandRestponse;

	// a timer to keep pulling from the sever (every 2 seconds...)
	private Timer timer;
	private static final int REFRESH_DELAY = 2000;

	public interface onMultyplayer {

	}

	public onMultyplayer listener;

	UserAdapter adapter;

	List<User> userList;
	User user = new User();

	private boolean isOnline;

	public String userNameOnlineHost;
	public String userNameOnlineGust;
	public String userHandInAction;

	public static MultyplayerFrag newInstance(String userName) {

		MultyplayerFrag frag = new MultyplayerFrag();

		Bundle args = new Bundle();
		args.putString("userNameInAction", userName);

		frag.setArguments(args);

		return frag;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (onMultyplayer) activity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "please implment onMultyplayer");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//

		View view = inflater.inflate(R.layout.multy_frag, container, false);

		userList = new ArrayList<User>();
		adapter = new UserAdapter();

		Bundle args = getArguments();

		// get the user name on line from main
		userNameOnlineHost = args.getString("userNameInAction");

		// show the login user name
		TextView textViewOut = (TextView) view.findViewById(R.id.textViewMultyHead);
		textViewOut.setText("user :" + userNameOnlineHost + " is online");

		ListView listView = (ListView) view.findViewById(R.id.listViewUsersList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		new RefreshUserListGetTask().execute(API);

		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// create and start a repeating timer:
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// this will run on the timer thread...
				// async tasks must be started from the UI thread:
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						// this will run on the ui thread.

						// refresh the list
						if (userNameOnlineHost != null) {
							new RefreshUserListGetTask().execute(API);

						}

					}
				});
			}
		}, 1, REFRESH_DELAY);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onClick");

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Log.d(TAG, "onItemClick");

		userNameOnlineGust = userList.get(position).getUserName();

		// crate a dialog massage for user choose:
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case Dialog.BUTTON_POSITIVE:

					userHandInAction = "-1";
					//new StartGamePostTask().execute(API);
					new SendPostReqAsyncTask().execute();
					break;

				case Dialog.BUTTON_NEGATIVE:

					userHandInAction = "-2";
					new SendPostReqAsyncTask().execute();
					break;

				case Dialog.BUTTON_NEUTRAL:
					userHandInAction = "-3";
					new SendPostReqAsyncTask().execute();

					break;
				}
			}
		};

		// alert dialog :
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(true).setMessage("please select your hand")
				.setTitle("Start a game VS " + userNameOnlineGust).setPositiveButton("Rock", listener).setNegativeButton("Paper", listener)
				.setNeutralButton("Scissors", listener).create();
		dialog.show();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.context, menu);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.d(TAG, "onDetach");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");

		// cancel timer:
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

	}

	class LogoutUserDeleteTask extends AsyncTask<Void, Void, String> {

		private String address;
		private String userName;

		@Override
		protected void onPreExecute() {

			address = API;

			TextView output = (TextView) getActivity().findViewById(R.id.textViewMultyHead);

			// userName = editTextUserName.getText().toString();
			userName = userNameOnlineHost;

			output.setText("user: " + userName + " logout \n");
		}

		@Override
		protected String doInBackground(Void... params) {
			// perform an HTTP DELETE request
			if (isOnline) {
				return delete(address, new String[] { "userName", userName });
			} else {

				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			TextView output = (TextView) getActivity().findViewById(R.id.textViewMultyHead);

			if (result != null) {
				output.append(result);
				new RefreshUserListGetTask().execute(API);
				isOnline = false;

			} else {
				output.append("error...");
			}
		}
	}

	class LogoutUser extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// perform an HTTP DELETE request
			if (userNameOnlineHost != null) {

				return delete(API, new String[] { "userName", userNameOnlineHost });
			} else {

				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {

			try {
				if (result != null) {

					userNameOnlineHost = null;

				} else {
					Log.e(TAG, "error from web, can't logout");
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class RefreshUserListGetTask extends AsyncTask<String, Void, String> {

		private static final String TAG = "RefreshUserList";

		@Override
		protected String doInBackground(String... params) {

			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			// http://10.0.0.3:8080/Rpz/login?printAll=
			String reqAction = "printAllUsers";

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

					userList.clear();

					Log.d(TAG, result);
					JSONObject responseObject = new JSONObject(result);
					JSONArray resultsArray = responseObject.getJSONArray("users");

					// Iterate over the JSON array:
					for (int i = 0; i < resultsArray.length(); i++) {

						JSONObject userObject = resultsArray.getJSONObject(i);

						// get the primitive values in the object
						String userName = userObject.getString("name");
						String userEmail = userObject.getString("Email");

						User user = new User(userName, "****", userEmail);

						// add only the others to the list:
						if (!userNameOnlineHost.equals(user.getUserName())) {
							userList.add(user);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// notify adapter:
				adapter.notifyDataSetChanged();
			}
		}
	}

	class StartGamePostTask extends AsyncTask<String, Void, Boolean> {

		ProgressDialog daialog;

		@Override
		protected void onPreExecute() {

			// crate a progress massage:
			daialog = new ProgressDialog(getActivity());
			daialog.setTitle("waiting for user response");
			daialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			daialog.show();

		}

		@Override
		protected Boolean doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(params[0]);

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			// set the "reqAction" for the server to be "addHostGame"
			postParameters.add(new BasicNameValuePair("reqAction", "addHostGame"));
			// send the user parameters
			postParameters.add(new BasicNameValuePair("hostUserName", userNameOnlineHost));
			postParameters.add(new BasicNameValuePair("hostHand", userHandInAction));
			postParameters.add(new BasicNameValuePair("gustUserName", userNameOnlineGust));

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
			
			daialog.dismiss();

		}

	}
	
    class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

		ProgressDialog progressDaialog;

		@Override
		protected void onPreExecute() {

			// crate a progress massage:
			progressDaialog = new ProgressDialog(getActivity());
			progressDaialog.setTitle("waiting for user response");
			progressDaialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDaialog.setCancelable(false);
			progressDaialog.show();

		}
		
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();

            // In a POST request, we don't pass the values in the URL.
            //Therefore we use only the web page URL as the parameter of the HttpPost argument
            HttpPost httpPost = new HttpPost(API);

            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
            //uniquely separate by the other end.
            //To achieve that we use BasicNameValuePair             
            //Things we need to pass with the POST request

            // We add the content that we want to pass with the POST request to as name-value pairs
            //Now we put those sending details to an ArrayList with type safe of NameValuePair
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(new BasicNameValuePair("reqAction", "addHostGame"));
            nameValuePairList.add(new BasicNameValuePair("hostUserName", userNameOnlineHost));
            nameValuePairList.add(new BasicNameValuePair("hostHand", userHandInAction));
            nameValuePairList.add(new BasicNameValuePair("gustUserName", userNameOnlineGust));

            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs. 
                //This is typically useful while sending an HTTP POST request. 
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                httpPost.setEntity(urlEncodedFormEntity);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    // According to the JAVA API, InputStream constructor do nothing. 
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }
        
        @Override
        protected void onPostExecute(String result) {
        	
        	progressDaialog.dismiss();
        	
        	if (result == null || result.toString().equals("Erorr user busy!")) {
        		
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast,(ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
				TextView textViewError = (TextView) layout.findViewById(R.id.textViewError);
				
				try {
					
					textViewError.setText(result);
					
				} catch (Exception e) {
					// TODO: handle exception
					textViewError.setText("no result from server");
				}
				
				// create and show the game toast :
				Toast toast = new Toast(getActivity().getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();
				
        		
        		
			}else {
				
	        	try {
					// get the json from result
	        		Log.d(TAG, result);
					JSONObject userObject = new JSONObject(result);

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

					// create a complete game with result details
					GameOfRps game = new GameOfRps(gameId, gameTimeStamp, hostUserName, gustUserName, hostHand);
					game.setGustHand(gustHand);
					game.setUserNameWon(userNameWon);
					game.setUserNameLose(userNameLose);
					game.setGameResult(gameResult);

					///////////////////////////////Make the game toast result//////////////////////////////////////////
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View layout = inflater.inflate(R.layout.toast,(ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
					
					// find views from toast xml
					TextView hostUserNameTV = (TextView) layout.findViewById(R.id.textViewToastHostUserName);
					TextView guestUserNameTV = (TextView) layout.findViewById(R.id.textViewToastGuestUserName);
					TextView gameResultTV = (TextView) layout.findViewById(R.id.textViewToastGameResult);
					ImageView hostUserHandIV = (ImageView) layout.findViewById(R.id.imageViewToastHostUserHand);
					ImageView guestUserHandIV = (ImageView) layout.findViewById(R.id.imageViewToastGuestUserHand);
					LinearLayout toastLayout = (LinearLayout) layout.findViewById(R.id.LinearLayoutError);
					
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

						gameResultTV.setText("you lose");

					} else {

						gameResultTV.setText("you win");

					}
					
					// create and show the game toast :
					Toast toast = new Toast(getActivity().getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(layout);
					toast.show();



				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				
				
	        }
        	
			}

	}

	class CheckForNewGamesGetTask extends AsyncTask<String, Void, String> {

		private static final String TAG = "RefreshGamesList";

		List<GameOfRps> gamesList;

		@Override
		protected void onPreExecute() {

			gamesList = new ArrayList<GameOfRps>();
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

			if (result != null) {

				try {

					gamesList.clear();

					JSONObject responseObject = new JSONObject(result);
					JSONArray resultsArray = responseObject.getJSONArray("games");

					// Iterate over the JSON array:
					for (int i = 0; i < resultsArray.length(); i++) {

						if (userNameOnlineHost.equals(resultsArray.getJSONObject(i).getString("gustUserName"))) {

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
			}
		}
	}

	// custom BaseAdapter adapter class
	class UserAdapter extends BaseAdapter {

		public int getCount() {
			return userList.size(); // the list's size
		}

		public Object getItem(int position) {

			// return null; //it's ok
			return null;
		}

		public long getItemId(int position) {
			return 0; // it's ok
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// create / recycle the view :
			View v = convertView;
			if (v == null) {
				v = getActivity().getLayoutInflater().inflate(R.layout.user_list_from_web, parent, false);
			}

			// get the user (in the position) :
			User user = userList.get(position);

			// bind user->view :
			TextView userName = (TextView) v.findViewById(R.id.TextViewUserName);
			TextView userEmail = (TextView) v.findViewById(R.id.textViewUserEmail);

			// name
			userName.setText("user name : "+user.getUserName());
			userEmail.setText("user Email : " + user.getUserEmail());

			return v;
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

}
