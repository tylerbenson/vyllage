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
public class Users extends org.jooq.impl.TableImpl<editor.domain.information_schema.tables.records.UsersRecord> {

	private static final long serialVersionUID = -2035667048;

	/**
	 * The reference instance of <code>INFORMATION_SCHEMA.USERS</code>
	 */
	public static final editor.domain.information_schema.tables.Users USERS = new editor.domain.information_schema.tables.Users();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<editor.domain.information_schema.tables.records.UsersRecord> getRecordType() {
		return editor.domain.information_schema.tables.records.UsersRecord.class;
	}

	/**
	 * The column <code>INFORMATION_SCHEMA.USERS.NAME</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.UsersRecord, java.lang.String> NAME = createField("NAME", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.USERS.ADMIN</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.UsersRecord, java.lang.String> ADMIN = createField("ADMIN", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.USERS.REMARKS</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.UsersRecord, java.lang.String> REMARKS = createField("REMARKS", org.jooq.impl.SQLDataType.VARCHAR.length(2147483647), this, "");

	/**
	 * The column <code>INFORMATION_SCHEMA.USERS.ID</code>.
	 */
	public final org.jooq.TableField<editor.domain.information_schema.tables.records.UsersRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>INFORMATION_SCHEMA.USERS</code> table reference
	 */
	public Users() {
		this("USERS", null);
	}

	/**
	 * Create an aliased <code>INFORMATION_SCHEMA.USERS</code> table reference
	 */
	public Users(java.lang.String alias) {
		this(alias, editor.domain.information_schema.tables.Users.USERS);
	}

	private Users(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.UsersRecord> aliased) {
		this(alias, aliased, null);
	}

	private Users(java.lang.String alias, org.jooq.Table<editor.domain.information_schema.tables.records.UsersRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, editor.domain.information_schema.InformationSchema.INFORMATION_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public editor.domain.information_schema.tables.Users as(java.lang.String alias) {
		return new editor.domain.information_schema.tables.Users(alias, this);
	}

	/**
	 * Rename this table
	 */
	public editor.domain.information_schema.tables.Users rename(java.lang.String name) {
		return new editor.domain.information_schema.tables.Users(name, null);
	}
}
