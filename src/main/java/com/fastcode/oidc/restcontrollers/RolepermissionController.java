package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.application.rolepermission.IRolepermissionAppService;
import com.fastcode.oidc.application.rolepermission.dto.*;
import com.fastcode.oidc.application.user.IUserAppService;
import com.fastcode.oidc.commons.application.OffsetBasedPageRequest;
import com.fastcode.oidc.commons.search.SearchCriteria;
import com.fastcode.oidc.commons.search.SearchUtils;
import com.fastcode.oidc.domain.model.RolepermissionId;
import com.fastcode.oidc.domain.model.UserEntity;
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
@RequestMapping("/rolepermission")
@SuppressWarnings({"rawtypes", "unchecked"})
public class RolepermissionController {

	@Autowired
	private IRolepermissionAppService _rolepermissionAppService;

	@Autowired
	private IUserAppService _userAppService;
	
	@Autowired
	private OIDCPropertiesConfiguration env;

	public RolepermissionController(IRolepermissionAppService rolepermissionAppService) {
		super();
		this._rolepermissionAppService = rolepermissionAppService;
	}

	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_CREATE')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<CreateRolepermissionOutput> create(@RequestBody @Valid CreateRolepermissionInput rolepermission) {
		UserEntity loggedInUser = _userAppService.getUser();
		CreateRolepermissionOutput output=_rolepermissionAppService.create(rolepermission);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	// ------------ Delete rolepermission ------------
	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_DELETE')")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String id) {
		RolepermissionId rolepermissionId =_rolepermissionAppService.parseRolepermissionKey(id);
		Optional.ofNullable(rolepermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindRolepermissionByIdOutput output = _rolepermissionAppService.findById(rolepermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a rolepermission with a id=%s", id)));

		_rolepermissionAppService.delete(rolepermissionId);
	}

	// ------------ Update rolepermission ------------
	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_UPDATE')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UpdateRolepermissionOutput> update(@PathVariable String id, @RequestBody @Valid UpdateRolepermissionInput rolepermission) {
		RolepermissionId rolepermissionId =_rolepermissionAppService.parseRolepermissionKey(id);
		Optional.ofNullable(rolepermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindRolepermissionByIdOutput currentRolepermission = _rolepermissionAppService.findById(rolepermissionId);
		Optional.ofNullable(currentRolepermission).orElseThrow(() -> new EntityNotFoundException(String.format("Unable to update. Rolepermission with id=%s not found.", id)));

        rolepermission.setVersion(currentRolepermission.getVersion());
		return new ResponseEntity(_rolepermissionAppService.update(rolepermissionId,rolepermission), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<FindRolepermissionByIdOutput> findById(@PathVariable String id) {
		RolepermissionId rolepermissionId =_rolepermissionAppService.parseRolepermissionKey(id);
		Optional.ofNullable(rolepermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		FindRolepermissionByIdOutput output = _rolepermissionAppService.findById(rolepermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_READ')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity find(@RequestParam(value="search", required=false) String search, @RequestParam(value = "offset", required=false) String offset, @RequestParam(value = "limit", required=false) String limit, Sort sort) throws Exception {
		if (offset == null) { offset = env.getFastCodeOffsetDefault(); }
		if (limit == null) { limit = env.getFastCodeLimitDefault(); }

		Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
		SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

		return ResponseEntity.ok(_rolepermissionAppService.find(searchCriteria,Pageable));
	}

	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/permission", method = RequestMethod.GET)
	public ResponseEntity<GetPermissionOutput> getPermission(@PathVariable String id) {
		
		RolepermissionId rolepermissionId =_rolepermissionAppService.parseRolepermissionKey(id);
		Optional.ofNullable(rolepermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));
		
		UserEntity loggedInUser = _userAppService.getUser();
		GetPermissionOutput output= _rolepermissionAppService.getPermission(rolepermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLEPERMISSIONENTITY_READ')")
	@RequestMapping(value = "/{id}/role", method = RequestMethod.GET)
	public ResponseEntity<GetRoleOutput> getRole(@PathVariable String id) {
		
		RolepermissionId rolepermissionId =_rolepermissionAppService.parseRolepermissionKey(id);
		Optional.ofNullable(rolepermissionId).orElseThrow(() -> new EntityNotFoundException(String.format("Invalid id=%s", id)));

		UserEntity loggedInUser = _userAppService.getUser();
		GetRoleOutput output= _rolepermissionAppService.getRole(rolepermissionId);
		Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

		return new ResponseEntity(output, HttpStatus.OK);
	}


}

