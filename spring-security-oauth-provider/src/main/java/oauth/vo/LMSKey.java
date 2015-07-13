package oauth.vo;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Set;

public class LMSKey  {
    private long keyId;
    
    private String keySha256;
    
    private String keyKey;
    
    private String secret;

    protected LMSKey() {
    }

    
    public LMSKey(String key, String secret) {
        this.keyKey = key;
        this.keySha256 = makeSHA256(key);
        if (StringUtils.isNotBlank(secret)) {
            this.secret = secret;
        }
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getKeySha256() {
        return keySha256;
    }

    public void setKeySha256(String keySha256) {
        this.keySha256 = keySha256;
    }

    public String getKeyKey() {
        return keyKey;
    }

    public void setKeyKey(String keyKey) {
        this.keyKey = keyKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public int hashCode() {
        int result = (int) keyId;
        result = 31 * result + (keySha256 != null ? keySha256.hashCode() : 0);
        result = 31 * result + (keyKey != null ? keyKey.hashCode() : 0);
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