package login.controller;

import login.model.BatchAccount;
import login.repository.GroupRepository;
import login.repository.UserDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("admin/account")
public class AdminAccountController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String admin(Model model) {

		model.addAttribute("groups", groupRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
		return "main";
	}

	@RequestMapping(value = "/createBatch", method = RequestMethod.POST)
	public void batchAccountCreation(BatchAccount batch) {
		System.out.println(batch);

	}

}
