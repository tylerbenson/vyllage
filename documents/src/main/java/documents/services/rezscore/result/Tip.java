package documents.services.rezscore.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "tip", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Tip {

	public Tip() {
	}

	@XmlElement(name = "short")
	private String sshort;

	@XmlElement(name = "short")
	private String llong;

	public String getSshort() {
		return sshort;
	}

	public void setSshort(String sshort) {
		this.sshort = sshort;
	}

	public String getLlong() {
		return llong;
	}

	public void setLlong(String llong) {
		this.llong = llong;
	}
}
