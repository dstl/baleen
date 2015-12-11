//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Utility class for identity related functionality, such as hashing strings and getting internal IDs
 * 
 * 
 *
 */
public class IdentityUtils {
	private static final IdentityUtils INSTANCE = new IdentityUtils();
	private AtomicLong id = new AtomicLong(1L);
	
	private IdentityUtils(){
		// Private constructor as all our functions should be static in a Utils class
	}
	
	/**
	 * Get the singleton instance of IdentityUtils
	 */
	public static IdentityUtils getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Get a new ID that can be used as an internal ID.
	 * The ID will be unique within the current runtime of Baleen
	 */
	public long getNewId(){
		return id.getAndIncrement();
	}
	
	/**
	 * Create a SHA-256 hash from one or more strings
	 * 
	 * @param strings The strings to concatenate and hash
	 * @return The hashed string
	 * @throws BaleenException If a MessageDigest instance cannot be retrieved for the SHA-256 algorithm
	 */
	public static String hashStrings (String... strings) throws BaleenException{
		
		StringBuilder hash = new StringBuilder();
		
		StringBuilder concat = new StringBuilder();
		for(String s : strings){
			if(s != null)
				concat.append(s);
		} 
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			
			byte[] buffer = concat.toString().getBytes(StandardCharsets.UTF_8);
			md.update(buffer);
				
			byte[] digest = md.digest();
			for(int i = 0; i < digest.length; i++){
				hash.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			}
		}
		catch (NoSuchAlgorithmException e) {
			throw new BaleenException("Can't get MessageDigest instance for constructing hashes", e);
		}

		return hash.toString();
	}

}
