package oauth.vo;

import org.springframework.security.oauth.provider.BaseConsumerDetails;

import oauth.model.LMSAccounts;

@SuppressWarnings("serial")
public class LMSConsumerDetails extends BaseConsumerDetails {

	  private String displayName;
	  private String resourceName;
	  private String resourceDescription;
	  private LMSUserAccount lmsUser ;
	  private LMSAccounts lmsAccounts ;
	  
	  /**
	   * The display name of the consumer.
	   *
	   * @return The display name of the consumer.
	   */
	  public String getDisplayName() {
	    return displayName;
	  }

	  /**
	   * The display name of the consumer.
	   *
	   * @param displayName The display name of the consumer.
	   */
	  public void setDisplayName(String displayName) {
	    this.displayName = displayName;
	  }

	  /**
	   * The name of the resource that this consumer can be granted access to.
	   *
	   * @return The name of the resource that this consumer can be granted access to.
	   */
	  public String getResourceName() {
	    return resourceName;
	  }

	  /**
	   * The name of the resource that this consumer can be granted access to.
	   *
	   * @param resourceName The name of the resource that this consumer can be granted access to.
	   */
	  public void setResourceName(String resourceName) {
	    this.resourceName = resourceName;
	  }

	  /**
	   * The description of the resource that this consumer can be granted access to.
	   *
	   * @return The description of the resource that this consumer can be granted access to.
	   */
	  public String getResourceDescription() {
	    return resourceDescription;
	  }

	  /**
	   * The description of the resource that this consumer can be granted access to.
	   *
	   * @param resourceDescription The description of the resource that this consumer can be granted access to.
	   */
	  public void setResourceDescription(String resourceDescription) {
	    this.resourceDescription = resourceDescription;
	  }

	public LMSUserAccount getLmsUser() {
		return lmsUser;
	}

	public void setLmsUser(LMSUserAccount lmsUser) {
		this.lmsUser = lmsUser;
	}

	public LMSAccounts getLmsAccounts() {
		return lmsAccounts;
	}

	public void setLmsAccounts(LMSAccounts lmsAccounts) {
		this.lmsAccounts = lmsAccounts;
	}

	}
