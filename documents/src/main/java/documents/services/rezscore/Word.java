package documents.services.rezscore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "word", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Word {

	@XmlElement(name = "string")
	private String string;

	@XmlElement(name = "val")
	private Long val;

	public Word() {
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Long getVal() {
		return val;
	}

	public void setVal(long val) {
		this.val = val;
	}

}
