package com.example.rockpapersizer;

public class User {
	
	private String userName;
	private String userPass;
	private String userEmail;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String userName , String userPass , String userEmail) {
		// 
		this.userName = userName;
		this.userPass = userPass;
		this.userEmail = userEmail;
	}
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	@Override
	public String toString() {
		// json format
		return "{\"name\":\"" + this.userName + "\",\"Email\": \"" + this.userEmail + "\"" + "}";
		//return this.userName + " " + this.userEmail;
	}


}
