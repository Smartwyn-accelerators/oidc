package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.user.dto.*;
import com.fastcode.oidc.application.userpermission.IUserpermissionAppService;
import com.fastcode.oidc.application.userpermission.dto.FindUserpermissionByIdOutput;
import com.fastcode.oidc.application.userrole.IUserroleAppService;
import com.fastcode.oidc.application.userrole.dto.FindUserroleByIdOutput;
import com.fastcode.oidc.commons.application.OffsetBasedPageRequest;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.security.JWTAppService;
import com.fastcode.oidc.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UserController {

	@Autowired
	private IUserAppService _userAppService;

	@Autowired
	private IUserpermissionAppService  _userpermissionAppService;

	@Autowired
	private IUserroleAppService  _userroleAppService;

	@Autowired
	private PasswordEncoder pEncoder;

	@Autowired
	private JWTAppService _jwtAppService;

	@Autowired
	private AuthLoggingHelper logHelper;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private OIDCPropertiesConfiguration env;

	public UserController(IUserAppService userAppService, IUserpermissionAppService userpermissionAppService,
			IUserroleAppService userroleAppService, PasswordEncoder pEncoder, JWTAppService jwtAppService, AuthLoggingHelper logHelper) {
		super();
		this._userAppService = userAppService;
		this._userpermissionAppService = userpermissionAppService;
		this._userroleAppService = userroleAppService;
		this._jwtAppService = jwtAppService;
		this.pEncoder = pEncoder;
		this.logHelper = logHelper;
	}

	@PreAuthorize("hasAnyAuthority('USERENTITY_READ')")
	@RequestMapping(value = "/getProfile",method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile() {
		UserEntity user = _userAppService.getUser();
		FindUserByIdOutput currentuser = _userAppService.findById(user.getId());
		return new ResponseEntity(_userAppService.getProfile(currentuser), HttpStatus.OK);
	}

	// CRUD Operations
	// ------------ Create a user ------------
	@PreAuthorize("hasAnyAuthority('USERENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreateUserOutput> create(@RequestBody @Valid CreateUserInput user) throws Exception {
		 
		UserEntity loggedInUser = _userAppService.getUser();
		FindUserByNameOutput foundUser = _userAppService.findByUserName(user.getEmailAddress());

		if (foundUser != null) {
			logHelper.getLogger().error("There already exists a user with a email=%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with a email=%s", user.getEmailAddress()));
		}

		foundUser = _userAppService.findByEmailAddress(user.getEmailAddress());
		if (foundUser != null) {
			logHelper.getLogger().error("There already exists a user with a email=%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with a email=%s", user.getEmailAddress()));
		}

		CreateUserOutput output=_userAppService.create(user);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete a user ------------
	@PreAuthorize("hasAnyAuthority('USERENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		UserEntity loggedInUser = _userAppService.getUser();
		FindUserByIdOutput existing = _userAppService.findById(Long.valueOf(id));
		Optional.ofNullable(existing).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a user with a id=%s", id)));

		_userAppService.delete(Long.valueOf(id));
	}

	// ------------ Update user ------------
	@PreAuthorize("hasAnyAuthority('USERENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdateUserOutput> update(@PathVariable String id, @RequestBody @Valid UpdateUserInput user) {

		UserEntity loggedInUser = _userAppService.getUser();
		
		FindUserWithAllFieldsByIdOutput currentUser = _userAppService.findWithAllFieldsById(Long.valueOf(id));
		Optional.ofNullable(currentUser).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. User with id=%s not found.", id)));

		FindUserByNameOutput foundUser = _userAppService.findByUserName(user.getEmailAddress());

		if (foundUser != null && foundUser.getId() != user.getId()) {
			logHelper.getLogger().error("There already exists a user with a email=%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with a email=%s", user.getEmailAddress()));
		}

		foundUser = _userAppService.findByEmailAddress(user.getEmailAddress());
		if (foundUser != null && foundUser.getId() != user.getId()) {
			logHelper.getLogger().error("There already exists a user with a email=%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with a email=%s", user.getEmailAddress()));
		}


		return new ResponseEntity(_userAppService.update(Long.valueOf(id),user), HttpStatus.OK);
	}

	// ----- Update User profile -----------
	//@PreAuthorize("hasAnyAuthority('USERENTITY_UPDATE')")
	@RequestMapping(value = "/updateProfile", method = RequestMethod.PUT)
	public ResponseEntity<UserProfile> updateProfile(@RequestBody @Valid UserProfile userProfile) {
		UserEntity user = _userAppService.getUser();

		FindUserByNameOutput userOutput = _userAppService.findByEmailAddress(userProfile.getEmailAddress());
		
		if(userOutput != null && userOutput.getId() !=user.getId())
		{
			logHelper.getLogger().error("There already exists a user with a email=%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with a email=%s", user.getEmailAddress()));
		}

		userOutput = _userAppService.findByUserName(userProfile.getEmailAddress());
		if(userOutput != null && userOutput.getId() !=user.getId())
		{
			logHelper.getLogger().error("There already exists a user with email =%s", user.getEmailAddress());
			throw new EntityExistsException(
					String.format("There already exists a user with email =%s", user.getEmailAddress()));
		}
		FindUserWithAllFieldsByIdOutput currentUser = _userAppService.findWithAllFieldsById(user.getId());
		return new ResponseEntity(_userAppService.updateUserProfile(currentUser,userProfile), HttpStatus.OK);
	}

	// ------------ Retrieve a user ------------
	@PreAuthorize("hasAnyAuthority('USERENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindUserByIdOutput> findById(@PathVariable String id) {
		UserEntity loggedInUser = _userAppService.getUser();
		FindUserByIdOutput output = _userAppService.findById(Long.valueOf(id));
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a user with a id=%s", id)));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('USERENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		if(sort == null || sort.isEmpty()) {
			sort = Sort.by(Sort.Direction.ASC, "userName");
		}
		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return ResponseEntity.ok(_userAppService.find(searchCriteria,Pageable));
	}

	@PreAuthorize("hasAnyAuthority('USERENTITY_READ')")
	@RequestMapping(value = "/{userid}/userpermission", method = RequestMethod.GET)
	public ResponseEntity getUserpermission(@PathVariable String userid, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {

		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_userAppService.parseUserpermissionJoinColumn(userid);
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

		searchCriteria.setJoinColumns(joinColDetails);

		List<FindUserpermissionByIdOutput> output = _userpermissionAppService.find(searchCriteria,pageable);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Users not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}   

	@PreAuthorize("hasAnyAuthority('USERENTITY_READ')")
	@RequestMapping(value = "/{id}/userrole", method = RequestMethod.GET)
	public ResponseEntity getUserrole(@PathVariable String id, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit),sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_userAppService.parseUserroleJoinColumn(id);
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

		searchCriteria.setJoinColumns(joinColDetails);

		List<FindUserroleByIdOutput> output = _userroleAppService.find(searchCriteria,pageable);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Users not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}   


}