package accounts.model.link;

import lombok.ToString;

@ToString
public class LinkStat {

	private Long total = 0L;
	private Long totalAccess = 0L;
	private String mostAccessedDocument = "";
	private Long linksWithNoAccess = 0L;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getTotalAccess() {
		return totalAccess;
	}

	public void setTotalAccess(Long totalAccess) {
		this.totalAccess = totalAccess;
	}

	public String getMostAccessedDocument() {
		return mostAccessedDocument;
	}

	public void setMostAccessedDocument(String mostAccessedDocument) {
		this.mostAccessedDocument = mostAccessedDocument;
	}

	public Long getLinksWithNoAccess() {
		return linksWithNoAccess;
	}

	public void setLinksWithNoAccess(Long linksWithNoAccess) {
		this.linksWithNoAccess = linksWithNoAccess;
	}

}
