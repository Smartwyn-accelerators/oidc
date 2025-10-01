package com.fastcode.oidc.application.rolepermission;

import com.fastcode.oidc.application.rolepermission.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.domain.model.QRolepermissionEntity;
import com.fastcode.oidc.domain.model.RolepermissionId;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IRolepermissionAppService {

	public CreateRolepermissionOutput create(CreateRolepermissionInput rolepermission);

    void delete(RolepermissionId rolepermissionId);

    public UpdateRolepermissionOutput update(RolepermissionId rolepermissionId , UpdateRolepermissionInput input);

    public FindRolepermissionByIdOutput findById(RolepermissionId rolepermissionId);

    public List<FindRolepermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
	
    public GetPermissionOutput getPermission(RolepermissionId rolepermissionId);
    
    public GetRoleOutput getRole(RolepermissionId rolepermissionId);
    
    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QRolepermissionEntity rolepermission,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception;
    
    public BooleanBuilder searchSpecificProperty(QRolepermissionEntity rolepermission,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QRolepermissionEntity rolepermission, Map<String,SearchFields> map,Map<String,String> joinColumns);

    public RolepermissionId parseRolepermissionKey(String keysString);
    
    
}
