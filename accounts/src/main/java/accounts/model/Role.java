package accounts.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;


import accounts.domain.tables.records.RolesRecord;

@ToString
@EqualsAndHashCode
public class Role {

	/**
	 * 
	 */
	private final String role;


	public Role(String role) {
		this.role = role;
	}
	
	public Role(RolesRecord record) {
		this.role = record.getRole();
	}


	public String getRole() {
		return role;
	}



}
