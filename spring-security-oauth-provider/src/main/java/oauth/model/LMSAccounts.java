package oauth.model;

import lombok.ToString;

@ToString
public class LMSAccounts {
	
	private Long lmsInstanceGuid ;
	private String lmsInstanceName ;
	
	public LMSAccounts() {
		
	}
	
	public LMSAccounts (Long lmsInstanceGuid, String lmsInstanceName) {
		super();
		this.lmsInstanceGuid = lmsInstanceGuid ;
		this.lmsInstanceName = lmsInstanceName ;
	}

	public Long getLmsInstanceGuid() {
		return lmsInstanceGuid;
	}

	public void setLmsInstanceGuid(Long lmsInstanceGuid) {
		this.lmsInstanceGuid = lmsInstanceGuid;
	}

	public String getLmsInstanceName() {
		return lmsInstanceName;
	}

	public void setLmsInstanceName(String lmsInstanceName) {
		this.lmsInstanceName = lmsInstanceName;
	}

	
	
}
