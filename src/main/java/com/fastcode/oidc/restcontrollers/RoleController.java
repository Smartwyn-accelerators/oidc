package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.role.IRoleAppService;
import com.fastcode.oidc.application.role.dto.*;
import com.fastcode.oidc.application.rolepermission.IRolepermissionAppService;
import com.fastcode.oidc.application.rolepermission.dto.FindRolepermissionByIdOutput;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.userrole.IUserroleAppService;
import com.fastcode.oidc.application.userrole.dto.FindUserroleByIdOutput;
import com.fastcode.oidc.commons.application.OffsetBasedPageRequest;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/role")
@SuppressWarnings({"rawtypes", "unchecked"})
public class RoleController {

	@Autowired
	private IUserroleAppService  _userroleAppService;
	
	@Autowired
	private IUserAppService  _userService;
	
	@Autowired
	private IRoleAppService _roleAppService;

	@Autowired
	private IRolepermissionAppService  _rolepermissionAppService;

	@Autowired
	private AuthLoggingHelper logHelper;

	@Autowired
	private OIDCPropertiesConfiguration env;

	public RoleController(IRoleAppService appService, AuthLoggingHelper helper,
						  IUserroleAppService userroleAppService, IRolepermissionAppService rolepermissionAppService) {

		this._roleAppService= appService;
		this.logHelper = helper;
		this._userroleAppService = userroleAppService;
		this._rolepermissionAppService = rolepermissionAppService;
	}

	// CRUD Operations
	// ------------ Create a role ------------
	@PreAuthorize("hasAnyAuthority('ROLEENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreateRoleOutput> create(@RequestBody @Valid CreateRoleInput role) {

		UserEntity loggedInUser = _userService.getUser();
		FindRoleByNameOutput foundRole = _roleAppService.findByRoleName(role.getName());

		if (Optional.ofNullable(foundRole).isPresent()) {
			logHelper.getLogger().error("There already exists a role with name=%s", role.getName());
			throw new EntityExistsException(
					String.format("There already exists a role with name=%s", role.getName()));
		}

		CreateRoleOutput output = _roleAppService.create(role);

		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete role ------------
	@PreAuthorize("hasAnyAuthority('ROLEENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		
		UserEntity loggedInUser = _userService.getUser();
		FindRoleByIdOutput output = _roleAppService.findById(Long.valueOf(id));
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a role with a id=%s", id)));
		
		_roleAppService.delete(Long.valueOf(id));
	}

	// ------------ Update role ------------
	@PreAuthorize("hasAnyAuthority('ROLEENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdateRoleOutput> update(@PathVariable String id, @RequestBody @Valid UpdateRoleInput role) {
		
		UserEntity loggedInUser = _userService.getUser();
		FindRoleByIdOutput currentRole = _roleAppService.findById(Long.valueOf(id));
		Optional.ofNullable(currentRole).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. Role with id=%s not found.", id)));

		FindRoleByNameOutput foundRole = _roleAppService.findByRoleName(role.getName());

		if (foundRole != null && foundRole.getId() != role.getId()) {
			logHelper.getLogger().error("There already exists a role with a name=%s", role.getName());
			throw new EntityExistsException(
					String.format("There already exists a role with a name=%s", role.getName()));
		}
		
		role.setVersion(currentRole.getVersion());
		return new ResponseEntity(_roleAppService.update(Long.valueOf(id),role), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindRoleByIdOutput> findById(@PathVariable String id) {
		
		UserEntity loggedInUser = _userService.getUser();
		FindRoleByIdOutput output = _roleAppService.findById(Long.valueOf(id));
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a role with a id=%s", id)));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }
	
		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		List<FindRoleByIdOutput> roles = _roleAppService.find(searchCriteria, Pageable);
		return ResponseEntity.ok(roles);
	}

	@PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{roleid}/userrole", method = RequestMethod.GET)
	public ResponseEntity getUserrole(@PathVariable String roleid, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_roleAppService.parseUserroleJoinColumn(roleid);
		
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));
		searchCriteria.setJoinColumns(joinColDetails);

		List<FindUserroleByIdOutput> output = _userroleAppService.find(searchCriteria,pageable);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));
		
		return new ResponseEntity(output, HttpStatus.OK);
	} 

	@PreAuthorize("hasAnyAuthority('ROLEENTITY_READ')")
	@RequestMapping(value = "/{roleid}/rolepermission", method = RequestMethod.GET)
	public ResponseEntity getRolepermission(@PathVariable String roleid, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_roleAppService.parseRolepermissionJoinColumn(roleid);
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

		searchCriteria.setJoinColumns(joinColDetails);

		List<FindRolepermissionByIdOutput> output = _rolepermissionAppService.find(searchCriteria,pageable);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}   

}