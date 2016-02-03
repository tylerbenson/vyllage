package documents.services.rezscore.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "language", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Language {

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
