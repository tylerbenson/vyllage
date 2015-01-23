package profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileController {

	// http://localhost:8080/profile
    @RequestMapping("/profile")
    public String profile(){ //@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		//        model.addAttribute("name", name);
        return "main";
    }

	// http://localhost:8080/main
    @RequestMapping("/main")
    public String main(){
        return "redirect:/profile";
    }
}
