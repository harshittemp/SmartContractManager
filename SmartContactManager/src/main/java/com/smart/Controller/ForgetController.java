package com.smart.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.Enteties.User;
import com.smart.dao.UserRepository;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetController {
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private UserRepository ur;
	
	@Autowired
	private EmailService emailService;
	
	
	Random random=new Random(1000);
	@GetMapping("/forgot")
	public String openEmailForm()
	{
		
		
		return "forgot_email_form";
	}
	
	//send otp
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam String email,HttpSession session )
	{	//generating otp of 4 digit
		int otp=random.nextInt(999999);
	
		String subject="OTP From SCM";
		String message=""
				+ "<div style='border:1px solid #e2e2e2;padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>"+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		
		String to=email;
		
		boolean flag=this.emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else
		{
			
			session.setAttribute("message","Wrong email !!");
			
			return "forgot_email_form";
		}
		
		
	}
	
	@PostMapping("/verify-otp")
	public String VerifyOtp(@RequestParam int otp,HttpSession session,RedirectAttributes ra)
	{
		int myotp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email"); 
		
		
		if(myotp==otp)
		{
			User user=ur.getUserByUserName(email);
			
			if(user==null)
			{
				//send error msg
				
				session.setAttribute("message","User does not exist with this email !!");
				return "forgot_email_form";
				
			}
			else
			{
				return "password_change_form";
			}
			
			//password change form
		
		}
		else
		{
			session.setAttribute("message","Wrong OTP !!");
			
			
			return "verify_otp";
			
		}
		
		
	}
	
	//change-password handler
	
    @PostMapping("/change-password")
	public String changepassword(@RequestParam String newPassword,HttpSession session)
	{
    	
    	String email=(String)session.getAttribute("email");
    	User user=ur.getUserByUserName(email);
    	
    	user.setPassword(pe.encode(newPassword));
    	ur.save(user);
    
		return "redirect:/signin?change=Password Changed Successfully !!";
		
    	
		
	}

}
