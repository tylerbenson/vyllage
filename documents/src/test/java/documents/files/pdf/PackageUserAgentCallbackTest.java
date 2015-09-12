package documents.files.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.resource.ImageResource;

@RunWith(MockitoJUnitRunner.class)
public class PackageUserAgentCallbackTest {

	@Test
	public void testGetImageResourceUriNotFoundBlankImage() {

		ITextOutputDevice device = Mockito.mock(ITextOutputDevice.class);
		PackageUserAgentCallback call = new PackageUserAgentCallback(device,
				Resource.class);

		ImageResource imageResource = call.getImageResource("uri");

		Assert.assertNotNull(imageResource);
	}

	@Test
	public void testGetImageResourceNullUriReturnsBlankImage() {

		ITextOutputDevice device = Mockito.mock(ITextOutputDevice.class);
		PackageUserAgentCallback call = new PackageUserAgentCallback(device,
				Resource.class);

		ImageResource imageResource = call.getImageResource(null);

		Assert.assertNotNull(imageResource);
	}

	@Test
	public void testGetImageResourceBlankUriReturnsBlankImage() {

		ITextOutputDevice device = Mockito.mock(ITextOutputDevice.class);
		PackageUserAgentCallback call = new PackageUserAgentCallback(device,
				Resource.class);

		ImageResource imageResource = call.getImageResource("   ");

		Assert.assertNotNull(imageResource);
	}
}
