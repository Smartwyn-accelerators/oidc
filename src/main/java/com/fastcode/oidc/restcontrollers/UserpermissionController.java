package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.user.dto.FindUserByIdOutput;
import com.fastcode.oidc.application.userpermission.IUserpermissionAppService;
import com.fastcode.oidc.application.userpermission.dto.*;
import com.fastcode.oidc.commons.application.OffsetBasedPageRequest;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserpermissionId;
import com.fastcode.oidc.security.JWTAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/userpermission")
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserpermissionController {

	@Autowired
	private IUserpermissionAppService _userpermissionAppService;

	@Autowired
	private IUserAppService _userAppService;

	@Autowired
	private JWTAppService _jwtAppService;

	@Autowired
	private OIDCPropertiesConfiguration env;

	public UserpermissionController(IUserpermissionAppService userpermissionAppService, IUserAppService userAppService,
			JWTAppService jwtAppService) {

		this._userpermissionAppService = userpermissionAppService;
		this._userAppService = userAppService;
		this._jwtAppService = jwtAppService;
	}

	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreateUserpermissionOutput> create(@RequestBody @Valid CreateUserpermissionInput userpermission) {
		UserEntity loggedInUser = _userAppService.getUser();
		CreateUserpermissionOutput output =_userpermissionAppService.create(userpermission);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

		FindUserByIdOutput foundUser =_userAppService.findById(output.getUserId());
		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());

		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete userrpermission ------------
	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		
		UserpermissionId userpermissionId =_userpermissionAppService.parseUserpermissionKey(id);
		Optional.ofNullable(userpermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserpermissionByIdOutput output = _userpermissionAppService.findById(userpermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a userpermission with a id=%s", id)));

		_userpermissionAppService.delete(userpermissionId);

		FindUserByIdOutput foundUser =_userAppService.findById(output.getUserId());
		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());
	}

	// ------------ Update userpermission ------------
	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdateUserpermissionOutput> update(@PathVariable String id, @RequestBody @Valid UpdateUserpermissionInput userpermission) {
		
		UserpermissionId userpermissionId =_userpermissionAppService.parseUserpermissionKey(id);
		Optional.ofNullable(userpermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserpermissionByIdOutput currentUserpermission = _userpermissionAppService.findById(userpermissionId);
		Optional.ofNullable(currentUserpermission).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. Userpermission with id=%s not found.", id)));

		FindUserByIdOutput foundUser =_userAppService.findById(currentUserpermission.getUserId());
		_jwtAppService.deleteAllUserTokens(foundUser.getEmailAddress());

		userpermission.setVersion(currentUserpermission.getVersion());
		return new ResponseEntity(_userpermissionAppService.update(userpermissionId,userpermission), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindUserpermissionByIdOutput> findById(@PathVariable String id) {
		
		UserpermissionId userpermissionId =_userpermissionAppService.parseUserpermissionKey(id);
		Optional.ofNullable(userpermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindUserpermissionByIdOutput output = _userpermissionAppService.findById(userpermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a userpermission with a id=%s", id)));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {
		
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return ResponseEntity.ok(_userpermissionAppService.find(searchCriteria,Pageable));
	}

	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
	public ResponseEntity<GetUserOutput> getUser(@PathVariable String id) {
		
		UserpermissionId userpermissionId =_userpermissionAppService.parseUserpermissionKey(id);
		Optional.ofNullable(userpermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		GetUserOutput output= _userpermissionAppService.getUser(userpermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Users not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('USERPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/permission", method = RequestMethod.GET)
	public ResponseEntity<GetPermissionOutput> GetPermission(@PathVariable String id) {
		
		UserpermissionId userpermissionId =_userpermissionAppService.parseUserpermissionKey(id);
		Optional.ofNullable(userpermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		GetPermissionOutput output= _userpermissionAppService.getPermission(userpermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Permissions not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

}

