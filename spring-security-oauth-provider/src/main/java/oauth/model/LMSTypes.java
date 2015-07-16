package oauth.model;

import lombok.ToString;

@ToString
public class LMSTypes {

	private Long lmsTypeId ;
	private String lmsTypeName ;
	
	public LMSTypes() {
	}
	
	public LMSTypes(Long lmsTypeId, String lmsTypeName) {
		super();
		this.lmsTypeId = lmsTypeId;
		this.lmsTypeName = lmsTypeName;
		
	}
	public Long getLmsTypeId() {
		return lmsTypeId;
	}
	public void setLmsTypeId(Long lmsTypeId) {
		this.lmsTypeId = lmsTypeId;
	}
	public String getLmsTypeName() {
		return lmsTypeName;
	}
	public void setLmsTypeName(String lmsTypeName) {
		this.lmsTypeName = lmsTypeName;
	}
}
