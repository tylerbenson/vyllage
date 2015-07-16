package oauth.model;

import lombok.ToString;

@ToString
public class LMSAccountDetails {

	private Long lmsInstanceGuid ;
	private String lmsInstanceVersion ;
	private LMSTypes lmsType ;
	
	

	public LMSAccountDetails() {
		
	}
	
	public LMSAccountDetails(Long lmsInstanceGuid , String lmsInstanceVersion, LMSTypes lmsType) {
		super() ;
		this.lmsInstanceGuid = lmsInstanceGuid ;
		this.lmsInstanceVersion =  lmsInstanceVersion ;
		this.lmsType = lmsType ;
	}
	
	public Long getLmsInstanceGuid() {
		return lmsInstanceGuid;
	}

	public void setLmsInstanceGuid(Long lmsInstanceGuid) {
		this.lmsInstanceGuid = lmsInstanceGuid;
	}
	
	public String getLmsInstanceVersion() {
		return lmsInstanceVersion;
	}
	public void setLmsInstanceVersion(String lmsInstanceVersion) {
		this.lmsInstanceVersion = lmsInstanceVersion;
	}
	public LMSTypes getLmsType() {
		return lmsType;
	}
	public void setLmsType(LMSTypes lmsType) {
		this.lmsType = lmsType;
	}
}
