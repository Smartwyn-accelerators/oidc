package com.fastcode.oidc.domain.core.authorization.userrole;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.fastcode.oidc.domain.model.UserroleEntity;
import com.fastcode.oidc.domain.model.UserroleId;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.irepository.IUserroleRepository;
import com.querydsl.core.types.Predicate;

@Repository
public class UserroleManager implements IUserroleManager {

    @Autowired
    private IUserroleRepository  _userroleRepository;
    
	public UserroleEntity create(UserroleEntity userrole) {

		return _userroleRepository.save(userrole);
	}

	public void delete(UserroleEntity userrole) {

		_userroleRepository.delete(userrole);	
	}

	public UserroleEntity update(UserroleEntity userrole) {

		return _userroleRepository.save(userrole);
	}

	public UserroleEntity findById(UserroleId userroleId) {
    	Optional<UserroleEntity> dbUserrole= _userroleRepository.findById(userroleId);
		if(dbUserrole.isPresent()) {
			UserroleEntity existingUserrole = dbUserrole.get();
		    return existingUserrole;
		} else {
		    return null;
		}
	}
	public Page<UserroleEntity> findAll(Predicate predicate, Pageable pageable) {

		return _userroleRepository.findAll(predicate,pageable);
	}
  
   //User
	public UserEntity getUser(UserroleId userroleId) {
		
		UserroleEntity dbUserrole= _userroleRepository.findByIdAndRoleId(userroleId.getUserId(),userroleId.getRoleId());
		if(dbUserrole !=null) {
		    return dbUserrole.getUser();
		} else {
		    return null;
		}
	}
  
   //Role
	public RoleEntity getRole(UserroleId userroleId) {
		
		UserroleEntity dbUserrole= _userroleRepository.findByIdAndRoleId(userroleId.getUserId(),userroleId.getRoleId());
		if(dbUserrole !=null) {
		    return dbUserrole.getRole();
		} else {
		    return null;
		}
	}
}
