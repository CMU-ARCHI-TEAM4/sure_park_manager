package com.lge.sureparkmanager.db;

/**
 * 
 * @author hakjoo.lee
 *
 */
public class UserInformation {
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String creditCardNumber;
	private String creditValidation;
	

	public UserInformation(String id, String firstName, String lastName, String email, String phoneNumber, String creditCardNumber, String creditCardValidation) {
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.creditCardNumber = creditCardNumber;
		this.creditValidation = creditCardValidation;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getFristName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public String getCreditCardNumber() {
		return this.creditCardNumber;
	}
	
	public String getCreditCardValidation() {
		return this.creditValidation;
	}
}
