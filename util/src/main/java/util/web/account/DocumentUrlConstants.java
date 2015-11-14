package util.web.account;

public interface DocumentUrlConstants {

	public final String RESUME_EXPORT = "export";

	public final String RESUME_DELETE = "delete";

	public final String REDIRECT_RESUME = "redirect:/resume/";

	public final String RESUME = "resume";

	public final String FILE_PDF_STYLES = "/file/pdf/styles";

	public final String DOCUMENT_ID_FILE_PDF = "{documentId}/file/pdf";

	public final String DOCUMENT_ID_FILE_PNG = "{documentId}/file/png";

	public final String DOCUMENT_ID_SECTION = "{documentId}/section";

	public final String DOCUMENT_ID_SECTION_SECTION_ID = "{documentId}/section/{sectionId}";

	public final String DOCUMENT_ID_SECTION_ORDER = "{documentId}/section-order";

	public final String DOCUMENT_ID_HEADER = "{documentId}/header";

	public final String DOCUMENT_ID_RECENT_USERS = "{documentId}/recent-users";

	public final String DOCUMENT_ID_SECTION_SECTION_ID_COMMENT = "{documentId}/section/{sectionId}/comment";

	public final String DOCUMENT_ID_SECTION_SECTION_ID_COMMENT_COMMENT_ID = "{documentId}/section/{sectionId}/comment/{commentId}";

	public final String DOCUMENT_ID_SECTION_SECTION_ID_ADVICE = "{documentId}/section/{sectionId}/advice";

	public final String DOCUMENT_ID_SECTION_SECTION_ID_ADVICE_SECTION_ADVICE_ID = "{documentId}/section/{sectionId}/advice/{sectionAdviceId}";

	// NOTIFICATIONS

	public final String NOTIFICATION_ALL = "/all";

	public final String NOTIFICATION_COMMENT = "/comment";

	public final String NOTIFICATION_REQUEST_FEEDBACK = "/request-feedback";

	public final String NOTIFICATION_REQUEST_REFERENCE = "/request-reference";

	public final String NOTIFICATION_DELETE_ALL = "/delete";

	// REFERENCES

	public final String REFERENCE_REQUEST = "/request";

	public final String REFERENCE_ACCEPT = "/accept";

	public final String REFERENCE_REJECT = "/reject";

}
