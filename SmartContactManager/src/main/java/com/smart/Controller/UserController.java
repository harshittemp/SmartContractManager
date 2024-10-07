package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import com.cloudinary.Cloudinary;
import com.smart.service.CloudanirayImageService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.smart.Enteties.Contact;
import com.smart.Enteties.MyOrder;
import com.smart.Enteties.User;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private CloudanirayImageService cloudanirayImageService;

	@Autowired
	private UserRepository ur;
	@Autowired
	private ContactRepository cr;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	
	@Autowired
	private MyOrderRepository or;
	
	//method for adding common datat to response
	@ModelAttribute
	public void addCommonData(Model m,Principal principal)
	{
		String uname=principal.getName();
User user=ur.getUserByUserName(uname);
		
		m.addAttribute("user", user);
	}
	
	
	//dashboard home
	
	@GetMapping("/index")
	public String Dashboard(Model m ,Principal principal)
	{
		
		String uname=principal.getName();
		User user=ur.getUserByUserName(uname);
		m.addAttribute("title","Home");
		m.addAttribute("user", user);
		return "normal/user_dashboard";
	}
	//open add form handler
	
	@GetMapping("/add-contact")
	public String openAddFormHandler(Model m)
	{
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,
			  RedirectAttributes ra)
	{
		try
		{ //success msg
			
			String name=principal.getName();
			User user=this.ur.getUserByUserName(name);
			
			//processing and uploading file
			if(file.isEmpty())
			{ 
				System.out.println("Empty");
				contact.setImage("contact.png");
			}
			else
			{
				//upload the file to folder and update name in contact
				
				contact.setImage(file.getOriginalFilename());
				
				//File saveFile=new ClassPathResource("/static/image").getFile();
				//Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
			//Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			
			Map data=this.cloudanirayImageService.upload(file);

				contact.setImage(data.get("url").toString());
			}
			/*
			If two files have the same name and you want to handle this situation, you need a strategy to ensure that file names are unique when saving them. Here are a few approaches you can consider:

1. Append a Unique Identifier
You can append a unique identifier (such as a timestamp or a UUID) to the file name to make it unique:

java
Copy code
else
{
    try {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename; // or use UUID.randomUUID().toString()

        contact.setImage(uniqueFilename);

        File saveFile = new ClassPathResource("/static/image").getFile();
        Path path = Paths.get(saveFile.getAbsolutePath(), uniqueFilename);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
        e.printStackTrace(); // Handle the exception
    }
}
			 */
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.ur.save(user);
			
		
			
			//adding success msg 
	ra.addFlashAttribute("message", new Message("Your Contact is added !!", "alert-success"));
		
			
		}
		catch (Exception e)
		{
			//error msg
			//adding success msg 
			ra.addFlashAttribute("message", new Message("Something went Wrong!!", "alert-danger"));
			
			
		}return "redirect:/user/add-contact"; // or another valid redirect URL

	}
	
	//handler  for  showing contact
	//showing 5 contacts now 5[n]
	//current page=0
	@GetMapping("show-contacts/{page}")
	public String showContacts(@PathVariable Integer page ,Model m,Principal principal)  //principal se nikal sakte hai
	{
		m.addAttribute("title", "Show Contacts");
		
		// to send contact list
		
		String email=principal.getName();
		User user=this.ur.getUserByUserName(email);
		
		Pageable pageable= PageRequest.of(page, 5);
		
		Page<Contact> contacts=this.cr.findContactByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
	
		return "normal/show_contacts";
	}
	
	
	
	//show contact detail of a users contact
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable Integer cId, Model m,Principal principal)
	{
		Optional<Contact> contactOptional=this.cr.findById(cId);
		Contact contact=contactOptional.get();
		
		
		//
		String name=principal.getName();
		User user=this.ur.getUserByUserName(name);
		
		if(user.getId()==contact.getUser().getId())
		{
			m.addAttribute("title",contact.getName());
			m.addAttribute("contact", contact);
		}
		
		
		
		
		
		return "normal/contact_details";
	}
	
	
	
	//delete contact 
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable Integer cId,Principal principal,RedirectAttributes ra)
	{
		String name=principal.getName();
		User user=this.ur.getUserByUserName(name);
		
		Optional<Contact> co=this.cr.findById(cId);
	Contact contact=co.get();
	if(user.getId()==contact.getUser().getId())
	{
		
		
		//contact.setUser(null);//unlink 
        //this.cr.delete(contact);
		
		user.getContacts().remove(contact);
		this.ur.save(user);
		
		
		
	ra.addFlashAttribute("message", new Message("Deleted Successfully !!","alert-success"));
	
	
	}	
	return "redirect:/user/show-contacts/0";
	}
	
	
	// update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable Integer cId,Model m)
	{  
		
		m.addAttribute("title", "Update Contact");
		Optional<Contact> oc= this.cr.findById(cId);
		Contact contact=oc.get();
		m.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	//process update handler
	
	@PostMapping("/process-update")
	public String updateProcess(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,Model m,RedirectAttributes ra,Principal principal)
	{
		
		Contact old=this.cr.findById(contact.getcId()).get();
		try
		{
			
			//image
			
			if(!file.isEmpty())
			{

				//delete old photo
				//File deleteFile=new ClassPathResource("/static/image").getFile();
				
				//File file1=new File(deleteFile,old.getImage());
				//file1.delete();
				
				//update new photo
				
				//File saveFile=new ClassPathResource("/static/image").getFile();
				//Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				//Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			
			//contact.setImage(file.getOriginalFilename());
				Map uploadResult = this.cloudanirayImageService.upload(file);
				contact.setImage(uploadResult.get("url").toString());
			
			}
			else
			{
				contact.setImage(old.getImage());
			}
			User user=this.ur.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.cr.save(contact);
			ra.addFlashAttribute("message", new Message("Yous Contact is updated.....","alert-success"));
			
		}
		catch(Exception e)
		{
			
		}
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	
	//your profile handle
	
	@GetMapping("/profile")
	public String yourprofile(Model m)
	{
		m.addAttribute("title","profile");
		return "normal/profile";
	}
	
	//setting handler
	
	@GetMapping("/settings")
	public String yoursetting(Model m)
	{
		m.addAttribute("title","setting");
		return "normal/settings";
	}
	
	//change password handler
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam String oldPassword,@RequestParam String newPassword ,Principal principal,RedirectAttributes ra)
	{
		User user=ur.getUserByUserName(principal.getName());
		if(this.pe.matches(oldPassword, user.getPassword()))
		{
			//change password
			
			user.setPassword(this.pe.encode(newPassword));
			ur.save(user);
			ra.addFlashAttribute("message",new Message("Password Changed","alert-success"));
			
		}
		else
		{
			ra.addFlashAttribute("message",new Message("please enter correct old password","alert-danger"));
			return "redirect:/user/settings";
		}
		
		
		return "redirect:/user/index";
	}
	
	//creating order for payment
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createorder(@RequestBody Map<String,Object> data,Principal principal) throws Exception
	{

		
		int amt=Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient Client = new RazorpayClient("rzp_test_jEshvXbr5mBElc","k7BoIdhpZjGULsoKGc2hsBni");
		JSONObject options = new JSONObject();
		options.put("amount", amt*100);
		options.put("currency", "INR");
		options.put("receipt", "txn_646467");
		
		//creating order
		Order order = Client.Orders.create(options);
		System.out.println(order);
		
		
		MyOrder myOrder=new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setStatus("created");
		myOrder.setUser(ur.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		this.or.save(myOrder);
		
		return order.toString();
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object> data,Principal principal)
	{
		
		System.out.println("hello");
		MyOrder myOrder=this.or.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		
		this.or.save(myOrder);
		return ResponseEntity.ok(Map.of("msg","Updated"));
	}
	
	
	
	

}
