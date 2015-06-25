package accounts.model.link;

import lombok.ToString;

@ToString
public class LinkStats {
	private LinkStat socialStats;
	private LinkStat emailStats;
	private LinkStat totalStats;

	public LinkStat getSocialStats() {
		return socialStats;
	}

	public void setSocialStats(LinkStat socialStats) {
		this.socialStats = socialStats;
	}

	public LinkStat getEmailStats() {
		return emailStats;
	}

	public void setEmailStats(LinkStat emailStats) {
		this.emailStats = emailStats;
	}

	public LinkStat getTotalStats() {
		return totalStats;
	}

	public void setTotalStats(LinkStat totalStats) {
		this.totalStats = totalStats;
	}

}
