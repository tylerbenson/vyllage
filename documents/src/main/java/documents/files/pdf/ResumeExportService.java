package documents.files.pdf;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;

@Service
public class ResumeExportService {

	private final Logger logger = Logger.getLogger(ResumeExportService.class
			.getName());

	private final TemplateEngine templateEngine;

	private Cache<String, ByteArrayOutputStream> docs;

	@Inject
	public ResumeExportService(final TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
		docs = CacheBuilder.newBuilder().maximumSize(10)
				.expireAfterAccess(15, TimeUnit.MINUTES).build();
	}

	public ByteArrayOutputStream generatePDFDocument(
			@NonNull final DocumentHeader resumeHeader,
			@NonNull final List<DocumentSection> sections,
			@NonNull final String templateName) throws DocumentException {

		return this.getCachedDocument(resumeHeader, sections, templateName);
	}

	/**
	 * Generates a thumbnail of the resume in PNG using the selected style. <br>
	 * http://stackoverflow.com/questions/4929813/convert-pdf-to-thumbnail-image
	 * -in-java?lq=1
	 *
	 * @param resumeHeader
	 * @param sections
	 * @param templateName
	 * @param width
	 * @param height
	 * @return
	 * @throws DocumentException
	 */
	public ByteArrayOutputStream generatePNGDocument(
			final DocumentHeader resumeHeader,
			final List<DocumentSection> sections, final String templateName,
			int width, int height) throws DocumentException {

		if (width == 0 || height == 0) {
			width = 64 * 2;
			height = 98 * 2;
		}

		final ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();

		final ByteArrayOutputStream pdfBytes = this.getCachedDocument(
				resumeHeader, sections, templateName);

		try {

			ByteBuffer buf = ByteBuffer.wrap(pdfBytes.toByteArray());

			PDFFile pdffile = new PDFFile(buf);

			// draw the first page to an image
			PDFPage page = pdffile.getPage(0);

			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			BufferedImage bufferedImage = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);

			Image image = page.getImage(rect.width, rect.height, // width &
																	// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);
			FSImageWriter imageWritter = new FSImageWriter("png");

			Image scaledInstance = image.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);

			Graphics2D bufImageGraphics = bufferedImage.createGraphics();
			bufImageGraphics.drawImage(scaledInstance, 0, 0, null);

			ImageIO.write(bufferedImage, "png", imageByteArrayOutputStream);
			imageWritter.write(bufferedImage, imageByteArrayOutputStream);

			imageByteArrayOutputStream.close();

		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}
		return imageByteArrayOutputStream;

	}

	protected ByteArrayOutputStream getCachedDocument(
			final DocumentHeader documentHeader,
			final List<DocumentSection> documentSections,
			final String templateName) {

		final String key = this.getCacheKey(documentHeader, documentSections,
				templateName, ".pdf");

		ByteArrayOutputStream pdfBytes = null;

		try {
			pdfBytes = docs
					.get(key,
							() -> {

								final ITextRenderer renderer = preparePDF(
										documentHeader, documentSections,
										templateName);

								ByteArrayOutputStream out = new ByteArrayOutputStream();

								renderer.createPDF(out);
								renderer.finishPDF();
								out.flush();

								docs.put(key, out);

								return out;
							});

		} catch (ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return pdfBytes;
	}

	protected ITextRenderer preparePDF(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String templateName) {

		String htmlContent = generateHTMLDocument(resumeHeader, sections,
				templateName);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		try {
			renderer.getFontResolver().addFont("/documents/fonts/Lato 700.ttf",
					true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Lato regular.ttf", true);

			renderer.getFontResolver().addFont("/documents/fonts/Lora 700.ttf",
					true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Lora regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Merriweather 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Merriweather regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Montserrat regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Open Sans 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Open Sans regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Roboto 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Roboto regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Source Sans Pro 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Source Sans Pro regular.ttf", true);

		} catch (DocumentException | IOException e) {
			logger.severe(e.getMessage());
			NewRelic.noticeError(e);
		}

		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		return renderer;
	}

	protected String generateHTMLDocument(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String templateName) {
		Context ctx = new Context();

		format(resumeHeader);

		ctx.setVariable("header", resumeHeader);

		ctx.setVariable("sections", sections.stream().sorted(sortSections())
				.collect(Collectors.toList()));

		String htmlContent = templateEngine.process(templateName, ctx);
		return htmlContent;
	}

	/**
	 * Generates a key based on the hash of the resume header, sections and
	 * style name.
	 *
	 * @param resumeHeader
	 * @param sections
	 * @param styleName
	 * @return
	 */
	private String getCacheKey(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String styleName, String extension) {

		StringBuilder sb = new StringBuilder();
		sb.append(resumeHeader.hashCode()).append("-");

		if (!sections.isEmpty())
			sb.append(sections.hashCode()).append("-");

		sb.append(styleName).append(extension);

		return sb.toString();
	}

	/**
	 * Applies formatting to header phone number in Locale.US.
	 *
	 * @param resumeHeader
	 * @throws ParseException
	 */
	protected void format(DocumentHeader resumeHeader) {
		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		resumeHeader.setPhoneNumber(formatter.print(
				resumeHeader.getPhoneNumber(), Locale.US));
	}

	/**
	 * Sorts sections based on their sectionPosition value.
	 *
	 * @return sorted sections
	 */
	protected Comparator<? super DocumentSection> sortSections() {
		return (s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition());
	}

	public ByteArrayOutputStream generateDOCXDocument(
			DocumentHeader documentHeader,
			List<DocumentSection> documentSections, String templateName) {

		final String key = this.getCacheKey(documentHeader, documentSections,
				templateName, ".docx");

		ByteArrayOutputStream docxBytes = null;

		try {
			docxBytes = docs
					.get(key,
							() -> {

								String htmlContent = generateHTMLDocument(
										documentHeader, documentSections,
										templateName);

								WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
										.createPackage();

								// MainDocumentPart document = wordMLPackage
								// .getMainDocumentPart();
								// ObjectFactory factory =
								// org.docx4j.jaxb.Context
								// .getWmlObjectFactory();
								// Convert all styles to use a font that I know
								// is on my system
								// Styles styles = document
								// .getStyleDefinitionsPart()
								// .getJaxbElement();
								// for (org.docx4j.wml.Style s :
								// styles.getStyle()) {
								// RPr rPr = s.getRPr();
								// if (rPr == null) {
								// rPr = factory.createRPr();
								// s.setRPr(rPr);
								// }
								//
								// RFonts rf = rPr.getRFonts();
								// if (rf == null) {
								// rf = factory.createRFonts();
								// rPr.setRFonts(rf);
								// }
								//
								// rf.setAscii("Arial");
								// }
								PhysicalFonts
										.addPhysicalFonts(
												"Merriweather",
												new URL(
														"file:/usr/share/fonts/truetype/Merriweather/Merriweather-Regular.ttf"));
								// PhysicalFonts
								// .addPhysicalFont(new URL(
								// "file:/usr/share/fonts/truetype/Merriweather/Merriweather-Regular.ttf"));

								PhysicalFonts
										.addPhysicalFont(new URL(
												"file:/usr/share/fonts/truetype/Merriweather/Merriweather-Bold.ttf"));

								PhysicalFonts
										.addPhysicalFont(new URL(
												"file:/usr/share/fonts/truetype/Merriweather/Merriweather-Light.ttf"));
								PhysicalFonts.discoverPhysicalFonts();

								// Set up font mapper
								BestMatchingMapper fontMapper = new BestMatchingMapper();

								PhysicalFont font = PhysicalFonts
										.getPhysicalFonts().get("Merriweather");

								fontMapper.getFontMappings().put(
										"Merriweather", font);

								wordMLPackage.setFontMapper(fontMapper);

								NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
								wordMLPackage.getMainDocumentPart()
										.addTargetPart(ndp);
								ndp.unmarshalDefaultNumbering();

								XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(
										wordMLPackage);
								xHTMLImporter.setHyperlinkStyle("Hyperlink");

								// oddly this doesn't work for pdfs
								wordMLPackage
										.getMainDocumentPart()
										.getContent()
										.addAll(xHTMLImporter.convert(
												htmlContent,
												"http://localhost:8080/"));

								// logger.info(XmlUtils.marshaltoString(
								// wordMLPackage.getMainDocumentPart()
								// .getJaxbElement(), true, true));

								ByteArrayOutputStream out = new ByteArrayOutputStream();

								wordMLPackage.save(out);
								out.flush();

								docs.put(key, out);

								return out;
							});

		} catch (ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return docxBytes;
	}
}
