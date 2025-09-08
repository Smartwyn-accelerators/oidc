package com.fastcode.oidc.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.fastcode.oidc.domain.model.RolepermissionEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserpermissionEntity;
import com.fastcode.oidc.domain.model.UserroleEntity;
import com.fastcode.oidc.domain.model.RoleEntity;

@Component
public class SecurityUtils {

    public List<String> getAllPermissionsFromRole(RoleEntity role)
    {
        List<String> permissions = new ArrayList<>();

        Set<RolepermissionEntity> srp= role.getRolepermissionSet();
        for (RolepermissionEntity item : srp) {
            permissions.add(item.getPermission().getName());
        }

        return permissions;

    }

    public List<String> getAllPermissionsFromUserAndRole(UserEntity user) {

        List<String> permissions = new ArrayList<>();
        Set<UserroleEntity> ure = user.getUserroleSet();
        Iterator rIterator = ure.iterator();
        while (rIterator.hasNext()) {
            UserroleEntity re = (UserroleEntity) rIterator.next();
            Set<RolepermissionEntity> srp= re.getRole().getRolepermissionSet();
            for (RolepermissionEntity item : srp) {
                permissions.add(item.getPermission().getName());
            }
        }

        Set<UserpermissionEntity> spe = user.getUserpermissionSet();
        Iterator pIterator = spe.iterator();
        while (pIterator.hasNext()) {
            UserpermissionEntity pe = (UserpermissionEntity) pIterator.next();

            if(permissions.contains(pe.getPermission().getName()) && (pe.getRevoked() != null && pe.getRevoked()))
            {
                permissions.remove(pe.getPermission().getName());
            }
            if(!permissions.contains(pe.getPermission().getName()) && (pe.getRevoked()==null || !pe.getRevoked()))
            {
                permissions.add(pe.getPermission().getName());

            }

        }

        return permissions
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    public String getTokenFromCookies(Cookie[] cookies, String cookieName)
    {
        if(cookies !=null) {
            for(Cookie c : cookies)
            {
                if(c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }

        return null;
    }

    public Boolean parseTokenAndCheckIfPermissionExists(String token, String permission)
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(permission));

    }

}