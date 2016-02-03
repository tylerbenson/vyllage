package documents.services.rezscore.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@XmlRootElement(name = "file", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class File implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5000992129761553127L;

	@XmlElement(name = "size")
	private Long size;

	@XmlElement(name = "encoding")
	private String encoding;

	@XmlElement(name = "extension")
	private String extension;

	@XmlElement(name = "lang")
	private String lang;

	public File() {
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
