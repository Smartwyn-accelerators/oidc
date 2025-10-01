package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.permission.IPermissionAppService;
import com.fastcode.oidc.application.permission.dto.*;
import com.fastcode.oidc.application.rolepermission.IRolepermissionAppService;
import com.fastcode.oidc.application.rolepermission.dto.FindRolepermissionByIdOutput;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.application.userpermission.IUserpermissionAppService;
import com.fastcode.oidc.application.userpermission.dto.FindUserpermissionByIdOutput;
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

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/permission")
@SuppressWarnings({"rawtypes", "unchecked"})
public class PermissionController {

	@Autowired
	private IPermissionAppService _permissionAppService;
	
	@Autowired 
	private IUserAppService userService;

	@Autowired
	private IRolepermissionAppService  _rolepermissionAppService;
	
	@Autowired
	private IUserpermissionAppService  _userpermissionAppService;
	
	@Autowired
	private AuthLoggingHelper logHelper;

	@Autowired
	private OIDCPropertiesConfiguration env;

	public PermissionController(IPermissionAppService appService, AuthLoggingHelper helper, IUserAppService userService,
								IUserpermissionAppService userpermissionAppService, IRolepermissionAppService rolepermissionAppService) {

		this._permissionAppService= appService;
		this.logHelper = helper;
		this.userService = userService;
		this._userpermissionAppService = userpermissionAppService;
		this._rolepermissionAppService = rolepermissionAppService;
	}

	// CRUD Operations

	// ------------ Create a permission ------------
	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreatePermissionOutput> create(@RequestBody @Valid CreatePermissionInput permission) {

		UserEntity loggedInUser = userService.getUser();
		FindPermissionByNameOutput existing = _permissionAppService.findByPermissionName(permission.getName());

		if (Optional.ofNullable(existing).isPresent()) {
			logHelper.getLogger().error("There already exists a permission with name=%s", permission.getName());
			throw new EntityExistsException(
					String.format("There already exists a permission with name=%s", permission.getName()));
		}

		CreatePermissionOutput output=_permissionAppService.create(permission);
		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete permission ------------
	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		
		UserEntity loggedInUser = userService.getUser();
		FindPermissionByIdOutput output = _permissionAppService.findById(Long.valueOf(id));
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a permission with a id=%s", id)));

		_permissionAppService.delete(Long.valueOf(id));
	}

	// ------------ Update permission ------------
	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdatePermissionOutput> update(@PathVariable String id, @RequestBody @Valid UpdatePermissionInput permission) {
		
		UserEntity loggedInUser = userService.getUser();
		FindPermissionByIdOutput currentPermission = _permissionAppService.findById(Long.valueOf(id));
		Optional.ofNullable(currentPermission).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. Permission with id=%s not found.", id)));
		
		FindPermissionByNameOutput foundPermission = _permissionAppService.findByPermissionName(permission.getName());

		if (foundPermission != null && foundPermission.getId() != permission.getId()) {
			logHelper.getLogger().error("There already exists a role with a name=%s", permission.getName());
			throw new EntityExistsException(
					String.format("There already exists a role with a name=%s", permission.getName()));
		}
		permission.setVersion(currentPermission.getVersion());
		return new ResponseEntity(_permissionAppService.update(Long.valueOf(id),permission), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindPermissionByIdOutput> findById(@PathVariable String id) {
		
		UserEntity loggedInUser = userService.getUser();
		FindPermissionByIdOutput output = _permissionAppService.findById(Long.valueOf(id));
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a permission with a id=%s", id)));

		return new ResponseEntity(output, HttpStatus.OK);
	}

    @PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {

		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		if(sort == null || sort.isEmpty()) {
			sort = Sort.by(Sort.Direction.ASC, "name");
		}
		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return ResponseEntity.ok(_permissionAppService.find(searchCriteria,Pageable));
	}

	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{permissionid}/rolepermission", method = RequestMethod.GET)
	public ResponseEntity getRolepermission(@PathVariable String permissionid, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		if(sort == null || sort.isEmpty()) {
			sort = Sort.by(Sort.Direction.ASC, "name");
			}
		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_permissionAppService.parseRolepermissionJoinColumn(permissionid);
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

		//		if(joinColDetails== null)
		//		{
		//			logHelper.getLogger().error("Invalid Join Column");
		//			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
		//		}

		searchCriteria.setJoinColumns(joinColDetails);

		List<FindRolepermissionByIdOutput> output = _rolepermissionAppService.find(searchCriteria,pageable);

		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Permissions not exists")));

		//    	if (output == null) {
		//			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
		//		}

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('PERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{permissionid}/userpermission", method = RequestMethod.GET)
	public ResponseEntity getUserpermission(@PathVariable String permissionid, @RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort)throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		//		if (sort.isUnsorted()) { sort = new Sort(Sort.Direction.fromString(env.getProperty("fastCode.sort.direction.default")), new String[]{env.getProperty("fastCode.sort.property.default")}); }

		Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
		Map<String,String> joinColDetails=_permissionAppService.parseUserpermissionJoinColumn(permissionid);
		Optional.ofNullable(joinColDetails).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));

		//		if(joinColDetails== null)
		//		{
		//			logHelper.getLogger().error("Invalid Join Column");
		//			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
		//		}
		searchCriteria.setJoinColumns(joinColDetails);

		List<FindUserpermissionByIdOutput> output = _userpermissionAppService.find(searchCriteria,pageable);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Permissions not exists")));

		//		if (output == null) {
		//			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
		//		}

		return new ResponseEntity(output, HttpStatus.OK);
	} 
}
