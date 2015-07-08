package oauth.model;

import lombok.ToString;

@ToString
public class LMSUserRole {
	
	private Long roleId ;
	private String roleName ;
	
	public LMSUserRole() {
		
	}
	
	public LMSUserRole (Long roleId , String roleName) {
		super() ;
		this.roleId =  roleId ;
		this.roleName = roleName ;
	}
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
