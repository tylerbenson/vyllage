/**
 * 
 */
package user.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kunal.shankar
 *
 */
@ToString
@EqualsAndHashCode
public class LMSType {

	private Long typeId;
	private String lmsName;
	private LocalDateTime dateCreated;
	private LocalDateTime lastModified;

	/**
	 * @return the typeId
	 */
	public Long getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId
	 *            the typeId to set
	 */
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
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

	/**
	 * @return the dateCreated
	 */
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the lastModified
	 */
	public LocalDateTime getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
}
