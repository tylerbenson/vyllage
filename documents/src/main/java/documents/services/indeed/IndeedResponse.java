package documents.services.indeed;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class IndeedResponse {
	public IndeedResponse() {
	}

	private int version;

	private String query;

	private String location;

	private boolean dupefilter;

	private boolean highlight;

	private int start;

	private int end;

	private String radius;

	private int totalResults;

	private int pageNumber;

	private String error;

	private List<IndeedResult> results = new ArrayList<>();

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isDupefilter() {
		return dupefilter;
	}

	public void setDupefilter(boolean dupefilter) {
		this.dupefilter = dupefilter;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public List<IndeedResult> getResults() {
		return results;
	}

	public void setResults(List<IndeedResult> results) {
		this.results = results;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
