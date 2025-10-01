package com.fastcode.oidc.security;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fastcode.oidc.domain.irepository.IUserRepository;
import com.fastcode.oidc.domain.model.UserEntity;

@Service("userDetailsService")
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private IUserRepository userService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (StringUtils.isAnyBlank(username)) {
            throw new UsernameNotFoundException("Username and domain must be provided");
        }
        // Look for the user based on the username and tenant by accessing the
        // UserRepository via the UserService
        UserEntity user = userService.findByEmailAddress(username);

        if (user == null) {
            throw new UsernameNotFoundException(
                    String.format("Username not found for domain, "
                            + "username=%s", username));
        }

    	List<String> permissions = securityUtils.getAllPermissionsFromUserAndRole(user);
    	String[] groupsArray = new String[permissions.size()];
       	List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(groupsArray));

        CustomUserDetails customUserDetails = 
                new CustomUserDetails(user.getEmailAddress().toLowerCase(), null, authorities, null);
        
        return customUserDetails;
    }
}