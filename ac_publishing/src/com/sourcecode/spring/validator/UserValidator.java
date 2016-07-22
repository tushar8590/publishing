package com.sourcecode.spring.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.sourcecode.spring.model.User;


@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
       return User.class.equals(clazz) ;
    }

    @Override
    public void validate(Object obj, Errors errors) {
       
        User user = (User) obj;
       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "user.username.empty");
       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.empty");
       /* if (user.getUserName().equals("")) {
            errors.rejectValue("userName","", "Username is not valid.");
        }*/
    }
    
}
