package core;

public final class SessionRecord {
	private final int userID;
	private final long expirationTime;
	public SessionRecord(int userID, int expiration) {
		this.userID = userID;
		expirationTime = (long)(expiration) + System.currentTimeMillis() / 1000L;
	}
	
	public final int getUserID() {
		return userID;
	}
	
	public final long getExpiration() {
		return expirationTime;
	}
	@Override
	public String toString() {
		return "" + userID + ":" + expirationTime;
	}
	
	public final boolean isExpired() {
		return System.currentTimeMillis() / 1000L > expirationTime;
	}
}
