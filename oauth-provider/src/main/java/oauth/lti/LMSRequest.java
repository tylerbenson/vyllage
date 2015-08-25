package oauth.lti;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import oauth.model.LMSAccount;
import oauth.model.LMSType;
import oauth.utilities.LMSConstants;
import oauth.utilities.LMSEnum;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import user.common.constants.RolesEnum;
import user.common.lms.LMSUser;

import com.newrelic.api.agent.NewRelic;

public class LMSRequest {

	final static Logger log = LoggerFactory.getLogger(LMSRequest.class);

	HttpServletRequest httpServletRequest;

	boolean complete = false;

	String rawUserRoles;
	Set<String> ltiUserRoles;
	int userRoleNumber;
	String rawUserRolesOverride;

	private LMSAccount lmsAccount;
	private LMSUser lmsUser;
	private String externalOrganizationId;

	public static synchronized LMSRequest getInstance() {

		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest req = sra.getRequest();
		if (req == null) {
			throw new IllegalStateException(
					LMSConstants.LTI_INVALID_HTTP_REQUEST);
		}
		LMSRequest ltiRequest = (LMSRequest) req.getAttribute(LMSRequest.class
				.getName());
		if (ltiRequest == null) {
			ltiRequest = new LMSRequest(req);
		}
		return ltiRequest;
	}

	public LMSRequest(HttpServletRequest request) {
		this.httpServletRequest = request;

		log.info(LMSConstants.LTI_VERSION + " "
				+ getParam(LMSConstants.LTI_VERSION));
		log.info(LMSConstants.LTI_INSTANCE_GUID + " "
				+ getParam(LMSConstants.LTI_INSTANCE_GUID));
		log.info(LMSConstants.LTI_CONSUMER_KEY + " "
				+ getParam(LMSConstants.LTI_CONSUMER_KEY));
		log.info(LMSConstants.LTI_INSTANCE_NAME + " "
				+ getParam(LMSConstants.LTI_INSTANCE_NAME));
		log.info(LMSConstants.LTI_INSTANCE_SERVER_ID + " "
				+ getParam(LMSConstants.LTI_INSTANCE_SERVER_ID));
		log.info(LMSConstants.LTI_INSTANCE_TYPE + " "
				+ getParam(LMSConstants.LTI_INSTANCE_TYPE));

		log.info(LMSConstants.LTI_LMS_VERSION + " "
				+ getParam(LMSConstants.LTI_LMS_VERSION));
		log.info(LMSConstants.LTI_OAUTH_VERSION + " "
				+ getParam(LMSConstants.LTI_OAUTH_VERSION));

		log.info(LMSConstants.LTI_USER_ID + " "
				+ getParam(LMSConstants.LTI_USER_ID));
		log.info(LMSConstants.LTI_USER_NAME + " "
				+ getParam(LMSConstants.LTI_USER_NAME));
		log.info(LMSConstants.LTI_USER_EMAIL + " "
				+ getParam(LMSConstants.LTI_USER_EMAIL));

		if (!isLTIRequest(request)) {
			throw new IllegalStateException(LMSConstants.LTI_INVALID_REQUEST);
		}
		processRequestParameters(request);

	}

	public String getParam(String paramName) {
		String value = null;
		if (this.httpServletRequest != null && paramName != null) {
			value = StringUtils.trimToNull(this.httpServletRequest
					.getParameter(paramName));
		}
		return value;
	}

	public boolean processRequestParameters(HttpServletRequest request) {

		if (request != null && this.httpServletRequest != request) {
			this.httpServletRequest = request;
		}
		lmsAccount = new LMSAccount();
		lmsUser = new LMSUser();

		if (getParam(LMSConstants.LTI_INSTANCE_GUID) != null) {
			externalOrganizationId = getParam(LMSConstants.LTI_INSTANCE_GUID);

			NewRelic.addCustomParameter("LTI_INSTANCE_GUID - EXT ORGANIZATION",
					getParam(LMSConstants.LTI_INSTANCE_GUID));

		} else if (getParam(LMSConstants.LTI_INSTANCE_SERVER_ID) != null) {

			externalOrganizationId = getParam(LMSConstants.LTI_INSTANCE_SERVER_ID);

			NewRelic.addCustomParameter("LTI_INSTANCE_GUID - EXT ORGANIZATION",
					getParam(LMSConstants.LTI_INSTANCE_SERVER_ID));

		} else {
			throw new IllegalStateException(LMSConstants.LTI_INVALID_SERVER_ID);
		}

		if (getParam(LMSConstants.LTI_INSTANCE_GUID) != null) {

			lmsAccount.setLmsGuid(getParam(LMSConstants.LTI_INSTANCE_GUID));

			NewRelic.addCustomParameter("LTI_INSTANCE_GUID",
					getParam(LMSConstants.LTI_INSTANCE_GUID));

		} else if (getParam(LMSConstants.LTI_INSTANCE_SERVER_ID) != null) {

			lmsAccount
					.setLmsGuid(getParam(LMSConstants.LTI_INSTANCE_SERVER_ID));

			NewRelic.addCustomParameter("LTI_INSTANCE_SERVER_ID",
					getParam(LMSConstants.LTI_INSTANCE_SERVER_ID));
		} else {
			throw new IllegalStateException(LMSConstants.LTI_INVALID_SERVER_ID);
		}
		lmsAccount.setExternalOrganizationId(externalOrganizationId);
		lmsAccount.setLtiVersion(getParam(LMSConstants.LTI_VERSION));

		// logging custom parameter for later
		NewRelic.addCustomParameter("LTI_INSTANCE_TYPE",
				getParam(LMSConstants.LTI_INSTANCE_TYPE));
		lmsAccount
				.setType(getLMSType(getParam(LMSConstants.LTI_INSTANCE_TYPE)));
		lmsAccount.setConsumerKey(getParam(LMSConstants.LTI_CONSUMER_KEY));

		lmsAccount.setOauthVersion(getParam(LMSConstants.LTI_OAUTH_VERSION));
		lmsAccount.setLmsVersion(getParam(LMSConstants.LTI_LMS_VERSION));

		lmsUser.setUserId(getParam(LMSConstants.LTI_USER_ID));
		lmsUser.setUserName(getParam(LMSConstants.LTI_USER_NAME));
		lmsUser.setEmail(getParam(LMSConstants.LTI_USER_EMAIL));

		if (getParam(LMSConstants.LIS_PERSON_PREFIX + "given") != null) {
			lmsUser.setFirstName(getParam(LMSConstants.LIS_PERSON_PREFIX
					+ "given"));
		}
		if (getParam(LMSConstants.LIS_PERSON_PREFIX + "family") != null) {
			lmsUser.setLastName(getParam(LMSConstants.LIS_PERSON_PREFIX
					+ "family"));
		}

		rawUserRoles = getParam(LMSConstants.LTI_USER_ROLES);
		userRoleNumber = makeUserRoleNum(rawUserRoles);
		String[] splitRoles = StringUtils.split(
				StringUtils.trimToEmpty(rawUserRoles), ",");
		ltiUserRoles = new HashSet<>(Arrays.asList(splitRoles));

		if (isRoleAdministrator()) {
			lmsUser.setRole(RolesEnum.ADMIN.name());
		} else if (isRoleInstructor()) {
			lmsUser.setRole(RolesEnum.INSTRUCTOR.name());
		} else if (isRoleLearner()) {
			lmsUser.setRole(RolesEnum.STUDENT.name());
		} else {
			lmsUser.setRole(RolesEnum.GUEST.name());
		}

		complete = checkCompleteLTIRequest();
		if (!complete) {
			throw new IllegalStateException(LMSConstants.LTI_INVALID_KEY);
		}
		HttpSession session = this.httpServletRequest.getSession();
		session.setAttribute(LMSConstants.LTI_USER_ID, lmsUser.getUserId());
		session.setAttribute(LMSConstants.LTI_USER_ROLE, lmsUser.getRole());
		return complete;
	}

	protected boolean checkCompleteLTIRequest() {
		return (lmsAccount.getConsumerKey() != null && lmsUser.getUserId() != null);
	}

	public boolean isRoleAdministrator() {
		return (rawUserRoles != null && userRoleNumber >= 2);
	}

	public boolean isRoleInstructor() {
		return (rawUserRoles != null && userRoleNumber >= 1);
	}

	public boolean isRoleLearner() {
		return (rawUserRoles != null && StringUtils.containsIgnoreCase(
				rawUserRoles, "learner"));
	}

	public static boolean isLTIRequest(ServletRequest request) {
		String ltiVersion = StringUtils.trimToNull(request
				.getParameter(LMSConstants.LTI_VERSION));

		if (ltiVersion != null) {
			return LMSConstants.LTI_VERSION_1P0.equals(ltiVersion);
		} else {
			log.warn(LMSConstants.LTI_VERSION + " not found in LTI request.");
		}
		return false;
	}

	public static String makeLTICompositeKey(HttpServletRequest request,
			String sessionSalt) {

		if (StringUtils.isBlank(sessionSalt)) {
			sessionSalt = "A7k254A0itEuQ9ndKJuZ";
		}
		String composite = sessionSalt + "::"
				+ request.getParameter(LMSConstants.LTI_CONSUMER_KEY) + "::"
				+ request.getParameter(LMSConstants.LTI_USER_ID) + "::"
				+ (System.currentTimeMillis() / 1800)
				+ request.getHeader("User-Agent") + "::"
				+ request.getContextPath();

		String compositeKey = DigestUtils.md5Hex(composite);
		return compositeKey;
	}

	public static int makeUserRoleNum(String rawUserRoles) {
		int roleNum = 0;
		if (rawUserRoles != null) {
			String lcRUR = rawUserRoles.toLowerCase();
			if (lcRUR.contains("administrator")) {
				roleNum = 2;
			} else if (lcRUR.contains("instructor")) {
				roleNum = 1;
			}
		}
		return roleNum;
	}

	private LMSType getLMSType(String lmsTypeString) {

		LMSType lmsType = new LMSType();
		if (lmsTypeString == null) {
			lmsType.setTypeId((long) LMSEnum.CUSTOM.getValue());
			lmsType.setLmsName(LMSEnum.CUSTOM.name());
			return lmsType;
		}

		LMSEnum lmsEnum = LMSEnum.valueOf(lmsTypeString.toUpperCase());

		switch (lmsEnum) {

		case BLACKBOARD:
			lmsType.setTypeId((long) LMSEnum.BLACKBOARD.getValue());
			lmsType.setLmsName(LMSEnum.BLACKBOARD.name());
			break;
		case SAKAI:
			lmsType.setTypeId((long) LMSEnum.SAKAI.getValue());
			lmsType.setLmsName(LMSEnum.SAKAI.name());
			break;
		case DESIRE2LEARN:
			lmsType.setTypeId((long) LMSEnum.DESIRE2LEARN.getValue());
			lmsType.setLmsName(LMSEnum.DESIRE2LEARN.name());
			break;
		case CANVAS:
			lmsType.setTypeId((long) LMSEnum.CANVAS.getValue());
			lmsType.setLmsName(LMSEnum.CANVAS.name());
			break;
		case MOODLE:
			lmsType.setTypeId((long) LMSEnum.MOODLE.getValue());
			lmsType.setLmsName(LMSEnum.MOODLE.name());
			break;
		default:
			lmsType.setTypeId((long) LMSEnum.CUSTOM.getValue());
			lmsType.setLmsName(LMSEnum.CUSTOM.name());
			break;
		}
		return lmsType;

	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public String getRawUserRoles() {
		return rawUserRoles;
	}

	public Set<String> getLtiUserRoles() {
		return ltiUserRoles;
	}

	public int getUserRoleNumber() {
		return userRoleNumber;
	}

	public boolean isComplete() {
		return complete;
	}

	/**
	 * @return the lmsAccount
	 */
	public LMSAccount getLmsAccount() {
		return lmsAccount;
	}

	/**
	 * @param lmsAccount
	 *            the lmsAccount to set
	 */
	public void setLmsAccount(LMSAccount lmsAccount) {
		this.lmsAccount = lmsAccount;
	}

	/**
	 * @return the lmsUser
	 */
	public LMSUser getLmsUser() {
		return lmsUser;
	}

	/**
	 * @param lmsUser
	 *            the lmsUser to set
	 */
	public void setLmsAccount(LMSUser lmsUser) {
		this.lmsUser = lmsUser;
	}
}
