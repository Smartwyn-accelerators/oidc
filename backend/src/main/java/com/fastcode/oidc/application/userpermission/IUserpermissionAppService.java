package com.fastcode.oidc.application.userpermission;

import com.fastcode.oidc.application.userpermission.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.fastcode.oidc.domain.model.QUserpermissionEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserpermissionId;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IUserpermissionAppService {

	public CreateUserpermissionOutput create(CreateUserpermissionInput userpermission);
	
	public boolean checkIfPermissionAlreadyAssigned(UserEntity foundUser,PermissionEntity foundPermission);
    
    void delete(UserpermissionId userpermissionId);

    public UpdateUserpermissionOutput update(UserpermissionId userpermissionId , UpdateUserpermissionInput input);

    public FindUserpermissionByIdOutput findById(UserpermissionId userpermissionId);

    public List<FindUserpermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
	
    public GetUserOutput getUser(UserpermissionId userpermissionId);

    public GetPermissionOutput getPermission(UserpermissionId userpermissionId);
    
    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QUserpermissionEntity userpermission,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception;
    
    public BooleanBuilder searchSpecificProperty(QUserpermissionEntity userpermission,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QUserpermissionEntity userpermission, Map<String,SearchFields> map,Map<String,String> joinColumns);
    
    public UserpermissionId parseUserpermissionKey(String keysString);
    
}
