package json;

import java.util.List;

public class JSONModels {
	@SuppressWarnings("unused")
	public static class RegisterReqJSON {
		private final String emailAddress;
		private final String password;
		private final String firstName;
		private final String lastName;
		public RegisterReqJSON (String emailAddress, String password, String firstName, String lastName) {
			this.emailAddress = emailAddress;
			this.password = password;
			this.firstName = firstName;
			this.lastName = lastName;
		}
	}
	@SuppressWarnings("unused")
	public static class LoginReqJSON {
		private final String emailAddress;
		private final String password;
		public LoginReqJSON (String emailAddress, String password) {
			this.emailAddress = emailAddress;
			this.password = password;
		}
	}
	
	public static class LoginResJSON {
		public String token;
	}
	@SuppressWarnings("unused")
	public static class HouseholdCreateReqJSON {
		private final String householdName;
		private final String householdDescription;
		public HouseholdCreateReqJSON(String name, String description ) {
			householdName = name;
			householdDescription = description;
		}
	}
	public static class HouseholdCreateResJSON {
		public int householdID;
	}
	
	@SuppressWarnings("unused")
	public static class LinkReqJSON {
		private final String description;
		private final String unitName;
		public LinkReqJSON(String description, String units) {
			this.description = description;
			this.unitName = units;
		}
	}
	@SuppressWarnings("unused")
	public static class ListCreateReqJSON {
		private final String listName;
		public ListCreateReqJSON (String name) {
			listName = name;
		}
	}
	public static class ListCreateResJSON {
		public int listID;
		public long version;
	}
	@SuppressWarnings("unused")
	public static class ListUpdateReqJSON {
		private long version;
		private List<ListUpdateItem> items;
		
		public ListUpdateReqJSON(long timestamp, List<ListUpdateItem> items) {
			this.version = timestamp;
			this.items = items;
		}
	}
	public static class ListUpdateItem {
		public String UPC;
		public int quantity;
		public int fractional = 0;
		
		public ListUpdateItem(String UPC, int quantity, int fractional) {
			this.UPC = UPC;
			this.quantity = quantity;
			this.fractional = fractional;
		}
	}
	public static class ListUpdateResJSON {
		public long timestamp;
	}
}
