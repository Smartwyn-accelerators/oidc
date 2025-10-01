package com.fastcode.oidc.application.permission;

import com.fastcode.oidc.application.permission.dto.CreatePermissionInput;
import com.fastcode.oidc.application.permission.dto.CreatePermissionOutput;
import com.fastcode.oidc.application.permission.dto.FindPermissionByIdOutput;
import com.fastcode.oidc.application.permission.dto.FindPermissionByNameOutput;
import com.fastcode.oidc.application.permission.dto.UpdatePermissionInput;
import com.fastcode.oidc.application.permission.dto.UpdatePermissionOutput;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.core.authorization.permission.IPermissionManager;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.QPermissionEntity;
import com.fastcode.oidc.domain.model.QUserEntity;
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
public class PermissionAppService implements IPermissionAppService {

    static final int case1=1;
	static final int case2=2;
	static final int case3=3;
    
    @Autowired
	private IPermissionManager _permissionManager;
    
    @Autowired
    @Lazy
	private IUserAppService _userAppService;
    
	@Autowired
	private IPermissionMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreatePermissionOutput create(CreatePermissionInput input) {

		PermissionEntity permission = mapper.createPermissionInputToPermissionEntity(input);
		PermissionEntity createdPermission = _permissionManager.create(permission);
		
		return mapper.permissionEntityToCreatePermissionOutput(createdPermission);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdatePermissionOutput update(Long permissionId, UpdatePermissionInput input) {
	
		PermissionEntity permission = mapper.updatePermissionInputToPermissionEntity(input);
		PermissionEntity updatedPermission = _permissionManager.update(permission);

		return mapper.permissionEntityToUpdatePermissionOutput(updatedPermission);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long permissionId) {

		PermissionEntity existing = _permissionManager.findById(permissionId) ;
		_permissionManager.delete(existing);

	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPermissionByIdOutput findById(Long permissionId) {

		PermissionEntity foundPermission = _permissionManager.findById(permissionId);
		if (foundPermission == null)  
			return null ; 
 	   
 	   FindPermissionByIdOutput output=mapper.permissionEntityToFindPermissionByIdOutput(foundPermission); 
		return output;
	}


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindPermissionByNameOutput findByPermissionName(String permissionName) {

		PermissionEntity foundPermission = _permissionManager.findByPermissionName(permissionName);
		if (foundPermission == null) {
			return null;
		}
		return mapper.permissionEntityToFindPermissionByNameOutput(foundPermission);

	}
	
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindPermissionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception  {

		Page<PermissionEntity> foundPermission = _permissionManager.findAll(search(search), pageable);
		List<PermissionEntity> permissionList = foundPermission.getContent();
		Iterator<PermissionEntity> permissionIterator = permissionList.iterator(); 
		List<FindPermissionByIdOutput> output = new ArrayList<>();

		while (permissionIterator.hasNext()) {
			output.add(mapper.permissionEntityToFindPermissionByIdOutput(permissionIterator.next()));
		}
		return output;
	}
	
	public BooleanBuilder search(SearchCriteria search) throws Exception {

		QPermissionEntity permission= QPermissionEntity.permissionEntity;
		if(search != null) {
			if(search.getType()==case1)
			{
				return searchAllProperties(permission, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2)
			{
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields())
				{
					keysList.add(f.getFieldName());
				}
				checkProperties(keysList);
				return searchSpecificProperty(permission,keysList,search.getValue(),search.getOperator());
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
				return searchKeyValuePair(permission, map,joinColumn);
				
			}

		}
		return null;
	}
	
	public BooleanBuilder searchAllProperties(QPermissionEntity permission,String value,String operator) {
		BooleanBuilder builder = new BooleanBuilder();

		if(operator.equals("contains")) {
			builder.or(permission.displayName.likeIgnoreCase("%"+ value + "%"));
			builder.or(permission.name.likeIgnoreCase("%"+ value + "%"));
		}
		else if(operator.equals("equals"))
		{
        	builder.or(permission.displayName.eq(value));
        	builder.or(permission.name.eq(value));
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
		
		 list.get(i).replace("%20","").trim().equals("displayName") ||
		 list.get(i).replace("%20","").trim().equals("id") ||
		 list.get(i).replace("%20","").trim().equals("name") ||
		 list.get(i).replace("%20","").trim().equals("rolepermission") ||
		 list.get(i).replace("%20","").trim().equals("userpermission")
		)) 
		{
		 throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!" );
		}
		}
	}
	
	public BooleanBuilder searchSpecificProperty(QPermissionEntity permission,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();
		
		for (int i = 0; i < list.size(); i++) {
		
            if(list.get(i).replace("%20","").trim().equals("displayName")) {
				if(operator.equals("contains"))
					builder.or(permission.displayName.likeIgnoreCase("%"+ value + "%"));
				else if(operator.equals("equals"))
					builder.or(permission.displayName.eq(value));
			}
            if(list.get(i).replace("%20","").trim().equals("name")) {
				if(operator.equals("contains"))
					builder.or(permission.name.likeIgnoreCase("%"+ value + "%"));
				else if(operator.equals("equals"))
					builder.or(permission.name.eq(value));
			}
		}
		
		return builder;
	}
	
	public BooleanBuilder searchKeyValuePair(QPermissionEntity permission, Map<String, SearchFields> map, Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();

		for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if(details.getKey().replace("%20","").trim().equals("displayName")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(permission.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(permission.displayName.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(permission.displayName.ne(details.getValue().getSearchValue()));
			}
            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(permission.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(permission.name.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(permission.name.ne(details.getValue().getSearchValue()));
			}
		}
		
		return builder;
	}
	
	public Map<String,String> parseRolepermissionJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("permissionId", keysString);
		return joinColumnMap;
		
	}
	
	public Map<String,String> parseUserpermissionJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("permissionId", keysString);
		return joinColumnMap;
		
	}
}
