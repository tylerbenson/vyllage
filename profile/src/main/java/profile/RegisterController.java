package profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegisterController {

	// http://localhost:8080/register
    @RequestMapping("/register")
    public String register(){
        return "register";
    }

}
