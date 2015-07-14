package oauth.controller;

import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import oauth.lti.LMSRequest;

@Controller
@RequestMapping("/lti")
public class LTIController {

	 	@RequestMapping({"", "/"})
	    public String home(HttpServletRequest req, Principal principal, Model model) {
	    	System.out.println("--------------------lti-----------------");
	    	model.addAttribute("today", new Date());
	        model.addAttribute("basicUser", "admin");
	        model.addAttribute("basicPass", "admin");
	        model.addAttribute("oauthKey", "key");
	        model.addAttribute("oauthSecret", "secret");
	        model.addAttribute("req", req);
	        model.addAttribute("reqURI", req.getMethod() + " " + req.getRequestURI());
	        model.addAttribute("username", principal != null ? principal.getName() : "ANONYMOUS");
	        model.addAttribute("name", "lti");	        
	        req.getSession().setAttribute("login", "oauth");
	        
	        LMSRequest ltiRequest = LMSRequest.getInstance();
	        if (ltiRequest != null) {
	            model.addAttribute("lti", true);
	            model.addAttribute("ltiVersion", ltiRequest.getLtiVersion());
	            model.addAttribute("ltiContext", ltiRequest.getLtiContextId());
	            model.addAttribute("ltiUser", ltiRequest.getLtiUserDisplayName());
	            model.addAttribute("ltiLink", ltiRequest.getLtiLinkId());
	        }
	        return "home"; 
	    }
}
