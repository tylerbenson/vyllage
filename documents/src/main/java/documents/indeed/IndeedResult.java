package documents.indeed;

import lombok.ToString;

@ToString
public class IndeedResult {

	public IndeedResult() {
	}

	private String jobtitle;

	private String company;

	private String city;

	private String state;

	private String country;

	private String formattedLocation;

	private String source;

	private String date;

	private String snippet;

	private String url;

	private String onmousedown;

	private long latitude;

	private long longitude;

	private String jobkey;

	private boolean sponsored;

	private boolean expired;

	private boolean indeedApply;

	private String formattedLocationFull;

	private String formattedRelativeTime;

	private String noUniqueUrl;

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFormattedLocation() {
		return formattedLocation;
	}

	public void setFormattedLocation(String formattedLocation) {
		this.formattedLocation = formattedLocation;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOnmousedown() {
		return onmousedown;
	}

	public void setOnmousedown(String onmousedown) {
		this.onmousedown = onmousedown;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public String getJobkey() {
		return jobkey;
	}

	public void setJobkey(String jobkey) {
		this.jobkey = jobkey;
	}

	public boolean isSponsored() {
		return sponsored;
	}

	public void setSponsored(boolean sponsored) {
		this.sponsored = sponsored;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public boolean isIndeedApply() {
		return indeedApply;
	}

	public void setIndeedApply(boolean indeedApply) {
		this.indeedApply = indeedApply;
	}

	public String getFormattedLocationFull() {
		return formattedLocationFull;
	}

	public void setFormattedLocationFull(String formattedLocationFull) {
		this.formattedLocationFull = formattedLocationFull;
	}

	public String getFormattedRelativeTime() {
		return formattedRelativeTime;
	}

	public void setFormattedRelativeTime(String formattedRelativeTime) {
		this.formattedRelativeTime = formattedRelativeTime;
	}

	public String getNoUniqueUrl() {
		return noUniqueUrl;
	}

	public void setNoUniqueUrl(String noUniqueUrl) {
		this.noUniqueUrl = noUniqueUrl;
	}

}
