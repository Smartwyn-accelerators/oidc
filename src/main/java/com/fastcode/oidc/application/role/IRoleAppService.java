package com.fastcode.oidc.application.role;

import com.fastcode.oidc.application.role.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.domain.model.QRoleEntity;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IRoleAppService {
    // CRUD Operations

    public CreateRoleOutput create(CreateRoleInput input);

    public void delete(Long rid);

    public UpdateRoleOutput update(Long roleId, UpdateRoleInput input);

    public FindRoleByIdOutput findById(Long rid);

    public FindRoleByNameOutput findByRoleName(String roleName);

    public List<FindRoleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QRoleEntity role,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception;
    
    public BooleanBuilder searchSpecificProperty(QRoleEntity role,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QRoleEntity role, Map<String,SearchFields> map,Map<String,String> joinColumns);
    
    public Map<String,String> parseRolepermissionJoinColumn(String keysString);
    
    public Map<String,String> parseUserroleJoinColumn(String keysString);
    
}
