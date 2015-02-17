package login.controller;

import login.model.BatchAccount;
import login.repository.GroupRepository;
import login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("admin")
public class AdminUserController {
	@Autowired
	private UserService service;

	@Autowired
	private GroupRepository groupRepository;

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String admin(Model model) {

		prepareBatch(model);
		return "adminAccountManagement";
	}

	@RequestMapping(value = "/user/createBatch", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String batchAccountCreation(BatchAccount batch, Model model) {

		if (batch.hasErrors()) {
			model.addAttribute("groups", groupRepository.getAll());
			model.addAttribute("batchAccount", batch);
			return "adminAccountManagement";
		}

		service.batchCreateUsers(batch);

		prepareBatch(model);
		return "adminAccountManagement";
	}

	private void prepareBatch(Model model) {
		model.addAttribute("groups", groupRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
	}
}
