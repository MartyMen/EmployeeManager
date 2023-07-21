package com.example.EmployeeApp.Controller;

import com.example.EmployeeApp.Repository.UserRepository;
import com.example.EmployeeApp.Services.AuthProvider;
import com.example.EmployeeApp.dbobjects.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository uRepo;
    @Autowired
    private AuthProvider authProvider;

    @GetMapping({"/register"})
    public ModelAndView registerScreen() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new Users());
        return mav;
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute Users user, BindingResult result) {
        System.out.println("saveUser method called");

        if (result.hasErrors()) {
            System.out.println("Binding errors: " + result.getAllErrors());
            return "register";
        }

        if (!user.getPassword().equals(user.getConfirmationPassword())) {
            result.rejectValue("confirmationPassword", "error.user", "Passwords do not match");
            return "register";
        }
        // create RegisterRequest object
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(user.getEmail());
        registerRequest.setPassword(user.getPassword());

        // call authProvider.register()
        AuthenticationResponse response = authProvider.register(registerRequest);
        // if needed, use the response data
        return "redirect:/login";
    }
}