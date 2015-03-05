package accounts.model;

public class GroupMember {
	private Long group_id;
	private String userName;

	public GroupMember() {
	}

	public GroupMember(Long group_id, String userName) {
		this.group_id = group_id;
		this.userName = userName;
	}

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
