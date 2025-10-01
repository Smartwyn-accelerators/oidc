package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.user.dto.FindUserByIdOutput;
import com.fastcode.oidc.application.userrole.IUserroleAppService;
import com.fastcode.oidc.application.userrole.dto.*;
import com.fastcode.oidc.commons.application.OffsetBasedPageRequest;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserroleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/userrole")
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserroleController {

	@Autowired
	private IUserroleAppService _userroleAppService;
    
    @Autowired
	private IUserAppService  _userAppService;

	@Autowired
	private OIDCPropertiesConfiguration env;
	
	 public UserroleController(IUserroleAppService userroleAppService, IUserAppService userAppService) {
		super();
		this._userroleAppService = userroleAppService;
		this._userAppService = userAppService;
	}

    @PreAuthorize("hasAnyAuthority('USERROLEENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreateUserroleOutput> create(@RequestBody @Valid CreateUserroleInput userrole) {
    	UserEntity loggedInUser = _userAppService.getUser();
    	CreateUserroleOutput output=_userroleAppService.create(userrole);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

		FindUserByIdOutput foundUser =_userAppService.findById(output.getUserId());
//		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());
	
		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete userrole ------------
	@PreAuthorize("hasAnyAuthority('USERROLEENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		
		UserroleId userroleid =_userroleAppService.parseUserroleKey(id);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserroleByIdOutput output = _userroleAppService.findById(userroleid);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a userrole with a id=%s", id)));
		
		_userroleAppService.delete(userroleid);
		 
	  	FindUserByIdOutput foundUser =_userAppService.findById(output.getUserId());
//		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());
    }
	
	// ------------ Update userrole ------------
	@PreAuthorize("hasAnyAuthority('USERROLEENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdateUserroleOutput> update(@PathVariable String id, @RequestBody @Valid UpdateUserroleInput userrole) {
		
		UserroleId userroleid =_userroleAppService.parseUserroleKey(id);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserroleByIdOutput currentUserrole = _userroleAppService.findById(userroleid);
		Optional.ofNullable(currentUserrole).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. Userrole with id=%s not found.", id)));
	
		FindUserByIdOutput foundUser =_userAppService.findById(currentUserrole.getUserId());
//		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());
		
		userrole.setVersion(currentUserrole.getVersion());
		return new ResponseEntity(_userroleAppService.update(userroleid,userrole), HttpStatus.OK);
	}

    @PreAuthorize("hasAnyAuthority('USERROLEENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindUserroleByIdOutput> findById(@PathVariable String id) {
		
    	UserroleId userroleid =_userroleAppService.parseUserroleKey(id);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserroleByIdOutput output = _userroleAppService.findById(userroleid);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}
    
    @PreAuthorize("hasAnyAuthority('USERROLEENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {

		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		
		return ResponseEntity.ok(_userroleAppService.find(searchCriteria,Pageable));
	}
	
	@PreAuthorize("hasAnyAuthority('USERROLEENTITY_READ')")
	@RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
	public ResponseEntity<GetUserOutput> getUser(@PathVariable String id) {
		
		UserroleId userroleid =_userroleAppService.parseUserroleKey(id);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		GetUserOutput output= _userroleAppService.getUser(userroleid);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Users not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('USERROLEENTITY_READ')")
	@RequestMapping(value = "/{id}/role", method = RequestMethod.GET)
	public ResponseEntity<GetRoleOutput> getRole(@PathVariable String id) {
		
		UserroleId userroleid =_userroleAppService.parseUserroleKey(id);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		GetRoleOutput output= _userroleAppService.getRole(userroleid);
		Optional.ofNullable(userroleid).orElseThrow(() -> new EntityNotFoundException(String.format("Role not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

}

