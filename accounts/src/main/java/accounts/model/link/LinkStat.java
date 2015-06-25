package accounts.model.link;

import lombok.ToString;

@ToString
public class LinkStat {

	private Long total;
	private Long totalAccess;
	private Long mostAccessedDocument;
	private Long linksWithNoAccess;

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

	public Long getMostAccessedDocument() {
		return mostAccessedDocument;
	}

	public void setMostAccessedDocument(Long mostAccessedDocument) {
		this.mostAccessedDocument = mostAccessedDocument;
	}

	public Long getLinksWithNoAccess() {
		return linksWithNoAccess;
	}

	public void setLinksWithNoAccess(Long linksWithNoAccess) {
		this.linksWithNoAccess = linksWithNoAccess;
	}

}
