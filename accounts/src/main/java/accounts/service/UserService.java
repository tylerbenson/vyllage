package accounts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import accounts.model.BatchAccount;
import accounts.model.CSRFToken;
import accounts.model.Organization;
import accounts.model.OrganizationMember;
import accounts.model.Role;
import accounts.model.User;
import accounts.model.UserFilterRequest;
import accounts.model.UserRole;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.OrganizationMemberRepository;
import accounts.repository.OrganizationRepository;
import accounts.repository.OrganizationRoleRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;
import accounts.repository.UserRoleRepository;
import accounts.validation.EmailValidator;

@Service
public class UserService {
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	@Autowired
	private DocumentService documentService;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationRoleRepository organizationRoleRepository;

	@Autowired
	private OrganizationMemberRepository organizationMemberRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private AccountSettingRepository settingRepository;

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public List<User> getAllUsers() {
		return this.userRepository.getAll();
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
	}

	public void batchCreateUsers(BatchAccount batchAccount)
			throws IllegalArgumentException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getOrganization());
		Assert.notNull(batchAccount.getRole());
		Assert.notNull(batchAccount.getEmails());

		String[] emailSplit = batchAccount.getEmails()
				.replace(";", System.lineSeparator())
				.replace(",", System.lineSeparator()).trim()
				.split(System.lineSeparator());

		boolean invalid = Arrays.stream(emailSplit).map(String::trim)
				.map(EmailValidator::validate).collect(Collectors.toList())
				.stream().anyMatch(p -> p.booleanValue() != true);

		if (invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		Role role = roleRepository.get(batchAccount.getRole());

		List<User> users = Arrays
				.stream(emailSplit)
				.map(String::trim)
				.map(s -> new User(s, s, enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, Arrays
								.asList(new UserRole(role.getRole(), s)),
						Arrays.asList(new OrganizationMember(batchAccount
								.getOrganization(), null))))
				.collect(Collectors.toList());

		userRepository.saveUsers(users);
	}

	/**
	 * Process a link request, if the user doesn't exist, creates one with a
	 * random password.
	 * 
	 * @param linkRequest
	 * @return link response
	 */
	public User createUser(String userName) {
		boolean invalid = false;

		if (EmailValidator.validate(userName) == invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		// assigns current user's Organizations
		// assigns default role.
		// TODO: add random password for account
		if (!userRepository.userExists(userName)) {
			User user = new User(userName, userName, true, true, true, true,
					userRoleRepository
							.getDefaultAuthoritiesForNewUser(userName),
					((User) SecurityContextHolder.getContext()
							.getAuthentication().getPrincipal())
							.getOrganizationMember());
			userRepository.createUser(user);
		}
		User loadUserByUsername = userRepository.loadUserByUsername(userName);

		return loadUserByUsername;
	}

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

	public List<User> getAdvisors(UserFilterRequest filter, User loggedUser,
			int maxsize) {
		return userRepository.getAdvisors(filter, loggedUser, maxsize);
	}

	public List<User> getAdvisors(User loggedUser, int maxsize) {
		return userRepository.getAdvisors(loggedUser, maxsize);
	}

	public List<AccountNames> getNames(List<Long> userIds) {
		return userRepository.getNames(userIds);
	}

	public void update(User user) {
		userRepository.updateUser(user);
	}

	public List<AccountSetting> getAccountSettings(User user) {
		return settingRepository.getAccountSettings(user);
	}

	public List<AccountSetting> getAccountSetting(final User user,
			final String settingName) throws ElementNotFoundException {
		assert settingName != null;

		return settingRepository.get(user.getUserId(), settingName);
	}

	public AccountSetting setAccountSetting(final User user,
			AccountSetting setting) {

		if (setting.getUserId() == null)
			setting.setUserId(user.getUserId());

		switch (setting.getName()) {
		case "firstName":
			setFirstName(user, setting);
			return settingRepository.set(user.getUserId(), setting);

		case "middleName":
			setMiddleName(user, setting);
			return settingRepository.set(user.getUserId(), setting);

		case "lastName":
			setLastName(user, setting);
			return settingRepository.set(user.getUserId(), setting);
		default:
			return settingRepository.set(user.getUserId(), setting);
		}
	}

	public List<AccountSetting> setAccountSettings(final User user,
			List<AccountSetting> settings) {

		List<AccountSetting> savedSettings = new ArrayList<>();

		savedSettings.addAll(settings
				.stream()
				.filter(set -> !set.getName().equalsIgnoreCase("role")
						|| !set.getName().equalsIgnoreCase("organization"))
				.map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList()));

		// set organizations
		savedSettings.addAll(setOrganizations(
				user,
				settings.stream()
						.filter(set -> set.getName().equalsIgnoreCase(
								"organization")).collect(Collectors.toList())));

		// set roles
		savedSettings.addAll(setRoles(
				user,
				settings.stream()
						.filter(set -> set.getName().equalsIgnoreCase("role"))
						.collect(Collectors.toList())));

		return settings;
	}

	private List<AccountSetting> setRoles(User user,
			List<AccountSetting> newRoles) {

		// create new role relationship
		List<UserRole> userRoles = newRoles.stream()
				.map(set -> new UserRole(set.getValue(), user.getUsername()))
				.collect(Collectors.toList());

		// save user roles
		// authorities are immutable in Spring's user details.

		User user2 = new User(user.getUserId(), user.getFirstName(),
				user.getMiddleName(), user.getLastName(), user.getUsername(),
				user.getPassword(), user.isEnabled(),
				user.isAccountNonExpired(), user.isCredentialsNonExpired(),
				user.isAccountNonLocked(), userRoles,
				user.getOrganizationMember(), user.getDateCreated(),
				user.getLastModified());

		this.update(user2);

		// delete current roles, they will be replaced with the new
		// ones.
		settingRepository.deleteByName(user.getUserId(), "role");

		// saving and returning the new settings
		return newRoles.stream().map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList());
	}

	protected List<AccountSetting> setOrganizations(User user,
			List<AccountSetting> newOrganizations) {

		// create new organization relationship
		List<OrganizationMember> organizationMembers = newOrganizations
				.stream()
				.map(set -> new OrganizationMember(organizationRepository
						.getByName(set.getValue()).getOrganizationId(), user
						.getUserId())).collect(Collectors.toList());

		// save user organizations
		user.setOrganizationMember(organizationMembers);

		this.update(user);

		// delete current organizations, they will be replaced with the new
		// ones.
		settingRepository.deleteByName(user.getUserId(), "organization");

		// saving and returning the new settings
		return newOrganizations.stream()
				.map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList());
	}

	/**
	 * Returns the list of organizations a given user belongs too.
	 * 
	 * @param user
	 * @return
	 */
	protected List<Organization> getOrganizationsForUser(User user) {
		return organizationMemberRepository.getByUserId(user.getUserId())
				.stream()
				.map(om -> organizationRepository.get(om.getOrganizationId()))
				.collect(Collectors.toList());
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setFirstName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setFirstName(setting.getValue());
			this.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setMiddleName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setMiddleName(setting.getValue());
			this.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setLastName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setLastName(setting.getValue());
			this.update(user);
		}
	}

	public void changePassword(String newPassword) {
		userRepository.changePassword(newPassword);
	}

	/**
	 * Returns account contact information
	 */
	public List<AccountContact> getAccountContactForUsers(List<Long> userIds) {
		List<AccountSetting> accountSettings = settingRepository
				.getAccountSettings(userIds);

		if (accountSettings == null || accountSettings.isEmpty())
			return Arrays.asList();

		Map<Long, List<AccountSetting>> map = accountSettings.stream().collect(
				Collectors.groupingBy((AccountSetting as) -> as.getUserId(),
						Collectors.mapping((AccountSetting as) -> as,
								Collectors.toList())));

		return map.entrySet().stream().map(UserService::mapAccountContact)
				.collect(Collectors.toList());
	}

	/**
	 * Maps a list of contact related account settings into a contact object.
	 * 
	 * @param entry
	 * @return
	 */
	protected static AccountContact mapAccountContact(
			Entry<Long, List<AccountSetting>> entry) {
		AccountContact ac = new AccountContact();

		Long userId = entry.getKey();
		Optional<AccountSetting> address = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("address"))
				.findFirst();
		Optional<AccountSetting> email = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("email"))
				.findFirst();
		Optional<AccountSetting> phoneNumber = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("phoneNumber"))
				.findFirst();
		Optional<AccountSetting> twitter = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("twitter"))
				.findFirst();
		Optional<AccountSetting> linkedIn = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("linkedIn"))
				.findFirst();

		ac.setUserId(userId);

		if (address.isPresent())
			ac.setAddress(address.get().getValue());

		if (email.isPresent())
			ac.setEmail(email.get().getValue());

		if (phoneNumber.isPresent())
			ac.setPhoneNumber(phoneNumber.get().getValue());

		if (twitter.isPresent())
			ac.setTwitter(twitter.get().getValue());

		if (linkedIn.isPresent())
			ac.setLinkedIn(linkedIn.get().getValue());

		return ac;
	}

	/**
	 * Deletes a user and logs him out of the system.
	 * 
	 * @param request
	 * 
	 * @param userId
	 * @param token
	 * @throws ServletException
	 * @throws UserNotFoundException
	 */
	public void delete(HttpServletRequest request, Long userId, CSRFToken token)
			throws ServletException, UserNotFoundException {
		User user = userRepository.get(userId);

		logger.info("Disabling user.");
		userRepository.deleteUser(user.getUsername());

		logger.info("Deleting user documents.");
		documentService.deleteUsers(request, Arrays.asList(userId), token);

		logger.info("Have a nice day.");
		request.logout(); // good bye :)

	}

}
