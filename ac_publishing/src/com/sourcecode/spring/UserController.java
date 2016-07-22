package com.sourcecode.spring;



import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.sourcecode.spring.model.User;
import com.sourcecode.spring.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@SessionAttributes("userAttribute")

public class UserController {
   
    @Autowired
    private UserService userService;
   
    
    @Autowired
    private Validator userValidator;
    
    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
      
    }
    
    @RequestMapping(value="/",method=RequestMethod.GET)
    public String homePage(Model model){
       model.addAttribute("tushar", "user.welcome");
        return "index";
    }
    
    @RequestMapping(value="login",method=RequestMethod.GET)
    public String authenticateUser(Model model){
       model.addAttribute("userAttribute", new User());
        return "login";
    }
    
    
    
    @RequestMapping(value="authenticate",method=RequestMethod.POST)
    public ModelAndView authenticateUser(@ModelAttribute("userAttribute") @Valid User user,BindingResult result,HttpServletRequest request){
    
        ModelAndView model = new ModelAndView();
        HttpSession session = request.getSession();
        if(result.hasErrors()){
            model.setViewName("login");
            return model;
        }
        if(!userService.isValidUser(user.getUserName())){
            model.addObject("userAttribute", user);
           // session.setAttribute("user", user);
            model.addObject("errorMsg","Invalid uname or pwd");
            model.setViewName("login");
            return model;
        }

         model.setViewName("redirect:loadDashboard");
        return model;
    }
    
   
    
    
    
    
}
