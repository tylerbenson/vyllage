/**
 * This class is generated by jOOQ
 */
package editor.domain.public_.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.0"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DocumentsRecord extends org.jooq.impl.UpdatableRecordImpl<editor.domain.public_.tables.records.DocumentsRecord> implements org.jooq.Record5<java.lang.Long, java.lang.Long, java.lang.Boolean, java.sql.Timestamp, java.sql.Timestamp> {

	private static final long serialVersionUID = 1585272652;

	/**
	 * Setter for <code>PUBLIC.DOCUMENTS.ID</code>.
	 */
	public void setId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>PUBLIC.DOCUMENTS.ID</code>.
	 */
	public java.lang.Long getId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>PUBLIC.DOCUMENTS.ACCOUNTID</code>.
	 */
	public void setAccountid(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>PUBLIC.DOCUMENTS.ACCOUNTID</code>.
	 */
	public java.lang.Long getAccountid() {
		return (java.lang.Long) getValue(1);
	}

	/**
	 * Setter for <code>PUBLIC.DOCUMENTS.VISIBILITY</code>.
	 */
	public void setVisibility(java.lang.Boolean value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>PUBLIC.DOCUMENTS.VISIBILITY</code>.
	 */
	public java.lang.Boolean getVisibility() {
		return (java.lang.Boolean) getValue(2);
	}

	/**
	 * Setter for <code>PUBLIC.DOCUMENTS.DATECREATED</code>.
	 */
	public void setDatecreated(java.sql.Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>PUBLIC.DOCUMENTS.DATECREATED</code>.
	 */
	public java.sql.Timestamp getDatecreated() {
		return (java.sql.Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>PUBLIC.DOCUMENTS.LASTMODIFIED</code>.
	 */
	public void setLastmodified(java.sql.Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>PUBLIC.DOCUMENTS.LASTMODIFIED</code>.
	 */
	public java.sql.Timestamp getLastmodified() {
		return (java.sql.Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Long> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Long, java.lang.Long, java.lang.Boolean, java.sql.Timestamp, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Long, java.lang.Long, java.lang.Boolean, java.sql.Timestamp, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field1() {
		return editor.domain.public_.tables.Documents.DOCUMENTS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field2() {
		return editor.domain.public_.tables.Documents.DOCUMENTS.ACCOUNTID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Boolean> field3() {
		return editor.domain.public_.tables.Documents.DOCUMENTS.VISIBILITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field4() {
		return editor.domain.public_.tables.Documents.DOCUMENTS.DATECREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field5() {
		return editor.domain.public_.tables.Documents.DOCUMENTS.LASTMODIFIED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value2() {
		return getAccountid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Boolean value3() {
		return getVisibility();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value4() {
		return getDatecreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value5() {
		return getLastmodified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord value1(java.lang.Long value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord value2(java.lang.Long value) {
		setAccountid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord value3(java.lang.Boolean value) {
		setVisibility(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord value4(java.sql.Timestamp value) {
		setDatecreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord value5(java.sql.Timestamp value) {
		setLastmodified(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentsRecord values(java.lang.Long value1, java.lang.Long value2, java.lang.Boolean value3, java.sql.Timestamp value4, java.sql.Timestamp value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DocumentsRecord
	 */
	public DocumentsRecord() {
		super(editor.domain.public_.tables.Documents.DOCUMENTS);
	}

	/**
	 * Create a detached, initialised DocumentsRecord
	 */
	public DocumentsRecord(java.lang.Long id, java.lang.Long accountid, java.lang.Boolean visibility, java.sql.Timestamp datecreated, java.sql.Timestamp lastmodified) {
		super(editor.domain.public_.tables.Documents.DOCUMENTS);

		setValue(0, id);
		setValue(1, accountid);
		setValue(2, visibility);
		setValue(3, datecreated);
		setValue(4, lastmodified);
	}
}
