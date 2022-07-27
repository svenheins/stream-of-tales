package de.svenheins;

/**	External DataBase Authenticator:  An easy way to authenticate your RDS users against
 * an 'external' database.
 *
 * Make sure you set the properties for this class properly in your app.properties file.
 * Take a look at the accompanied 'app.properties' file for help with that.
 *
 * @author	David Wipperfurth
 * @dated	4/16/10
 * @copyright	Public Domain
 */

//Use whatever package you want here, all needed imports are listed.
//Just make sure the package you choose is properly reflected in your properties file
//package com.sun.sgs.auth;

import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.impl.auth.IdentityImpl;
import com.sun.sgs.impl.auth.NamePasswordCredentials;

import com.mysql.jdbc.Driver;

import java.sql.*;
//import java.lang.ClassNotFoundException;
//import com.mysql.jdbc.Driver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.security.DigestException;

//import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;

public class ExternalDBAuthenticator implements IdentityAuthenticator {

    private static final Logger logger = Logger.getLogger(ExternalDBAuthenticator.class.getName()); // Used for all logging from this class

    public static final String PROP_JDBC_DRIVER = "simpleremoteconnection.ExternalDBAuthenticator.JDBC.driver";
    public static final String PROP_JDBC_LOCATION = "simpleremoteconnection.ExternalDBAuthenticator.JDBC.location";
    public static final String PROP_JDBC_USER = "simpleremoteconnection.ExternalDBAuthenticator.JDBC.user";
    public static final String PROP_JDBC_PASSWORD = "simpleremoteconnection.ExternalDBAuthenticator.JDBC.password";
    public static final String PROP_SALT = "simpleremoteconnection.ExternalDBAuthenticator.salt";
	public static final String PROP_ENCRYPTION = "simpleremoteconnection.ExternalDBAuthenticator.encryption";
	public static final String PROP_DB_TABLE = "simpleremoteconnection.ExternalDBAuthenticator.db.table";
	public static final String PROP_DB_USERNAME_FIELD = "simpleremoteconnection.ExternalDBAuthenticator.db.table.field.username";
	public static final String PROP_DB_PASSWORD_FIELD = "simpleremoteconnection.ExternalDBAuthenticator.db.table.field.password";
	public static final String PROP_DB_GAMEID_FIELD = "simpleremoteconnection.ExternalDBAuthenticator.db.table.field.gameid";

    private String driver;
    private String location;
    private String user;
    private String password;
    private String salt;
	private String table;
	private String userNameField;
	private String passwordField;
	private String gameIDField;
	private String encryption;

	/**	Constructor:  Creates an Authenticator object.
	 *
	 * @param properties
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
    public ExternalDBAuthenticator(Properties properties) throws ClassNotFoundException,SQLException{
        if(properties == null) throw new NullPointerException("Null properties not allowed");

		String info = "Authentication Info:\n";

        //get property by properties.getProperty("property name here")
        this.driver = properties.getProperty(PROP_JDBC_DRIVER);
		if(this.driver == null) this.driver = "com.mysql.jdbc.Driver";
		info += "DBMS Driver = '"+this.driver+"'\n";

        this.location = properties.getProperty(PROP_JDBC_LOCATION);
		if(this.location == null) this.location = "jdbc:mysql://localhost/rds";
		info += "DB Location = '"+this.location+"'\n";
        
		this.user = properties.getProperty(PROP_JDBC_USER);
		if(this.user == null) this.user = "rds";
		info += "DB Connection UserName = '"+this.user+"'\n";
        
		this.password = properties.getProperty(PROP_JDBC_PASSWORD);
		if(this.password == null) this.password = "drowssap";
		info += "DB Connection Password = '"+this.password+"'\n";
        
		this.salt = properties.getProperty(PROP_SALT);
		if(this.salt == null) this.salt = "";
		info += "User Password Salt = '"+this.salt+"'\n";
		
		this.encryption = properties.getProperty(PROP_ENCRYPTION);
		if(this.encryption == null) this.encryption = "MD5";
		info += "User Password Encryption/Hash = '"+this.encryption+"'\n";
		
		this.table = properties.getProperty(PROP_DB_TABLE);
		if(this.table == null) this.table = "users";
		info += "User Table Name = '"+this.table+"'\n";
		
		this.userNameField = properties.getProperty(PROP_DB_USERNAME_FIELD);
		if(this.userNameField == null) this.userNameField = "name";
		info += "User Table's UserName Field Name = '"+this.userNameField+"'\n";
		
		this.passwordField = properties.getProperty(PROP_DB_PASSWORD_FIELD);
		if(this.passwordField == null) this.passwordField = "password";
		info += "User Table's Password Field Name = '"+this.passwordField+"'\n";
		
		this.gameIDField = properties.getProperty(PROP_DB_GAMEID_FIELD);
		if(this.gameIDField == null) this.gameIDField = "name";
		info += "User Table's GameID/UserName Field Name = '"+this.gameIDField+"'\n";

		logger.info(info);
		
        //tests the DB connection to throw an impending error sooner rather than later
		Class.forName(this.driver);
        (DriverManager.getConnection(this.location, this.user, this.password)).close();
    }

    public Identity authenticateIdentity(IdentityCredentials credentials) throws AccountNotFoundException, CredentialException{
        if(!(credentials instanceof NamePasswordCredentials)) throw new CredentialException("unsupported credentials");
        NamePasswordCredentials cred = (NamePasswordCredentials) credentials;
        if(cred.getName().length() < 1 || cred.getPassword().length < 1) throw new AccountNotFoundException();  //make the very minimal assumption about valid names and passwords to prevent un-needed DB connections

		String passDB;
		String gameID;
        try{
			Connection con = DriverManager.getConnection(this.location, this.user, this.password);
			PreparedStatement stmt = con.prepareStatement(
				  " SELECT u.`"+this.gameIDField+"`, u.`"+this.passwordField+"` "
				+ " FROM `"+this.table+"` u "
				+ " WHERE  u.`"+this.userNameField+"` = ? "
				+ " LIMIT 1 ");
			stmt.setString(1, cred.getName());

			ResultSet results = stmt.executeQuery();
			if(!results.next()) throw new AccountNotFoundException("User failed to log in using userName '"+cred.getName()+"'.");

			passDB = results.getString(this.passwordField);
			gameID = results.getString(this.gameIDField);

			stmt.close();
			con.close();
        }catch(SQLException e){
			logger.severe(e.getMessage());
			throw new AccountNotFoundException(e.getMessage());
        }

		String pass;
		try{
			pass = hash(this.encryption, this.salt + new String(cred.getPassword()) );
		}/*catch(DigestException e){
			throw new AccountNotFoundException(e.getMessage());
		}*/catch(NoSuchAlgorithmException e){
			logger.severe(e.getMessage());
			throw new AccountNotFoundException(e.getMessage());
		}

		if(!passDB.equals(pass)) throw new AccountNotFoundException("User '"+cred.getName()+"' failed to log in using password '"+(new String(cred.getPassword()))+"'.");

		logger.info("User '"+cred.getName()+"' logged in.");
		
        return new IdentityImpl(gameID);
    }

    public String[] getSupportedCredentialTypes(){
        return new String [] { NamePasswordCredentials.TYPE_IDENTIFIER };
    }

	/**	Hash:  hashes/encrypts a string into a HexString using a specified algorithm.
	 *
	 * @param crypt	The name of the encryption/hash algorithm
	 * @param content The string you wish to encrypt/hash
	 * @return The resulting HexString
	 * @throws NoSuchAlgorithmException
	 */
	public String hash(String crypt, String content) throws NoSuchAlgorithmException{
		// Create Hash
		MessageDigest digest = MessageDigest.getInstance(crypt);
		digest.update(content.getBytes());
		byte[] messageDigest = digest.digest();

		// Create Hex String
		StringBuffer hexString = new StringBuffer();
		for (int i=0; i<messageDigest.length; i++){
			String h = Integer.toHexString(0xFF & messageDigest[i]);
			while(h.length()<2) h = "0" + h;
			hexString.append(h);
		}
		return hexString.toString();
	}

}