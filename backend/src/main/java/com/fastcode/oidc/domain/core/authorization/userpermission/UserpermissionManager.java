package com.fastcode.oidc.domain.core.authorization.userpermission;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.fastcode.oidc.domain.model.UserpermissionEntity;
import com.fastcode.oidc.domain.model.UserpermissionId;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.PermissionEntity;

import com.fastcode.oidc.domain.irepository.IUserpermissionRepository;
import com.querydsl.core.types.Predicate;

@Repository
public class UserpermissionManager implements IUserpermissionManager {

    @Autowired
    private IUserpermissionRepository  _userpermissionRepository;
    
	public UserpermissionEntity create(UserpermissionEntity userpermission) {

		return _userpermissionRepository.save(userpermission);
	}

	public void delete(UserpermissionEntity userpermission) {

		_userpermissionRepository.delete(userpermission);	
	}

	public UserpermissionEntity update(UserpermissionEntity userpermission) {

		return _userpermissionRepository.save(userpermission);
	}

	public UserpermissionEntity findById(UserpermissionId userpermissionId ) {
    
    Optional<UserpermissionEntity> dbUserpermission= _userpermissionRepository.findById(userpermissionId);
		if(dbUserpermission.isPresent()) {
			UserpermissionEntity existingUserpermission = dbUserpermission.get();
		    return existingUserpermission;
		} else {
		    return null;
		}
	}

	public Page<UserpermissionEntity> findAll(Predicate predicate, Pageable pageable) {

		return _userpermissionRepository.findAll(predicate,pageable);
	}

	//User
	public UserEntity getUser(UserpermissionId userpermissionId) {
		
		UserpermissionEntity dbUserpermission = _userpermissionRepository.findByIdAndPermissionId(userpermissionId.getUserId(), userpermissionId.getPermissionId());
		if(dbUserpermission !=null) {
		return dbUserpermission.getUser();
		}
		else return null;

	}
	
   //Permission
	public PermissionEntity getPermission(UserpermissionId userpermissionId) {
		
		UserpermissionEntity dbUserpermission = _userpermissionRepository.findByIdAndPermissionId(userpermissionId.getUserId(), userpermissionId.getPermissionId());
		if(dbUserpermission !=null) {
		return dbUserpermission.getPermission();
		}
		else return null;
	}
	
}
