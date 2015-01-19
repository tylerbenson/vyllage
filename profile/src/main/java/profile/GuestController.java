package profile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GuestController {

	// http://localhost:8080/guest
    @RequestMapping("/guest")
    public String guest(){
        return "guest";
    }

}
