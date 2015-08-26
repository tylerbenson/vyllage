package oauth.utilities;

public class LMSConstants {

	public static String OAUTH_KEY = "spring.oauth.consumerKey";
	public static String OAUTH_SECRET = "spring.oauth.consumerSecret";
	public static String ROLE_OAUTH = "ROLE_OAUTH";
	public static String ROLE_LTI = "ROLE_LTI";
	public static String SAMPLE_NAME = "Sample";
	public static String ROLE_USER = "ROLE_USER";
	public static String ROLE_ADMIN = "ROLE_ADMIN";
	public static String USER_NAME = "username";
	public static String ADMIN_USER = "admin";

	public static final String LTI_INSTANCE_GUID = "tool_consumer_instance_guid";
	public static final String LTI_INSTANCE_SERVER_ID = "ext_sakai_serverid";
	public static final String LTI_INSTANCE_NAME = "tool_consumer_instance_name";
	public static final String LTI_INSTANCE_TYPE = "custom_lms_type";
	public static final String LTI_CONSUMER_KEY = "oauth_consumer_key";
	public static final String LTI_VERSION = "lti_version";
	public static final String LTI_VERSION_1P0 = "LTI-1p0";
	public static final String LTI_OAUTH_VERSION = "oauth_version";
	public static final String LTI_LMS_VERSION = "tool_consumer_info_version";

	public static final String LTI_USER_ID = "user_id";
	public static final String LTI_USER_NAME = "custom_user_id";
	public static final String LTI_USER_EMAIL = "lis_person_contact_email_primary";
	public static final String LIS_PERSON_PREFIX = "lis_person_name_";

	public static final String LTI_USER_ROLES = "roles";
	public static final String LTI_USER_ROLE = "user_role";

	// LMS Error Message
	public static final String LTI_INVALID_HTTP_REQUEST = "HttpServletRequest can't be null";
	public static final String LTI_INVALID_REQUEST = "Not a valid LTI request";
	public static final String LTI_INVALID_KEY = "LTI request doesn't have Consumer Key or/and LMS user id. ";
	public static final String LTI_INVALID_SERVER_ID = "LTI Request doesn't have valid server Id.";
	public static final String LTI_INVALID_LTIREQ = "Cannot create authentication for LTI because the LTIRequest is null";
	public static final String LTI_INVALID_HTTPSER_REQ = "LTI request MUST be an HttpServletRequest (cannot only be a ServletRequest)";
	public static final String LTI_INVALID_LMS_USER = "LTI request doesn't have LMS and User details..";
	public static final String LTI_INVALID_USER = "LTI request doesn't have LMS user details";
	public static final String LTI_INVALID_LMS = "LTI request doesn't have LMS detail";
	public static final String LTI_INVALID_LMS_INSTANCE = "LTI request doesn't have LMS instance Id";

}
