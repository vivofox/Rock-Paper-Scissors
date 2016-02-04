package com.example.rockpapersizer;



import java.util.Date;

public class GameOfRps {
	
	private int gameId;
	long gameTimeStamp;
	
	private String hostUserName;
	private String gustUserName;
	private int hostHand;
	private int gustHand;
	
	private String userNameWon;
	private String userNameLose;
	private String gameResult;
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// empty constructor:
	public GameOfRps() {
		
		
	}
	
	// a constructor for new host game:
	public GameOfRps(int gameId , String hostUserName , String gustUserName , int hostHand) {
		
		this.gameId = gameId;
		this.gameTimeStamp = new Date().getDate();
		
		this.hostUserName = hostUserName;
		this.gustUserName = gustUserName;
		this.hostHand = hostHand;
		
	}
	
	// a constructor for new host game:
	public GameOfRps(int gameId ,long gameTimeStamp, String hostUserName , String gustUserName , int hostHand) {
		
		this.gameId = gameId;
		this.gameTimeStamp = new Date().getDate();
		
		this.hostUserName = hostUserName;
		this.gustUserName = gustUserName;
		this.hostHand = hostHand;
		
	}
	
	public int getGameId() {
		return gameId;
	}
	
	private void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public long getGameTimeStamp() {
		return gameTimeStamp;
	}
	public void setGameTimeStamp(long gameTimeStamp) {
		this.gameTimeStamp = gameTimeStamp;
	}
	public String getHostUserName() {
		return hostUserName;
	}
	public void setHostUserName(String hostUserName) {
		this.hostUserName = hostUserName;
	}
	public String getGustUserName() {
		return gustUserName;
	}
	public void setGustUserName(String gustUserName) {
		this.gustUserName = gustUserName;
	}
	public int getHostHand() {
		return hostHand;
	}
	public void setHostHand(int hostHand) {
		this.hostHand = hostHand;
	}
	public int getGustHand() {
		return gustHand;
	}
	public void setGustHand(int gustHand) {
		this.gustHand = gustHand;
	}
	public String getUserNameWon() {
		return userNameWon;
	}
	public void setUserNameWon(String userNameWon) {
		this.userNameWon = userNameWon;
	}
	public String getUserNameLose() {
		return userNameLose;
	}
	public void setUserNameLose(String userNameLose) {
		this.userNameLose = userNameLose;
	}
	public String getGameResult() {
		return gameResult;
	}
	public void setGameResult(String gameResult) {
		this.gameResult = gameResult;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{\"gameId\":\"" + this.gameId 
				+ "\",\"gameTimeStamp\": \"" + this.gameTimeStamp
				+ "\",\"hostUserName\": \"" + this.hostUserName
				+ "\",\"gustUserName\": \"" + this.gustUserName
				+ "\",\"hostHand\": \"" + this.hostHand
				+ "\",\"gustHand\": \"" + this.gustHand
				+ "\",\"userNameWon\": \"" + this.userNameWon
				+ "\",\"userNameLose\": \"" + this.userNameLose
				+ "\",\"gameResult\": \"" + this.gameResult
				+ "\"" + "}";
				
	}
	
}
