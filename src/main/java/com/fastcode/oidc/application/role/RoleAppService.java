package com.fastcode.oidc.application.role;

import com.fastcode.oidc.application.role.dto.*;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchFields;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.core.authorization.role.IRoleManager;
import com.fastcode.oidc.domain.model.QRoleEntity;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.UserEntity;
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
public class RoleAppService implements IRoleAppService{

	static final int case1=1;
	static final int case2=2;
	static final int case3=3;
	
	@Autowired
	private IRoleManager _roleManager;
	
	@Autowired
	@Lazy
	private IUserAppService _userAppService;

	@Autowired
	private IRoleMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateRoleOutput create(CreateRoleInput input) {

		RoleEntity role = mapper.createRoleInputToRoleEntity(input);
		RoleEntity createdRole = _roleManager.create(role);

		return mapper.roleEntityToCreateRoleOutput(createdRole);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateRoleOutput update(Long roleId, UpdateRoleInput input) {

		RoleEntity role = mapper.updateRoleInputToRoleEntity(input);
		RoleEntity updatedRole = _roleManager.update(role);
		
		return mapper.roleEntityToUpdateRoleOutput(updatedRole);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long roleId) {

		RoleEntity existing = _roleManager.findById(roleId) ;
		_roleManager.delete(existing);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRoleByIdOutput findById(Long roleId) {

		RoleEntity foundRole = _roleManager.findById(roleId);
		
		if (foundRole == null)  
			return null ; 
 	   
 	   FindRoleByIdOutput output=mapper.roleEntityToFindRoleByIdOutput(foundRole); 
		return output;
	}
	

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindRoleByNameOutput findByRoleName(String roleName) {

		RoleEntity foundRole = _roleManager.findByRoleName(roleName);

		if (foundRole == null) {
			return null;
		}
		return mapper.roleEntityToFindRoleByNameOutput(foundRole);
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindRoleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception  {

		Page<RoleEntity> foundRole = _roleManager.findAll(search(search), pageable);
		List<RoleEntity> roleList = foundRole.getContent();
		Iterator<RoleEntity> roleIterator = roleList.iterator(); 
		List<FindRoleByIdOutput> output = new ArrayList<>();

		while (roleIterator.hasNext()) {
			output.add(mapper.roleEntityToFindRoleByIdOutput(roleIterator.next()));
		}
		return output;
	}
	
	public BooleanBuilder search(SearchCriteria search) throws Exception {

		QRoleEntity role= QRoleEntity.roleEntity;
		if(search != null) {
			if(search.getType()==case1)
			{
				return searchAllProperties(role, search.getValue(),search.getOperator());
			}
			else if(search.getType()==case2)
			{
				List<String> keysList = new ArrayList<String>();
				for(SearchFields f: search.getFields())
				{
					keysList.add(f.getFieldName());
				}
				checkProperties(keysList);
				return searchSpecificProperty(role,keysList,search.getValue(),search.getOperator());
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
				return searchKeyValuePair(role, map,joinColumn);
			}

		}
		return null;
	}
	
	public BooleanBuilder searchAllProperties(QRoleEntity role,String value,String operator) {
		BooleanBuilder builder = new BooleanBuilder();

		if(operator.equals("contains")) {
			builder.or(role.displayName.likeIgnoreCase("%"+ value + "%"));
			builder.or(role.name.likeIgnoreCase("%"+ value + "%"));
		}
		else if(operator.equals("equals"))
		{
        	builder.or(role.displayName.eq(value));
        	builder.or(role.name.eq(value));
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
		 list.get(i).replace("%20","").trim().equals("user")
		)) 
		{
		 throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!" );
		}
		}
	}
	
	public BooleanBuilder searchSpecificProperty(QRoleEntity role,List<String> list,String value,String operator)  {
		BooleanBuilder builder = new BooleanBuilder();
		
		for (int i = 0; i < list.size(); i++) {
		
            if(list.get(i).replace("%20","").trim().equals("displayName")) {
				if(operator.equals("contains"))
					builder.or(role.displayName.likeIgnoreCase("%"+ value + "%"));
				else if(operator.equals("equals"))
					builder.or(role.displayName.eq(value));
			}
            if(list.get(i).replace("%20","").trim().equals("name")) {
				if(operator.equals("contains"))
					builder.or(role.name.likeIgnoreCase("%"+ value + "%"));
				else if(operator.equals("equals"))
					builder.or(role.name.eq(value));
			}
		}
		return builder;
	}
	
	public BooleanBuilder searchKeyValuePair(QRoleEntity role, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();

		for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if(details.getKey().replace("%20","").trim().equals("displayName")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(role.displayName.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(role.displayName.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(role.displayName.ne(details.getValue().getSearchValue()));
			}
            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains"))
					builder.and(role.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				else if(details.getValue().getOperator().equals("equals"))
					builder.and(role.name.eq(details.getValue().getSearchValue()));
				else if(details.getValue().getOperator().equals("notEqual"))
					builder.and(role.name.ne(details.getValue().getSearchValue()));
			}
		}

		return builder;
	}

	public Map<String,String> parseRolepermissionJoinColumn(String keysString) {
	
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("roleId", keysString);
		return joinColumnMap;
	}

	public Map<String,String> parseUserroleJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("roleId", keysString);
		return joinColumnMap;
	}
	
}