package oauth.lti;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import oauth.vo.LMSKey;
import oauth.vo.LMSUser;

public class LMSRequest {

    final static Logger log = LoggerFactory.getLogger(LMSRequest.class);

    static final String LIS_PERSON_PREFIX = "lis_person_name_";
    public static final String LTI_CONSUMER_KEY = "oauth_consumer_key";
    public static final String LTI_CONTEXT_ID = "context_id";
    public static final String LTI_LINK_ID = "resource_link_id";
    
    
    public static final String LTI_USER_ID = "user_id";
    public static final String LTI_USER_EMAIL = "lis_person_contact_email_primary";
    public static final String LTI_USER_NAME_FULL = LIS_PERSON_PREFIX + "full";
    public static final String LTI_USER_ROLES = "roles";
    public static final String LTI_USER_ROLE = "user_role";
    public static final String LTI_VERSION = "lti_version";
    public static final String LTI_VERSION_1P0 = "LTI-1p0";
    public static final String LTI_ROLE_GENERAL = "user";
    public static final String LTI_ROLE_LEARNER = "learner";
    public static final String LTI_ROLE_INSTRUCTOR = "instructor";
    public static final String LTI_ROLE_ADMIN = "administrator";

    HttpServletRequest httpServletRequest;

    LMSKey key;
    LMSUser user;
    
    boolean complete = false;
    boolean updated = false;

    String ltiContextId;
    String ltiConsumerKey;
    String ltiLinkId;
    String ltiUserId;
    String ltiUserEmail;
    String ltiUserDisplayName;
    String rawUserRoles;
    Set<String> ltiUserRoles;
    int userRoleNumber;
    String rawUserRolesOverride;
    String ltiVersion;


    public static synchronized LMSRequest getInstance() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        if (req == null) {
            throw new IllegalStateException("HttpServletRequest can't be null");
        }
        LMSRequest ltiRequest = (LMSRequest) req.getAttribute(LMSRequest.class.getName());
        if (ltiRequest == null) {
            ltiRequest = new LMSRequest(req);
        }
        if (ltiRequest == null) {
            throw new IllegalStateException("Invalid LTI request");
        }
        return ltiRequest;
    }
    
    public LMSRequest(HttpServletRequest request) {
    	
        this.httpServletRequest = request;
        if (!isLTIRequest(request)) {
            throw new IllegalStateException("Not a LTI request");
        }
        processRequestParameters(request);
        
    }
    public String getParam(String paramName) {
        String value = null;
        if (this.httpServletRequest != null && paramName != null) {
            value = StringUtils.trimToNull(this.httpServletRequest.getParameter(paramName));
        }
        return value;
    }

    public boolean processRequestParameters(HttpServletRequest request) {
        if (request != null && this.httpServletRequest != request) {
            this.httpServletRequest = request;
        }
        ltiVersion = getParam(LTI_VERSION);
        ltiConsumerKey = getParam(LTI_CONSUMER_KEY);
        ltiLinkId = getParam(LTI_LINK_ID);
        ltiUserId = getParam(LTI_USER_ID);
        complete = checkCompleteLTIRequest(false);
        
        ltiUserEmail = getParam(LTI_USER_EMAIL);
        rawUserRoles = getParam(LTI_USER_ROLES);
        userRoleNumber = makeUserRoleNum(rawUserRoles);
        String[] splitRoles = StringUtils.split(StringUtils.trimToEmpty(rawUserRoles), ",");
        ltiUserRoles = new HashSet<>(Arrays.asList(splitRoles));

        if (getParam(LTI_USER_NAME_FULL) != null) {
            ltiUserDisplayName = getParam(LTI_USER_NAME_FULL);
        } else if (getParam(LIS_PERSON_PREFIX + "given") != null && getParam(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given") + " " + getParam(LIS_PERSON_PREFIX + "family");
        } else if (getParam(LIS_PERSON_PREFIX + "given") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given");
        } else if (getParam(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "family");
        }
       
        HttpSession session = this.httpServletRequest.getSession();
        session.setAttribute(LTI_USER_ID, ltiUserId);
        session.setAttribute(LTI_CONTEXT_ID, ltiContextId);
        String normalizedRoleName = LTI_ROLE_GENERAL;
       
        if (isRoleAdministrator()) {
            normalizedRoleName = LTI_ROLE_ADMIN;
        } else if (isRoleInstructor()) {
            normalizedRoleName = LTI_ROLE_INSTRUCTOR;
        } else if (isRoleLearner()) {
            normalizedRoleName = LTI_ROLE_LEARNER;
        }
        session.setAttribute(LTI_USER_ROLE, normalizedRoleName);
        return complete;
    }

    protected boolean checkCompleteLTIRequest(boolean objects) {
        if (!objects && ltiConsumerKey != null && ltiContextId != null  && ltiUserId != null) {
            complete = true;
        } else {
            complete = false;
        }
        return complete;
    }


    public boolean isRoleAdministrator() {
        return (rawUserRoles != null && userRoleNumber >= 2);
    }

    public boolean isRoleInstructor() {
        return (rawUserRoles != null && userRoleNumber >= 1);
    }

    public boolean isRoleLearner() {
        return (rawUserRoles != null && StringUtils.containsIgnoreCase(rawUserRoles, "learner"));
    }
    public static boolean isLTIRequest(ServletRequest request) {
        boolean valid = false;
        String ltiVersion = StringUtils.trimToNull(request.getParameter(LTI_VERSION));
        
        if (ltiVersion != null) {            
            boolean goodLTIVersion = LTI_VERSION_1P0.equals(ltiVersion);
            valid = goodLTIVersion;
        }
        return valid;
    }

    public static String makeLTICompositeKey(HttpServletRequest request, String sessionSalt) {
        if (StringUtils.isBlank(sessionSalt)) {
            sessionSalt = "A7k254A0itEuQ9ndKJuZ";
        }
        String composite = sessionSalt + "::" + request.getParameter(LTI_CONSUMER_KEY) + "::" + request.getParameter(LTI_CONTEXT_ID) + "::" +
        request.getParameter(LTI_LINK_ID) + "::" + request.getParameter(LTI_USER_ID) + "::" + (System.currentTimeMillis() / 1800) +
        request.getHeader("User-Agent") + "::" + request.getContextPath();
        return DigestUtils.md5Hex(composite);
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

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public String getLtiContextId() {
        return ltiContextId;
    }

    public String getLtiConsumerKey() {
        return ltiConsumerKey;
    }
    
    public String getLtiUserId() {
        return ltiUserId;
    }

    public String getLtiVersion() {
        return ltiVersion;
    }

    public LMSKey getKey() {
        return key;
    }
    public LMSUser getUser() {
        return user;
    }
    public String getLtiUserEmail() {
        return ltiUserEmail;
    }

    public String getLtiUserDisplayName() {
        return ltiUserDisplayName;
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

    public boolean isUpdated() {
        return updated;
    }
    public String getLtiLinkId() {
        return ltiLinkId;
    }
}