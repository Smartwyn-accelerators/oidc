package com.fastcode.oidc.domain.core.authorization.user;

import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.irepository.IUserpermissionRepository;
import com.fastcode.oidc.domain.irepository.IRoleRepository;
import com.fastcode.oidc.domain.irepository.IUserRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserManager implements IUserManager {

	@Autowired
    IUserRepository  _userRepository;
    
    @Autowired
	IUserpermissionRepository  _userpermissionRepository;
    
    @Autowired
	IRoleRepository  _roleRepository;

	public UserEntity create(UserEntity user) {
		user.setEmailAddress(user.getEmailAddress().toLowerCase());
		return _userRepository.save(user);
	}

	public void delete(UserEntity user) {

		_userRepository.delete(user);	
	}

	public UserEntity update(UserEntity user) {
		user.setEmailAddress(user.getEmailAddress().toLowerCase());
		return _userRepository.save(user);
	}

	public UserEntity findById(Long  userId) {
    Optional<UserEntity> dbUser= _userRepository.findById(userId);
		if(dbUser.isPresent()) {
			UserEntity existingUser = dbUser.get();
		    return existingUser;
		} else {
		    return null;
		}
	}

	public Page<UserEntity> findAll(Predicate predicate, Pageable pageable) {

		return _userRepository.findAll(predicate,pageable);
	}

	public UserEntity findByEmailAddress(String emailAddress) {
		return  _userRepository.findByEmailAddress(emailAddress.toLowerCase());
	}
	
	
}

