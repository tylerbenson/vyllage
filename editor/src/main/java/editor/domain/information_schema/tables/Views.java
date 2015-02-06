/**
 * This class is generated by jOOQ
 */
package editor.domain.information_schema.tables;

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
public class Views extends org.jooq.impl.TableImpl<editor.domain.information_schema.tables.records.ViewsRecord> {

	private static final long serialVersionUID = -729466596;

	/**
	 * The reference instance of <code>INFORMATION_SCHEMA.VIEWS</code>
	 */
	public static final editor.domain.information_schema.tables.Views VIEWS = new editor.domain.information_schema.tables.Views();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<editor.domain.information_schema.tables.records.ViewsRecord> getRecordType() {
		return editor.domain.information_schema.tables.records.ViewsRecord.class;
	}

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.TABLE_CATALOG</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> TABLE_CATALOG = createField("TABLE_CATALOG", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.TABLE_SCHEMA</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> TABLE_SCHEMA = createField("TABLE_SCHEMA", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.TABLE_NAME</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> TABLE_NAME = createField("TABLE_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.VIEW_DEFINITION</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> VIEW_DEFINITION = createField("VIEW_DEFINITION", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.CHECK_OPTION</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> CHECK_OPTION = createField("CHECK_OPTION", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.IS_UPDATABLE</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> IS_UPDATABLE = createField("IS_UPDATABLE", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.STATUS</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> STATUS = createField("STATUS", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.REMARKS</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.String> REMARKS = createField("REMARKS", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.VIEWS.ID</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.ViewsRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>INFORMATION_SCHEMA.VIEWS</code> table reference
	 */
	public Views() {
		this("VIEWS", null);
	}

	/**
	 * Create an aliased <code>INFORMATION_SCHEMA.VIEWS</code> table reference
	 */
	public Views(java.lang.String alias) {
		this(alias, editor.domain.information_schema.tables.Views.VIEWS);
	}

	private Views(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.ViewsRecord> aliased) {
		this(alias, aliased, null);
	}

	private Views(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.ViewsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, editor.domain.information_schema.InformationSchema.INFORMATION_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public editor.domain.information_schema.tables.Views as(java.lang.String alias) {
		return new editor.domain.information_schema.tables.Views(alias, this);
	}

	/**
	 * Rename this table
	 */
	public editor.domain.information_schema.tables.Views rename(java.lang.String name) {
		return new editor.domain.information_schema.tables.Views(name, null);
	}
}
