package documents.services.rezscore.result;

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
	private Long percentile;

	@XmlElement(name = "percentile_suffix")
	private Long percentileSuffix;

	@XmlElement(name = "normal_img")
	private String normalImg;

	@XmlElement(name = "brevity_score")
	private Long brevityScore;

	@XmlElement(name = "impact_score")
	private Long impactScore;

	@XmlElement(name = "depth_score")
	private Long depthScore;

	@XmlElement(name = "email")
	private Long email;

	@XmlElement(name = "phone")
	private Long phone;

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

	public Long getPercentile() {
		return percentile;
	}

	public void setPercentile(Long percentile) {
		this.percentile = percentile;
	}

	public Long getPercentileSuffix() {
		return percentileSuffix;
	}

	public void setPercentileSuffix(Long percentileSuffix) {
		this.percentileSuffix = percentileSuffix;
	}

	public String getNormalImg() {
		return normalImg;
	}

	public void setNormalImg(String normalImg) {
		this.normalImg = normalImg;
	}

	public Long getBrevityScore() {
		return brevityScore;
	}

	public void setBrevityScore(Long brevityScore) {
		this.brevityScore = brevityScore;
	}

	public Long getImpactScore() {
		return impactScore;
	}

	public void setImpactScore(Long impactScore) {
		this.impactScore = impactScore;
	}

	public Long getDepthScore() {
		return depthScore;
	}

	public void setDepthScore(Long depthScore) {
		this.depthScore = depthScore;
	}

	public Long getEmail() {
		return email;
	}

	public void setEmail(Long email) {
		this.email = email;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
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
