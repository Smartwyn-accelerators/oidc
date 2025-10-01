package com.fastcode.oidc.application.userpermission;

import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.userpermission.dto.*;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import com.fastcode.oidc.commons.search.*;
import com.fastcode.oidc.domain.core.authorization.permission.IPermissionManager;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;
import com.fastcode.oidc.domain.core.authorization.userpermission.IUserpermissionManager;
import com.fastcode.oidc.domain.model.*;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class UserpermissionAppService implements IUserpermissionAppService {

    static final int case1=1;
	static final int case2=2;
	static final int case3=3;
	
	@Autowired
	private IUserpermissionManager _userpermissionManager;
	
	@Autowired
	@Lazy
	private IUserAppService _userAppService;
  
  
    @Autowired
	private IUserManager _userManager;

    @Autowired
	private IPermissionManager _permissionManager;
    
	@Autowired
	private AuthLoggingHelper logHelper;

	@Autowired
	private IUserpermissionMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateUserpermissionOutput create(CreateUserpermissionInput input) {

		UserpermissionEntity userpermission = mapper.createUserpermissionInputToUserpermissionEntity(input);
    	if(input.getUserId()!=null || input.getPermissionId()!=null)
		{
		UserEntity foundUser = _userManager.findById(input.getUserId());
		PermissionEntity foundPermission = _permissionManager.findById(input.getPermissionId());
		
		if(foundUser!=null || foundPermission!=null)
		{			
				if(!checkIfPermissionAlreadyAssigned(foundUser, foundPermission))
				{
					foundUser.addUserpermission(userpermission);
					foundPermission.addUserpermission(userpermission);
					
					CreateUserpermissionOutput output = mapper.userAndPermissionEntityToCreateUserpermissionOutput(foundUser, foundPermission);
					output.setRevoked(input.getRevoked());
					return output;
				}
		}
		}
		return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateUserpermissionOutput update(UserpermissionId userpermissionId , UpdateUserpermissionInput input) {

		UserpermissionEntity userpermission = mapper.updateUserpermissionInputToUserpermissionEntity(input);
		
		if(input.getUserId()!=null || input.getPermissionId()!=null)
		{
			UserEntity foundUser = _userManager.findById(input.getUserId());
			PermissionEntity foundPermission = _permissionManager.findById(input.getPermissionId());
		
			if(foundUser!=null || foundPermission!=null)
			{			
				if(checkIfPermissionAlreadyAssigned(foundUser, foundPermission))
				{
					userpermission.setPermission(foundPermission);
					userpermission.setUser(foundUser);
					userpermission.setRevoked(input.getRevoked());
					
					UserpermissionEntity updatedUserpermission = _userpermissionManager.update(userpermission);
					return mapper.userpermissionEntityToUpdateUserpermissionOutput(updatedUserpermission);
				}
			}
		}
		
		return null;
	}
	
	public boolean checkIfPermissionAlreadyAssigned(UserEntity foundUser,PermissionEntity foundPermission)
	{
		
		Set<UserpermissionEntity> userPermission = foundUser.getUserpermissionSet();
		 
		Iterator pIterator = userPermission.iterator();
			while (pIterator.hasNext()) { 
				UserpermissionEntity pe = (UserpermissionEntity) pIterator.next();
				if (pe.getPermission() == foundPermission ) {
					return true;
				}
			}
			
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(UserpermissionId userpermissionId) {

		UserpermissionEntity existing = _userpermissionManager.findById(userpermissionId) ; 
		_userpermissionManager.delete(existing);

	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindUserpermissionByIdOutput findById(UserpermissionId userpermissionId) {

		UserpermissionEntity foundUserpermission = _userpermissionManager.findById(userpermissionId);
		if (foundUserpermission == null)  
			return null ; 
 	   
 	   FindUserpermissionByIdOutput output = mapper.userpermissionEntityToFindUserpermissionByIdOutput(foundUserpermission); 
		return output;
	}
	
    //User
	// ReST API Call - GET /userpermission/1/user
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetUserOutput getUser(UserpermissionId userpermissionId) {

		UserpermissionEntity foundUserpermission = _userpermissionManager.findById(userpermissionId);
		if (foundUserpermission == null) {
			logHelper.getLogger().error("There does not exist a userpermission wth a id=%s", userpermissionId);
			return null;
		}
		UserEntity re = _userpermissionManager.getUser(userpermissionId);
		return mapper.userEntityToGetUserOutput(re, foundUserpermission);
	}
    
    //Permission
	// ReST API Call - GET /userpermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPermissionOutput getPermission(UserpermissionId userpermissionId) {

		UserpermissionEntity foundUserpermission = _userpermissionManager.findById(userpermissionId);
		if (foundUserpermission == null) {
			logHelper.getLogger().error("There does not exist a userpermission wth a id=%s", userpermissionId);
			return null;
		}
		PermissionEntity re = _userpermissionManager.getPermission(userpermissionId);
		return mapper.permissionEntityToGetPermissionOutput(re, foundUserpermission);
	}
    
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindUserpermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception  {

		Page<UserpermissionEntity> foundUserpermission = _userpermissionManager.findAll(search(search), pageable);
		List<UserpermissionEntity> userpermissionList = foundUserpermission.getContent();
		Iterator<UserpermissionEntity> userpermissionIterator = userpermissionList.iterator(); 
		List<FindUserpermissionByIdOutput> output = new ArrayList<>();

		while (userpermissionIterator.hasNext()) {
			output.add(mapper.userpermissionEntityToFindUserpermissionByIdOutput(userpermissionIterator.next()));
		}
		return output;
	}
	
	public BooleanBuilder search(SearchCriteria search) throws Exception {

		QUserpermissionEntity userpermission= QUserpermissionEntity.userpermissionEntity;
		if(search != null) {
			if(search.getType()==case1)
			{
				return searchAllProperties(userpermission, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2)
			{
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields())
				{
					keysList.add(f.getFieldName());
				}
				checkProperties(keysList);
				return searchSpecificProperty(userpermission,keysList,search.getValue(),search.getOperator());
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
				
				UserEntity loggedInUser =  _userAppService.getUser();
				return searchKeyValuePair(userpermission, map,joinColumn);
			}
		}
		return null;
	}
	
	public BooleanBuilder searchAllProperties(QUserpermissionEntity userpermission,String value,String operator) {
		BooleanBuilder builder = new BooleanBuilder();

		if(operator.equals("contains")) {
		}
		else if(operator.equals("equals"))
		{
        	if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
       	 	}
			else if(StringUtils.isNumeric(value)){
        	}
        	else if(SearchUtils.stringToDate(value)!=null) {
			}
		}

		return builder;
	}

	public void checkProperties(List<String> list) throws Exception  {
		for (int i = 0; i < list.size(); i++) {
		if(!(
    	list.get(i).replace("%20","").trim().equals("userId")||
		 list.get(i).replace("%20","").trim().equals("permission") ||
		 list.get(i).replace("%20","").trim().equals("permissionId") ||
		 list.get(i).replace("%20","").trim().equals("user")
		)) 
		{
		 throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!" );
		}
		}
	}
	
	public BooleanBuilder searchSpecificProperty(QUserpermissionEntity userpermission,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();
		
		for (int i = 0; i < list.size(); i++) {

		if(list.get(i).replace("%20","").trim().equals("userId")) {
			builder.or(userpermission.user.id.eq(Long.parseLong(value)));
		}
		if(list.get(i).replace("%20","").trim().equals("permissionId")) {
			builder.or(userpermission.permission.id.eq(Long.parseLong(value)));
		}
		}
		return builder;
	}
	
	public BooleanBuilder searchKeyValuePair(QUserpermissionEntity userpermission, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();

		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        
        if(joinCol != null && joinCol.getKey().equals("userId")) {
		    builder.and(userpermission.user.id.eq(Long.parseLong(joinCol.getValue())));
		}
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("permissionId")) {
		    builder.and(userpermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
		}
        }
		return builder;
	}
	
	public UserpermissionId parseUserpermissionKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		UserpermissionId userpermissionId = new UserpermissionId();
		
		Map<String,String> keyMap = new HashMap<String,String>();
		if(keyEntries.length > 1) {
			for(String keyEntry: keyEntries)
			{
				String[] keyEntryArr = keyEntry.split(":");
				if(keyEntryArr.length > 1) {
					keyMap.put(keyEntryArr[0], keyEntryArr[1]);					
				}
				else {
					return null;
				}
			}
		}
		else {
			return null;
		}
		
		userpermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
        userpermissionId.setUserId(Long.valueOf(keyMap.get("userId")));
		return userpermissionId;
	}	

}
