package oauth.utilities;

public enum LMSEnum {

	BLACKBOARD (1) , SAKAI (2), DESIRE2LEARN (3), CANVAS (4), MOODLE (5), CUSTOM (6) ;
	
	private final int value;

    private LMSEnum(final int lmsId) {
        value = lmsId;
    }

    public int getValue() { return value; }
}
