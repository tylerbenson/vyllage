package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.newrelic.api.agent.NewRelic;

public class PackageUserAgentCallback extends ITextUserAgent {

	private static final String DOCUMENTS_IMAGES_BLANK_PNG = "/documents/images/blank.png";

	private static Logger logger = Logger
			.getLogger(PackageUserAgentCallback.class.getName());

	private Class<Resource> resourceClass;

	private int imageResizePercent = 1000; // the original image had a very low
											// dpi...

	public PackageUserAgentCallback(ITextOutputDevice outputDevice,
			Class<Resource> resourceClass) {
		super(outputDevice);
		this.resourceClass = resourceClass;
	}

	@Override
	public ImageResource getImageResource(String uri) {

		if (StringUtils.trimToNull(uri) == null) {
			String error = "Invalid path provided. Replacing with blank image.";
			logger.severe(error);
			NewRelic.noticeError(error);
			uri = DOCUMENTS_IMAGES_BLANK_PNG;
		}

		try {
			InputStream in = resourceClass.getResourceAsStream(uri);

			if (in == null) {
				String error = "Image for " + uri
						+ " not found. Replacing with blank image.";
				logger.severe(error);
				NewRelic.noticeError(error);
				in = resourceClass
						.getResourceAsStream(DOCUMENTS_IMAGES_BLANK_PNG);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numRead;
			byte[] buffer = new byte[256];
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}

			Image image = Image.getInstance(out.toByteArray());
			image.scalePercent(getImageResizePercent());

			ITextFSImage fsi = new ITextFSImage(image);
			return new ImageResource(uri, fsi);

		} catch (BadElementException | IOException | NullPointerException e) {
			logger.severe(e.getMessage());
			NewRelic.noticeError(e);
		}
		return null;
	}

	public int getImageResizePercent() {
		return imageResizePercent;
	}

	public void setImageResizePercent(int imageResizePercent) {
		this.imageResizePercent = imageResizePercent;
	}

}
