package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.springframework.core.io.Resource;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.newrelic.api.agent.NewRelic;

public class PackageUserAgentCallback extends ITextUserAgent {

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
		try {
			InputStream in = resourceClass.getResourceAsStream(uri);
			
			if(in == null) {
				String error = "Image for " + uri + " not found. Replacing with blank image.";
				logger.severe(error);
				NewRelic.noticeError(error);
				in = resourceClass.getResourceAsStream("/documents/images/blank.png");
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numRead;
			byte[] buffer = new byte[256];
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}

			Image i = Image.getInstance(out.toByteArray());
			i.scalePercent(getImageResizePercent());

			ITextFSImage fsi = new ITextFSImage(i);
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
