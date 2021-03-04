package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details;
        Account account = repository.findById(username).orElseGet(() -> repository.save(new Account(username).setRole("ROLE_USER")));
        details = new User(account.getId(), "", Optional.ofNullable(account.getRoles()).map(roles -> roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())).orElseGet(Lists::newArrayList));
        return details;
    }
}
