package com.fastcode.oidc.application.user;

import com.fastcode.oidc.application.user.dto.*;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;
import com.fastcode.oidc.domain.irepository.IUserRepository;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.QUserEntity;
import com.fastcode.oidc.domain.model.UserroleEntity;
import com.fastcode.oidc.security.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class UserAppService implements IUserAppService {
	
	public static final long PASSWORD_TOKEN_EXPIRATION_TIME = 3_600_000; // 1 hour86400000

	static final int case1=1;
	static final int case2=2;
	static final int case3=3;

	@Autowired
	private IUserManager _userManager;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IUserMapper mapper;

	public void saveToken(String code) throws Exception {
		UserEntity loggedInUser = getUser();
//		GitAccountEntity gitAccount = gitAccountRepository.findByProviderNameAndCompanyAccount(gitProvider.getValue(),true);
//
//		if(gitAccount == null) {
//			gitAccount = new GitAccountEntity();
//			gitAccount.setGitProvider(gitProvider.getValue());
//			gitAccount.setOrganization(loggedInUser.getOrganization());
//			gitAccount = gitAccountRepository.save(gitAccount);
//		}
//
//		if (gitProvider.equals(GitProvider.GITHUB)) {
//			gitAccount.setGitOAuthToken(code);
//			gitAccount = this.githubService.getSyncedUserFromGitProvider(gitAccount);
//		} else if (gitProvider.equals(GitProvider.GITLAB)) {
//			gitAccount.setGitOAuthToken(code);
//			gitAccount = this.gitlabService.getSyncedUserFromGitProvider(gitAccount);
//		}
//
//		gitAccountRepository.save(gitAccount);
	
	}

	@Transactional(readOnly = true)
	public UserEntity getUser() {
		return userRepository.findByEmailAddress(SecurityUtils.getCurrentUserLogin().orElse(null));
	}
	
	public UserProfile getProfile(FindUserByIdOutput user) {
		return mapper.findUserByIdOutputToUserProfile(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CreateUserOutput create(CreateUserInput input) throws Exception {
		UserEntity user = mapper.createUserInputToUserEntity(input);	
		UserEntity createdUser = _userManager.create(user);

		CreateUserOutput output = mapper.userEntityToCreateUserOutput(createdUser);
		return output;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateUserOutput update(Long userId, UpdateUserInput input) {

		UserEntity user = mapper.updateUserInputToUserEntity(input);
		
		UserEntity updatedUser = _userManager.update(user);
		UpdateUserOutput output =  mapper.userEntityToUpdateUserOutput(updatedUser);
		return output;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long userId) {

		UserEntity existing = _userManager.findById(userId) ; 
		_userManager.delete(existing);

	}
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserByIdOutput findById(Long userId) {

		UserEntity foundUser = _userManager.findById(userId);
		if (foundUser == null)  
			return null ; 

		FindUserByIdOutput output = mapper.userEntityToFindUserByIdOutput(foundUser); 
		return output;
	}

//	public RoleEntity findRoleByUser(UserEntity foundUser)
//	{
//		Set<UserroleEntity> userRole = foundUser.getUserroleSet();
//		for(UserroleEntity ur : userRole)
//		{
//			if(ur.getUserId() == foundUser.getId())
//			{
//				RoleEntity role = _roleManager.findById(ur.getRoleId());
//				return role;
//			}
//		}
//
//		return null;
//	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserByNameOutput findByUserName(String userName) {

		UserEntity foundUser = _userManager.findByEmailAddress(userName);
		if (foundUser == null) {
			return null;
		}
		
		return  mapper.userEntityToFindUserByNameOutput(foundUser);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserByNameOutput findByEmailAddress(String emailAddress) {

		UserEntity foundUser = _userManager.findByEmailAddress(emailAddress);
		if (foundUser == null) {
			return null;
		}
	
		return  mapper.userEntityToFindUserByNameOutput(foundUser);
	}
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateUserData(FindUserWithAllFieldsByIdOutput user)
	{
		UserEntity foundUser = mapper.findUserWithAllFieldsByIdOutputToUserEntity(user);
		
		_userManager.update(foundUser);
	}
	
	public UserProfile updateUserProfile(FindUserWithAllFieldsByIdOutput user, UserProfile userProfile)
	{
		UpdateUserInput userInput = mapper.findUserWithAllFieldsByIdOutputAndUserProfileToUpdateUserInput(user, userProfile);
		UpdateUserOutput output = update(user.getId(),userInput);
		
		return mapper.updateUserOutputToUserProfile(output);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserWithAllFieldsByIdOutput findWithAllFieldsById(Long userId) {
	
		UserEntity foundUser = _userManager.findById(userId);
		if (foundUser == null)  
			return null ; 
//		RoleEntity role = findRoleByUser(foundUser);
		
		FindUserWithAllFieldsByIdOutput output=mapper.userEntityToFindUserWithAllFieldsByIdOutput(foundUser); 
		return output;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindUserByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception  {

		Page<UserEntity> foundUser = _userManager.findAll(search(search), pageable);
		List<UserEntity> userList = foundUser.getContent();
		Iterator<UserEntity> userIterator = userList.iterator(); 
		List<FindUserByIdOutput> output = new ArrayList<>();

		while (userIterator.hasNext()) {
			UserEntity user = userIterator.next();
		//	RoleEntity role = findRoleByUser(user);
			FindUserByIdOutput obj = mapper.userEntityToFindUserByIdOutput(user);
			output.add(obj);
		}
		return output;
	}

	public BooleanBuilder search(SearchCriteria search) throws Exception {

		QUserEntity user= QUserEntity.userEntity;
		if(search != null) {
			if(search.getType()==case1)
			{
				return searchAllProperties(user, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2)
			{
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields())
				{
					keysList.add(f.getFieldName());
				}
				checkProperties(keysList);
				return searchSpecificProperty(user,keysList,search.getValue(),search.getOperator());
			}
			else if(search.getType()==case3)
			{
				Map<String,SearchFields> map = new HashMap<>();
				for(SearchFields fieldDetails: search.getFields())
				{
					map.put(fieldDetails.getFieldName(),fieldDetails);
				}
				List<String> keysList = new ArrayList<String>(map.keySet());
				checkProperties(keysList);
				
				Map<String, String> joinColumn = search.getJoinColumns();
				if(joinColumn ==null) {
					joinColumn = new HashMap<String, String>();
				}
				
				UserEntity loggedInUser =  getUser();
				return searchKeyValuePair(user, map,joinColumn);
			}

		}
		return null;
	}

	public BooleanBuilder searchAllProperties(QUserEntity user,String value,String operator) {
		BooleanBuilder builder = new BooleanBuilder();

		if(operator.equals("contains")) {
			builder.or(user.emailAddress.likeIgnoreCase("%"+ value + "%"));
		}
		else if(operator.equals("equals"))
		{
			builder.or(user.emailAddress.eq(value));
		}

		return builder;
	}

	public void checkProperties(List<String> list) throws Exception  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
					list.get(i).replace("%20","").trim().equals("roleId") ||

					list.get(i).replace("%20","").trim().equals("accessFailedCount") ||
					list.get(i).replace("%20","").trim().equals("authenticationSource") ||
					list.get(i).replace("%20","").trim().equals("emailAddress") ||
					list.get(i).replace("%20","").trim().equals("emailConfirmationCode") ||
					list.get(i).replace("%20","").trim().equals("firstName") ||
					list.get(i).replace("%20","").trim().equals("id") ||
					list.get(i).replace("%20","").trim().equals("isEmailConfirmed") ||
					list.get(i).replace("%20","").trim().equals("isLockoutEnabled") ||
					list.get(i).replace("%20","").trim().equals("isPhoneNumberConfirmed") ||
					list.get(i).replace("%20","").trim().equals("lastLoginTime") ||
					list.get(i).replace("%20","").trim().equals("lastName") ||
					list.get(i).replace("%20","").trim().equals("lockoutEndDateUtc") ||
					list.get(i).replace("%20","").trim().equals("isActive") ||
					list.get(i).replace("%20","").trim().equals("password") ||
					list.get(i).replace("%20","").trim().equals("passwordResetCode") ||
					list.get(i).replace("%20","").trim().equals("phoneNumber") ||
					list.get(i).replace("%20","").trim().equals("profilePictureId") ||
					list.get(i).replace("%20","").trim().equals("userrole") ||
					list.get(i).replace("%20","").trim().equals("isTwoFactorEnabled") ||
					list.get(i).replace("%20","").trim().equals("userName") ||
					list.get(i).replace("%20","").trim().equals("userpermission")
					)) 
			{
				throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}

	public BooleanBuilder searchSpecificProperty(QUserEntity user,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();

		for (int i = 0; i < list.size(); i++) {

			if(list.get(i).replace("%20","").trim().equals("emailAddress")) {
				if(operator.equals("contains"))
					builder.or(user.emailAddress.likeIgnoreCase("%"+ value + "%"));
				else if(operator.equals("equals)"))
					builder.or(user.emailAddress.eq(value));
			}

		}
		return builder;
	}

	public BooleanBuilder searchKeyValuePair(QUserEntity user, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();

		for (Map.Entry<String, SearchFields> details : map.entrySet()) {

			if(details.getKey().replace("%20","").trim().equals("emailAddress")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(user.emailAddress.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(user.emailAddress.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(user.emailAddress.ne(details.getValue().getSearchValue()));
			}

		}
		
		return builder;
	}

	public Boolean checkIsAdmin(UserEntity user)
	{
		Set<UserroleEntity> ure = user.getUserroleSet();
		Iterator<UserroleEntity> iterator = ure.iterator();
		while(iterator.hasNext())
		{
			UserroleEntity ur = (UserroleEntity) iterator.next();
			if(ur.getRole().getName().equals("ROLE_Admin"))
				return true;
		}

		return false;
	}

	public Map<String,String> parseUserpermissionJoinColumn(String keysString) {

		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("userId", keysString);
		return joinColumnMap;
	}

	public Map<String,String> parseUserroleJoinColumn(String keysString) {

		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("userId", keysString);
		return joinColumnMap;

	}


}

