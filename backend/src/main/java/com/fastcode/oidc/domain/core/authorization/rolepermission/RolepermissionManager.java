package com.fastcode.oidc.domain.core.authorization.rolepermission;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.fastcode.oidc.domain.model.RolepermissionEntity;
import com.fastcode.oidc.domain.model.RolepermissionId;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.fastcode.oidc.domain.model.RoleEntity;

import com.fastcode.oidc.domain.irepository.IRolepermissionRepository;
import com.querydsl.core.types.Predicate;

@Component
public class RolepermissionManager implements IRolepermissionManager {

    @Autowired
    private IRolepermissionRepository  _rolepermissionRepository;

	public RolepermissionEntity create(RolepermissionEntity rolepermission) {

		return _rolepermissionRepository.save(rolepermission);
	}

	public void delete(RolepermissionEntity rolepermission) {

		_rolepermissionRepository.delete(rolepermission);	
	}

	public RolepermissionEntity update(RolepermissionEntity rolepermission) {

		return _rolepermissionRepository.save(rolepermission);
	}

	public RolepermissionEntity findById(RolepermissionId rolepermissionId) {
	
    Optional<RolepermissionEntity> dbRolepermission= _rolepermissionRepository.findById(rolepermissionId);
		if(dbRolepermission.isPresent()) {
			RolepermissionEntity existingRolepermission = dbRolepermission.get();
		    return existingRolepermission;
		} else {
		    return null;
		}
	}

	public Page<RolepermissionEntity> findAll(Predicate predicate, Pageable pageable) {

		return _rolepermissionRepository.findAll(predicate,pageable);
	}

    //Permission
	public PermissionEntity getPermission(RolepermissionId rolepermissionId) {
		
		RolepermissionEntity dbRolepermission = _rolepermissionRepository.findByIdAndPermissionId(rolepermissionId.getRoleId(), rolepermissionId.getPermissionId());
		if(dbRolepermission !=null) {
		    return dbRolepermission.getPermission();
		} else {
		    return null;
		}
	}
	
    //Role
	public RoleEntity getRole(RolepermissionId rolepermissionId) {
		
		RolepermissionEntity dbRolepermission = _rolepermissionRepository.findByIdAndPermissionId(rolepermissionId.getRoleId(), rolepermissionId.getPermissionId());
		if(dbRolepermission !=null) {
			return dbRolepermission.getRole();
		}
		else return null;
		
	}
	
}
