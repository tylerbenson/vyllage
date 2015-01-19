package login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	// http://localhost:8080/login
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

	// http://localhost:8080/expire
    @RequestMapping("/expire")
    public String expire() {
        return "expire";
    }
}
