package documents.services.rezscore.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@XmlRootElement(name = "industry", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class Industry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5896084970579869652L;

	@XmlElement(name = "first_industry_match")
	private String firstIndustryMatch;

	@XmlElement(name = "first_industry_conf")
	private Long firstIndustryConf;

	@XmlElement(name = "second_industry_match")
	private String secondIndustryMatch;

	@XmlElement(name = "second_industry_conf")
	private Long secondIndustryConf;

	@XmlElement(name = "second_industry_match")
	private String thirdIndustryMatch;

	@XmlElement(name = "third_industry_match")
	private Long thirdIndustryConf;

	public Industry() {
	}

	public String getFirstIndustryMatch() {
		return firstIndustryMatch;
	}

	public void setFirstIndustryMatch(String firstIndustryMatch) {
		this.firstIndustryMatch = firstIndustryMatch;
	}

	public Long getFirstIndustryConf() {
		return firstIndustryConf;
	}

	public void setFirstIndustryConf(Long firstIndustryConf) {
		this.firstIndustryConf = firstIndustryConf;
	}

	public String getSecondIndustryMatch() {
		return secondIndustryMatch;
	}

	public void setSecondIndustryMatch(String secondIndustryMatch) {
		this.secondIndustryMatch = secondIndustryMatch;
	}

	public Long getSecondIndustryConf() {
		return secondIndustryConf;
	}

	public void setSecondIndustryConf(Long secondIndustryConf) {
		this.secondIndustryConf = secondIndustryConf;
	}

	public String getThirdIndustryMatch() {
		return thirdIndustryMatch;
	}

	public void setThirdIndustryMatch(String thirdIndustryMatch) {
		this.thirdIndustryMatch = thirdIndustryMatch;
	}

	public Long getThirdIndustryConf() {
		return thirdIndustryConf;
	}

	public void setThirdIndustryConf(Long thirdIndustryConf) {
		this.thirdIndustryConf = thirdIndustryConf;
	}
}
