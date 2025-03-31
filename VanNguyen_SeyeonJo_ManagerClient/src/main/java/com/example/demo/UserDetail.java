package com.example.demo;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetail implements UserDetailsService {
	@Autowired    
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
        if(user==null){
               new UsernameNotFoundException("User not exists by Username");
             }
        
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        

      return new org.springframework.security.core.userdetails.User(username,user.getPassword(),Collections.singleton(authority));
  }

}
