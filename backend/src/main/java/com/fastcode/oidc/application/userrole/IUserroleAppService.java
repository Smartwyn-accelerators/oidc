package com.fastcode.oidc.application.userrole;

import com.fastcode.oidc.application.user.dto.FindUserByIdOutput;
import com.fastcode.oidc.application.userrole.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.domain.model.QUserroleEntity;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserroleId;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IUserroleAppService {

	public CreateUserroleOutput create(CreateUserroleInput userrole);

    void delete(UserroleId userroleId);

    public UpdateUserroleOutput update(UserroleId userroleId, UpdateUserroleInput input);

    public FindUserroleByIdOutput findById(UserroleId userroleId);

    public List<FindUserroleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public List<FindUserByIdOutput> findByRole(String role) throws Exception;

    public GetUserOutput getUser(UserroleId userroleId);
    
    public GetRoleOutput getRole(UserroleId userroleId);
    
    public boolean checkIfRoleAlreadyAssigned(UserEntity foundUser,RoleEntity foundRole);
    
    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QUserroleEntity userrole,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception ;
    
    public BooleanBuilder searchSpecificProperty(QUserroleEntity userrole,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QUserroleEntity userrole, Map<String,SearchFields> map,Map<String,String> joinColumns);
    
    public UserroleId parseUserroleKey(String keysString);
}
