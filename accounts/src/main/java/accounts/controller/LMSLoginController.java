package accounts.controller;

import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import oauth.lti.LMSRequest;

@Controller
public class LMSLoginController {

	@RequestMapping(value = "/lti", method = RequestMethod.POST)
    public String lti(HttpServletRequest req, Principal principal, Model model) {
    	
    	
        //req.getSession().setAttribute("login", "oauth");
        
        LMSRequest ltiRequest = LMSRequest.getInstance();
        if (ltiRequest != null) {
            
        	System.out.println("lti:             --> "+ true);
            System.out.println("ltiVersion:      --> "+ ltiRequest.getLtiVersion());
            System.out.println("UserDisplayName: --> "+ ltiRequest.getLtiContextId());
            System.out.println("ltiUser:         --> "+ ltiRequest.getLtiUserDisplayName());
            System.out.println("ltiLinkId:       --> "+ ltiRequest.getLtiLinkId());
            System.out.println("key:             --> "+ ltiRequest.getKey());
            System.out.println("consumerKey:     --> "+ ltiRequest.getLtiConsumerKey());
            System.out.println("ltiLinkId:       --> "+ ltiRequest.getLtiLinkId());
            
            System.out.println("userDisplayName: --> "+ ltiRequest.getLtiUserDisplayName());
            System.out.println("userEmail:       --> "+ ltiRequest.getLtiUserEmail());
            System.out.println("ltiUserId:       --> "+ ltiRequest.getLtiUserId());
            System.out.println("userRoles:       --> "+ ltiRequest.getLtiUserRoles());
            System.out.println("rawUserRoles:    --> "+ ltiRequest.getRawUserRoles());
            System.out.println("user:            --> "+ ltiRequest.getUser());
            System.out.println("userRoleNumber:  --> "+ ltiRequest.getUserRoleNumber());
            
        }
        return "login";
    }
}
