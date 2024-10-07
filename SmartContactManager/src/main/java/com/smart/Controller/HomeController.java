package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.Enteties.User;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;

import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
    private BCryptPasswordEncoder bpe;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute User user, BindingResult result1,
                               @RequestParam(defaultValue = "false") boolean agreement, Model model,
                               RedirectAttributes ra) {

        try {
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You have not agreed to the terms and conditions");
            }

            if (result1.hasErrors()) {
                System.out.println("ERROR " + result1.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImgeUrl("default.png");
            user.setPassword(bpe.encode(user.getPassword()));

            System.out.println("Agreement " + agreement);
            System.out.println("USER " + user);

            userRepository.save(user);
            ra.addFlashAttribute("message", new Message("Successfully Registered !!", "alert-success"));
            return "redirect:/signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            ra.addFlashAttribute("message", new Message("Something Went wrong !! " + e.getMessage(), "alert-danger"));
            return "redirect:/signup";
        }
    }
    @RequestMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}
}
