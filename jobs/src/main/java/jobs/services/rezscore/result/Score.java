package jobs.services.rezscore.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@XmlRootElement(name = "score", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class Score implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5085944045195976170L;

	@XmlElement(name = "grade")
	private String grade;

	@XmlElement(name = "grade_headline")
	private String gradeHeadline;

	@XmlElement(name = "grade_blurb")
	private String gradeBlurb;

	@XmlElement(name = "percentile")
	private Double percentile;

	@XmlElement(name = "percentile_suffix")
	private Double percentileSuffix;

	@XmlElement(name = "normal_img")
	private String normalImg;

	@XmlElement(name = "brevity_score")
	private Double brevityScore;

	@XmlElement(name = "impact_score")
	private Double impactScore;

	@XmlElement(name = "depth_score")
	private Double depthScore;

	@XmlElement(name = "email")
	private Double email;

	@XmlElement(name = "phone")
	private Double phone;

	@XmlElement(name = "job_keywords")
	private String jobKeywords;

	@XmlElement(name = "rez_id")
	private String rezId;

	public Score() {
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGradeHeadline() {
		return gradeHeadline;
	}

	public void setGradeHeadline(String gradeHeadline) {
		this.gradeHeadline = gradeHeadline;
	}

	public String getGradeBlurb() {
		return gradeBlurb;
	}

	public void setGradeBlurb(String gradeBlurb) {
		this.gradeBlurb = gradeBlurb;
	}

	public Double getPercentile() {
		return percentile;
	}

	public void setPercentile(Double percentile) {
		this.percentile = percentile;
	}

	public Double getPercentileSuffix() {
		return percentileSuffix;
	}

	public void setPercentileSuffix(Double percentileSuffix) {
		this.percentileSuffix = percentileSuffix;
	}

	public String getNormalImg() {
		return normalImg;
	}

	public void setNormalImg(String normalImg) {
		this.normalImg = normalImg;
	}

	public Double getBrevityScore() {
		return brevityScore;
	}

	public void setBrevityScore(Double brevityScore) {
		this.brevityScore = brevityScore;
	}

	public Double getImpactScore() {
		return impactScore;
	}

	public void setImpactScore(Double impactScore) {
		this.impactScore = impactScore;
	}

	public Double getDepthScore() {
		return depthScore;
	}

	public void setDepthScore(Double depthScore) {
		this.depthScore = depthScore;
	}

	public Double getEmail() {
		return email;
	}

	public void setEmail(Double email) {
		this.email = email;
	}

	public Double getPhone() {
		return phone;
	}

	public void setPhone(Double phone) {
		this.phone = phone;
	}

	public String getJobKeywords() {
		return jobKeywords;
	}

	public void setJobKeywords(String jobKeywords) {
		this.jobKeywords = jobKeywords;
	}

	public String getRezId() {
		return rezId;
	}

	public void setRezId(String rezId) {
		this.rezId = rezId;
	}

}
