package jobs.services.rezscore.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@XmlRootElement(name = "language", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class Language implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5277573378241244250L;

	@XmlElement(name = "word")
	private Word word;

	public Language() {
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

}
