package oauth.model;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class LMSType implements Serializable {

	private static final long serialVersionUID = 1193198267897218151L;

	private Long typeId;
	private String lmsName;

	public LMSType() {
	}

	public LMSType(Long typeId, String lmsName) {
		super();
		this.setTypeId(typeId);
		this.setLmsName(lmsName);

	}

	/**
	 * @return the typeId
	 */
	public Long getTypeId() {
		return typeId;
	}

	/**
	 * @param i
	 *            the typeId to set
	 */
	public void setTypeId(Long i) {
		this.typeId = i;
	}

	/**
	 * @return the lmsName
	 */
	public String getLmsName() {
		return lmsName;
	}

	/**
	 * @param lmsName
	 *            the lmsName to set
	 */
	public void setLmsName(String lmsName) {
		this.lmsName = lmsName;
	}

}
