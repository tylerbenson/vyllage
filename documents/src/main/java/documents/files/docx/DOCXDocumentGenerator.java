package documents.files.docx;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.springframework.stereotype.Component;

import com.newrelic.api.agent.NewRelic;

@Component
public class DOCXDocumentGenerator {

	private final Logger logger = Logger.getLogger(DOCXDocumentGenerator.class
			.getName());

	@SuppressWarnings("deprecation")
	public ByteArrayOutputStream generateDOCXDocument(String htmlContent)
			throws MalformedURLException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
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
			PhysicalFonts
					.addPhysicalFont(

					new URL(
							"file:/usr/share/fonts/truetype/Open_Sans/OpenSans-Regular.ttf"));
			PhysicalFonts
					.addPhysicalFonts(
							"Open Sans",
							new URL(
									"file:/usr/share/fonts/truetype/Open_Sans/OpenSans-Regular.ttf"));

			// PhysicalFonts.discoverPhysicalFonts();

			// Set up font mapper
			BestMatchingMapper fontMapper = new BestMatchingMapper();

			PhysicalFont font = PhysicalFonts.getPhysicalFonts().get(
					"Merriweather");

			fontMapper.getFontMappings().put("Merriweather", font);

			wordMLPackage.setFontMapper(fontMapper);

			NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();

			wordMLPackage.getMainDocumentPart().addTargetPart(ndp);

			ndp.unmarshalDefaultNumbering();

			XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(
					wordMLPackage);
			xHTMLImporter.setHyperlinkStyle("Hyperlink");

			// oddly this doesn't work for pdfs
			wordMLPackage
					.getMainDocumentPart()
					.getContent()
					.addAll(xHTMLImporter.convert(htmlContent,
							"http://localhost:8080/"));

			// logger.info(XmlUtils.marshaltoString(
			// wordMLPackage.getMainDocumentPart()
			// .getJaxbElement(), true, true));

			wordMLPackage.save(out);
			out.flush();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return out;
	}

}
