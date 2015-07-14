package oauth.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class LMSUser {

	private long userId;
    private String userSha256;
    private String userKey;
    private String displayName;
    private String email;


    protected LMSUser() {
    }
    public LMSUser(String userKey, Date loginAt) {
        if (loginAt == null) {
            loginAt = new Date();
        }
        this.userKey = userKey;
        this.userSha256 = makeSHA256(userKey);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserSha256() {
        return userSha256;
    }

    public void setUserSha256(String userSha256) {
        this.userSha256 = userSha256;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LMSUser that = (LMSUser) o;

        if (userId != that.userId) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (userKey != null ? !userKey.equals(that.userKey) : that.userKey != null) return false;
        if (userSha256 != null ? !userSha256.equals(that.userSha256) : that.userSha256 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) userId;
        result = 31 * result + (userSha256 != null ? userSha256.hashCode() : 0);
        result = 31 * result + (userKey != null ? userKey.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
    
    public static String makeSHA256(String text) {
        String encode = null;
        if (StringUtils.isNotBlank(text)) {
            encode = org.apache.commons.codec.digest.DigestUtils.sha256Hex(text);
        }
        return encode;
    }
}
