package sql.wrappers;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import sql.SQLExecutable;
import sql.SQLParam;
import sql.SQLType;

import com.google.gson.annotations.Expose;

import core.Server;
public class UserLoginWrapper extends SQLExecutable{
	@Expose(serialize = true)
	private String firstName = null;
	@Expose(serialize = true)
	private String lastName = null;
	@Expose(serialize = true)
	private int userID = -1;
	@Expose(serialize = true)
	private String token = null;
	
	private transient String emailAddress, sha256pwd;
	private transient boolean validated = false;
	
	public UserLoginWrapper(String emailAddress, String base64sha256pwd) {
		this.emailAddress = emailAddress;
		this.sha256pwd = base64sha256pwd;
	}
	
	public static enum AuthResult {
		OK,
		INVALID_PWD,
		INTERNAL_ERROR,
		MALFORMED_INPUT,
		USER_NOT_FOUND
	}
	
	public AuthResult isAuthenticated() {
		if (emailAddress == null || sha256pwd == null ) return AuthResult.MALFORMED_INPUT;
		byte [] bytePWD;
		try {
			bytePWD = Hex.decodeHex(sha256pwd.toCharArray());
			if (bytePWD.length != 32) return AuthResult.MALFORMED_INPUT;
		} catch (DecoderException e) {return AuthResult.MALFORMED_INPUT;}
		
		ResultSet results = null;
		try {
			results = query("SELECT * FROM User WHERE (Email = ?);", new SQLParam(emailAddress, SQLType.VARCHAR));
		}catch (Exception e) {
			release();
			return AuthResult.INTERNAL_ERROR;
		}
		String salt = null;
		String hashed = null;
		try {
			while (results.next()) {
				salt = results.getString("PasswordSalt");
				userID = results.getInt("UserID");
				hashed = results.getString("Password");
				lastName = results.getString("LastName");
				firstName = results.getString("FirstName");
			}
		} catch (SQLException sqle) {
			return AuthResult.INTERNAL_ERROR;
		} finally {
			release(results);
			release();
		}
		if (userID == -1) return AuthResult.USER_NOT_FOUND;
		//Convert user's hashed password, which was a hex encoded string, to a byte array
		//Convert the DB's stored salt value to a char array and encode it as a byte array
		//Append the salt to the password hash and hash this value
		//Compare the resulting hash to the hash stored in the database
		byte []  byteSALT, saltedpwd;
		try {
			byteSALT = Hex.decodeHex(salt.toCharArray());
		} catch (DecoderException e) {return AuthResult.INTERNAL_ERROR;}
		saltedpwd = new byte[bytePWD.length + byteSALT.length];
		System.arraycopy(bytePWD, 0, saltedpwd, 0, bytePWD.length);
		System.arraycopy(byteSALT, 0, saltedpwd, bytePWD.length, byteSALT.length);
		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		}catch (Exception e ){ return AuthResult.INTERNAL_ERROR;}
		byte[] hashedpwd = digest.digest(saltedpwd);
		try {
			if (!Arrays.equals(hashedpwd, Hex.decodeHex(hashed.toCharArray()))) return AuthResult.INVALID_PWD;
		} catch (DecoderException e) {
			return AuthResult.INTERNAL_ERROR;
		}
		validated = true;
		return AuthResult.OK;	
	}

	
	public void produceToken() {
		if (!validated || userID == -1) return;
		token = Server.sessionTable().login(userID).getToken();
	}
	

}
