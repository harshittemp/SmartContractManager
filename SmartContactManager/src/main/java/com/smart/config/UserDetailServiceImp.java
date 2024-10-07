package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.Enteties.User;
import com.smart.dao.UserRepository;

public class UserDetailServiceImp implements UserDetailsService{
	
	//db->>>userrepo
	@Autowired
	private UserRepository ur;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		//fetching user from db
		
		User user=ur.getUserByUserName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("Could Not Found User !!");
		}
		
		
		CustomuserDetail cud=new CustomuserDetail(user);
		return cud; 
	}

}
