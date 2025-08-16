package com.fastcode.oidc.application.rolepermission;

import com.fastcode.oidc.application.rolepermission.dto.*;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import com.fastcode.oidc.commons.search.*;
import com.fastcode.oidc.domain.core.authorization.permission.IPermissionManager;
import com.fastcode.oidc.domain.core.authorization.role.IRoleManager;
import com.fastcode.oidc.domain.core.authorization.rolepermission.IRolepermissionManager;
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
public class RolepermissionAppService implements IRolepermissionAppService {

    static final int case1=1;
	static final int case2=2;
	static final int case3=3;
	
	@Autowired
	private IRolepermissionManager _rolepermissionManager;
	
	@Autowired
	@Lazy
	private IUserAppService _userAppService;
  
    @Autowired
	private IPermissionManager _permissionManager;
    
    @Autowired
	private IRoleManager _roleManager;

	@Autowired
	private AuthLoggingHelper logHelper;

	@Autowired
	private IRolepermissionMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateRolepermissionOutput create(CreateRolepermissionInput input) {

		RolepermissionEntity rolepermission = mapper.createRolepermissionInputToRolepermissionEntity(input);
		if(input.getPermissionId()!=null && input.getRoleId()!=null) {
			PermissionEntity foundPermission = _permissionManager.findById(input.getPermissionId());
			RoleEntity foundRole = _roleManager.findById(input.getRoleId());
			
			if(foundPermission!=null && foundRole!=null) {
				foundRole.addRolepermission(rolepermission);
				foundPermission.addRolepermission(rolepermission);
				return mapper.roleEntityAndPermissionEntityToCreateRolepermissionOutput(foundRole,foundPermission);
			}
		}
		return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateRolepermissionOutput update(RolepermissionId rolepermissionId , UpdateRolepermissionInput input) {

		RolepermissionEntity rolepermission = mapper.updateRolepermissionInputToRolepermissionEntity(input);

		if(input.getPermissionId()!=null && input.getRoleId()!=null){
			PermissionEntity foundPermission = _permissionManager.findById(input.getPermissionId());
			RoleEntity foundRole = _roleManager.findById(input.getRoleId());

			if(foundPermission!=null && foundRole!=null) {
				rolepermission.setPermission(foundPermission);
				rolepermission.setRole(foundRole);
				
				RolepermissionEntity updatedRolepermission = _rolepermissionManager.update(rolepermission);
				return mapper.rolepermissionEntityToUpdateRolepermissionOutput(updatedRolepermission);
			}
		}

		return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(RolepermissionId rolepermissionId) {

		RolepermissionEntity existing = _rolepermissionManager.findById(rolepermissionId) ;
		_rolepermissionManager.delete(existing);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRolepermissionByIdOutput findById(RolepermissionId rolepermissionId) {

		RolepermissionEntity foundRolepermission = _rolepermissionManager.findById(rolepermissionId);
		if (foundRolepermission == null)  
			return null ; 
 	   
 	   FindRolepermissionByIdOutput output=mapper.rolepermissionEntityToFindRolepermissionByIdOutput(foundRolepermission); 
		return output;
	}

    //Permission
	// ReST API Call - GET /rolepermission/1/permission
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetPermissionOutput getPermission(RolepermissionId rolepermissionId) {

		RolepermissionEntity foundRolepermission = _rolepermissionManager.findById(rolepermissionId);
		if (foundRolepermission == null) {
			logHelper.getLogger().error("There does not exist a rolepermission wth a id=%s", rolepermissionId);
			return null;
		}
		PermissionEntity re = _rolepermissionManager.getPermission(rolepermissionId);
		return mapper.permissionEntityToGetPermissionOutput(re, foundRolepermission);
	}
    
    //Role
	// ReST API Call - GET /rolepermission/1/role
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public GetRoleOutput getRole(RolepermissionId rolepermissionId) {

		RolepermissionEntity foundRolepermission = _rolepermissionManager.findById(rolepermissionId);
		if (foundRolepermission == null) {
			logHelper.getLogger().error("There does not exist a rolepermission wth a id=%s", rolepermissionId);
			return null;
		}
		RoleEntity re = _rolepermissionManager.getRole(rolepermissionId);
		return mapper.roleEntityToGetRoleOutput(re, foundRolepermission);
	}
    

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindRolepermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception  {

		Page<RolepermissionEntity> foundRolepermission = _rolepermissionManager.findAll(search(search), pageable);
		List<RolepermissionEntity> rolepermissionList = foundRolepermission.getContent();
		Iterator<RolepermissionEntity> rolepermissionIterator = rolepermissionList.iterator(); 
		List<FindRolepermissionByIdOutput> output = new ArrayList<>();

		while (rolepermissionIterator.hasNext()) {
			output.add(mapper.rolepermissionEntityToFindRolepermissionByIdOutput(rolepermissionIterator.next()));
		}
		return output;
	}
	
	public BooleanBuilder search(SearchCriteria search) throws Exception {

		QRolepermissionEntity rolepermission= QRolepermissionEntity.rolepermissionEntity;
		if(search != null) {
			if(search.getType()==case1)
			{
				return searchAllProperties(rolepermission, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2)
			{
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields())
				{
					keysList.add(f.getFieldName());
				}
				checkProperties(keysList);
				return searchSpecificProperty(rolepermission,keysList,search.getValue(),search.getOperator());
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
				return searchKeyValuePair(rolepermission, map,joinColumn);
			}

		}
		return null;
	}
	
	public BooleanBuilder searchAllProperties(QRolepermissionEntity rolepermission,String value,String operator) {
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
		 list.get(i).replace("%20","").trim().equals("permission") ||
		 list.get(i).replace("%20","").trim().equals("permissionId") ||
		 list.get(i).replace("%20","").trim().equals("role") ||
		 list.get(i).replace("%20","").trim().equals("roleId")
		)) 
		{
		 throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!" );
		}
		}
	}
	
	public BooleanBuilder searchSpecificProperty(QRolepermissionEntity rolepermission,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();
		
		for (int i = 0; i < list.size(); i++) {
		
		  if(list.get(i).replace("%20","").trim().equals("permissionId")) {
			builder.or(rolepermission.permission.id.eq(Long.parseLong(value)));
			}
		  if(list.get(i).replace("%20","").trim().equals("roleId")) {
			builder.or(rolepermission.role.id.eq(Long.parseLong(value)));
			}
		}
		return builder;
	}
	
	public BooleanBuilder searchKeyValuePair(QRolepermissionEntity rolepermission, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();

		for (Map.Entry<String, SearchFields> details : map.entrySet()) {
		}
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("permissionId")) {
		    builder.and(rolepermission.permission.id.eq(Long.parseLong(joinCol.getValue())));
		}
        }
		for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
        if(joinCol != null && joinCol.getKey().equals("roleId")) {
		    builder.and(rolepermission.role.id.eq(Long.parseLong(joinCol.getValue())));
		}
        }
		return builder;
	}
	
	public RolepermissionId parseRolepermissionKey(String keysString) {
		
		String[] keyEntries = keysString.split(",");
		RolepermissionId rolepermissionId = new RolepermissionId();
		
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
		
		rolepermissionId.setPermissionId(Long.valueOf(keyMap.get("permissionId")));
		rolepermissionId.setRoleId(Long.valueOf(keyMap.get("roleId")));
		return rolepermissionId;
	}	
	
}


