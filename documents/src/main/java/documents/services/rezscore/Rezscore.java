package documents.services.rezscore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "rezscore", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Rezscore {

	@XmlElement(name = "score")
	private Score score;

	@XmlElement(name = "industry")
	private Industry industry;

	@XmlElement(name = "language")
	private Language language;

	@XmlElement(name = "advice")
	private Advice advice;

	@XmlElement(name = "file")
	private File file;

	@XmlElement(name = "extended")
	private Extended extended;

	@XmlElement(name = "text")
	private Text text;

	public Rezscore() {
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Advice getAdvice() {
		return advice;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Extended getExtended() {
		return extended;
	}

	public void setExtended(Extended extended) {
		this.extended = extended;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}
}
