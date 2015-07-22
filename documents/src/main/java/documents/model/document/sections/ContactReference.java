package documents.model.document.sections;

import lombok.ToString;

@ToString
public class ContactReference {

	private String pictureUrl;
	private String name;
	String description;
	
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
