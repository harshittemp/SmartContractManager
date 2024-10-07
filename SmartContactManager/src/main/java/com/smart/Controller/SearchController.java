package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.Enteties.Contact;
import com.smart.Enteties.User;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;

@RestController
public class SearchController {
	@Autowired
	private UserRepository ur;
	@Autowired
	private ContactRepository cr;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable String query,Principal principal)
	{
		User user=this.ur.getUserByUserName(principal.getName());
		List<Contact> contacts=cr.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}

}
