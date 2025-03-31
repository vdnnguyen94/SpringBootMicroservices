package com.example.demo;

public class LoginBean {
	//declare properties
	private String username;
	private String password;
	
	//setter and getter methods
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

    // Overriding toString() method
    @Override
    public String toString() {
        return "LoginBean{" +
                "username='" + username + '\'' +
                ", password='" +password+ 
                "'}";
    }
	
}
