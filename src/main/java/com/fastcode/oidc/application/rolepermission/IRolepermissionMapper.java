package com.fastcode.oidc.application.rolepermission;

import com.fastcode.oidc.application.rolepermission.dto.*;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.RolepermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IRolepermissionMapper {

    RolepermissionEntity createRolepermissionInputToRolepermissionEntity(CreateRolepermissionInput rolepermissionDto);
   
//   @Mappings({ 
//   @Mapping(source = "permission.name", target = "permissionDescriptiveField"),                   
//   @Mapping(source = "permission.id", target = "permissionId"),                   
//   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
//   @Mapping(source = "role.id", target = "roleId"),                   
//   }) 
//   CreateRolepermissionOutput rolepermissionEntityToCreateRolepermissionOutput(RolepermissionEntity entity);
   
   @Mappings({ 
	   @Mapping(source = "permission.name", target = "permissionDescriptiveField"),                   
	   @Mapping(source = "permission.id", target = "permissionId"),                   
	   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
	   @Mapping(source = "role.id", target = "roleId"),                   
   })
   CreateRolepermissionOutput roleEntityAndPermissionEntityToCreateRolepermissionOutput(RoleEntity role, PermissionEntity permission);

   RolepermissionEntity updateRolepermissionInputToRolepermissionEntity(UpdateRolepermissionInput rolepermissionDto);

   @Mappings({ 
   @Mapping(source = "permission.name", target = "permissionDescriptiveField"),                   
   @Mapping(source = "permission.id", target = "permissionId"),                   
   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                   
   })
   UpdateRolepermissionOutput rolepermissionEntityToUpdateRolepermissionOutput(RolepermissionEntity entity);
   
   @Mappings({ 
   @Mapping(source = "permission.name", target = "permissionDescriptiveField"),                   
   @Mapping(source = "permission.id", target = "permissionId"),                   
   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                   
   })
   FindRolepermissionByIdOutput rolepermissionEntityToFindRolepermissionByIdOutput(RolepermissionEntity entity);

   @Mappings({
   @Mapping(source = "rolepermission.permissionId", target = "rolepermissionPermissionId"),
   @Mapping(source = "rolepermission.roleId", target = "rolepermissionRoleId")
   })
   GetPermissionOutput permissionEntityToGetPermissionOutput(PermissionEntity permission, RolepermissionEntity rolepermission);
 
   @Mappings({
   @Mapping(source = "rolepermission.permissionId", target = "rolepermissionPermissionId"),
   @Mapping(source = "rolepermission.roleId", target = "rolepermissionRoleId")
   })
   GetRoleOutput roleEntityToGetRoleOutput(RoleEntity role, RolepermissionEntity rolepermission);
 

}
