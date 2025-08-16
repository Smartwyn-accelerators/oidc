package com.fastcode.oidc.application.user;

import com.fastcode.oidc.application.user.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.domain.model.QUserEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IUserAppService {

	public void saveToken(String code) throws Exception;
	
	public CreateUserOutput create(CreateUserInput user) throws Exception;

	public UserEntity getUser();
	
	public UserProfile getProfile(FindUserByIdOutput user);

	public FindUserByNameOutput findByUserName(String userName);

	public FindUserByNameOutput findByEmailAddress(String emailAddress);
	
	public void updateUserData(FindUserWithAllFieldsByIdOutput user);
	
	public UserProfile updateUserProfile(FindUserWithAllFieldsByIdOutput user, UserProfile userProfile);
	
	public FindUserWithAllFieldsByIdOutput findWithAllFieldsById(Long userId);

    void delete(Long id);

    public UpdateUserOutput update(Long userId, UpdateUserInput input);

    public FindUserByIdOutput findById(Long id);

    public List<FindUserByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;
    
    public BooleanBuilder search(SearchCriteria search) throws Exception;
    
    public BooleanBuilder searchAllProperties(QUserEntity user,String value,String operator);
    
    public void checkProperties(List<String> list) throws Exception;
    
    public BooleanBuilder searchSpecificProperty(QUserEntity user,List<String> list,String value,String operator);
    
    public BooleanBuilder searchKeyValuePair(QUserEntity user, Map<String,SearchFields> map,Map<String,String> joinColumns);
    
    public Boolean checkIsAdmin(UserEntity user);
    
    public Map<String,String> parseUserpermissionJoinColumn(String keysString);
    
    public Map<String,String> parseUserroleJoinColumn(String keysString);

}
