package com.fision.utils;

import com.fision.entity.TbUser;
import com.fision.serviceImpl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author LordDev
 */
@Service
public class JWTUserDetailService implements UserDetailsService {
    @Autowired
    LoginServiceImpl loginService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TbUser user = loginService.getUserByUsername(username);
        if(user != null) {
            return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
    }
}
