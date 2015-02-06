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
public class Locks extends org.jooq.impl.TableImpl<editor.domain.information_schema.tables.records.LocksRecord> {

	private static final long serialVersionUID = 735654666;

	/**
	 * The reference instance of <code>INFORMATION_SCHEMA.LOCKS</code>
	 */
	public static final editor.domain.information_schema.tables.Locks LOCKS = new editor.domain.information_schema.tables.Locks();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<editor.domain.information_schema.tables.records.LocksRecord> getRecordType() {
		return editor.domain.information_schema.tables.records.LocksRecord.class;
	}

	/**
	 * The column <code>INFORMATION_SCHEMA.LOCKS.TABLE_SCHEMA</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.LocksRecord, java.lang.String> TABLE_SCHEMA = createField("TABLE_SCHEMA", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.LOCKS.TABLE_NAME</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.LocksRecord, java.lang.String> TABLE_NAME = createField("TABLE_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.LOCKS.SESSION_ID</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.LocksRecord, java.lang.Integer> SESSION_ID = createField("SESSION_ID", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.LOCKS.LOCK_TYPE</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.LocksRecord, java.lang.String> LOCK_TYPE = createField("LOCK_TYPE", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * Create a <code>INFORMATION_SCHEMA.LOCKS</code> table reference
	 */
	public Locks() {
		this("LOCKS", null);
	}

	/**
	 * Create an aliased <code>INFORMATION_SCHEMA.LOCKS</code> table reference
	 */
	public Locks(java.lang.String alias) {
		this(alias, editor.domain.information_schema.tables.Locks.LOCKS);
	}

	private Locks(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.LocksRecord> aliased) {
		this(alias, aliased, null);
	}

	private Locks(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.LocksRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, editor.domain.information_schema.InformationSchema.INFORMATION_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public editor.domain.information_schema.tables.Locks as(java.lang.String alias) {
		return new editor.domain.information_schema.tables.Locks(alias, this);
	}

	/**
	 * Rename this table
	 */
	public editor.domain.information_schema.tables.Locks rename(java.lang.String name) {
		return new editor.domain.information_schema.tables.Locks(name, null);
	}
}
