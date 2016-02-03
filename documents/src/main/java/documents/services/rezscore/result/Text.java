package documents.services.rezscore.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "text", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Text {

	@XmlElement(name = "binlink")
	private String binLink;

	@XmlElement(name = "htmllink")
	private String htmlLink;

	@XmlElement(name = "txtlink")
	private String txtLink;

	public Text() {
	}

	public String getBinLink() {
		return binLink;
	}

	public void setBinLink(String binLink) {
		this.binLink = binLink;
	}

	public String getHtmlLink() {
		return htmlLink;
	}

	public void setHtmlLink(String htmlLink) {
		this.htmlLink = htmlLink;
	}

	public String getTxtLink() {
		return txtLink;
	}

	public void setTxtLink(String txtLink) {
		this.txtLink = txtLink;
	}

}
