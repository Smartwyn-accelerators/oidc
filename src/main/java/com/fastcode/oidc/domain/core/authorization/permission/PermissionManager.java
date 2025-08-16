package com.fastcode.oidc.domain.core.authorization.permission;

import com.fastcode.oidc.domain.irepository.IPermissionRepository;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PermissionManager implements IPermissionManager {

    @Autowired
    private IPermissionRepository  _permissionRepository;
   
    
	public PermissionEntity create(PermissionEntity permission) {

		return _permissionRepository.save(permission);
	}

	public void delete(PermissionEntity permission) {

		_permissionRepository.delete(permission);	
	}

	public PermissionEntity update(PermissionEntity permission) {

		return _permissionRepository.save(permission);
	}

	public PermissionEntity findById(Long  permissionId)
    {
    Optional<PermissionEntity> dbPermission= _permissionRepository.findById(permissionId);
		if(dbPermission.isPresent()) {
			PermissionEntity existingPermission = dbPermission.get();
		    return existingPermission;
		} else {
		    return null;
		}
     //  return _permissionRepository.findById(permissionId.longValue());

	}

	public Page<PermissionEntity> findAll(Predicate predicate, Pageable pageable) {

		return _permissionRepository.findAll(predicate,pageable);
	}

    public PermissionEntity findByPermissionName(String permissionName) {
        return _permissionRepository.findByPermissionName(permissionName);
 
    }
}
