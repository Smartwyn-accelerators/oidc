package com.fastcode.oidc.application.permission;

import com.fastcode.oidc.application.permission.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.domain.model.QPermissionEntity;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


public interface IPermissionAppService {
    // CRUD Operations
    public CreatePermissionOutput create(CreatePermissionInput input);

    public void delete(Long pid);

    public UpdatePermissionOutput update(Long permissionId, UpdatePermissionInput input);

    public FindPermissionByIdOutput findById(Long pid);
    
    public FindPermissionByNameOutput findByPermissionName(String permissionName);

    public List<FindPermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
    
    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QPermissionEntity permission,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception;
    
    public BooleanBuilder searchSpecificProperty(QPermissionEntity permission,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QPermissionEntity permission, Map<String,SearchFields> map,Map<String,String> joinColumns);
    
    public Map<String,String> parseRolepermissionJoinColumn(String keysString);
    
    public Map<String,String> parseUserpermissionJoinColumn(String keysString);

}
