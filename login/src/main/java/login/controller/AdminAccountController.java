package login.controller;

import login.model.BatchAccount;
import login.repository.GroupRepository;
import login.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("admin")
public class AdminAccountController {
	@Autowired
	private AccountService service;

	@Autowired
	private GroupRepository groupRepository;

	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String admin(Model model) {

		model.addAttribute("groups", groupRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
		return "adminAccountManagement";
	}

	@RequestMapping(value = "/account/createBatch", method = RequestMethod.POST)
	public String batchAccountCreation(BatchAccount batch, Model model) {

		service.batchCreateUsers(batch);

		model.addAttribute("groups", groupRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
		return "adminAccountManagement";
	}
}
