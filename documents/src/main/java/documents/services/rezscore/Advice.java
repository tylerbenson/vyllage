package documents.services.rezscore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "advice", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Advice {

	@XmlElement(name = "tip")
	private Tip tip;

	public Tip getTip() {
		return tip;
	}

	public void setTip(Tip tip) {
		this.tip = tip;
	}

}
