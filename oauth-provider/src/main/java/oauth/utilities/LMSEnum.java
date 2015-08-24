package oauth.utilities;

public enum LMSEnum {

	BLACKBOARD(0), SAKAI(1), DESIRE2LEARN(2), CANVAS(3), MOODLE(4), CUSTOM(5);

	private final int value;

	private LMSEnum(final int lmsId) {
		value = lmsId;
	}

	public int getValue() {
		return value;
	}
}
