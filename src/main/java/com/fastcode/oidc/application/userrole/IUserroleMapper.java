package com.fastcode.oidc.application.userrole;

import com.fastcode.oidc.application.userrole.dto.*;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.UserroleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IUserroleMapper {

   UserroleEntity createUserroleInputToUserroleEntity(CreateUserroleInput userroleDto);
   
   @Mappings({ 
   @Mapping(source = "user.emailAddress", target = "userDescriptiveField"),
   @Mapping(source = "user.id", target = "userId"),  
   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                   
   })
   CreateUserroleOutput userAndRoleEntityToCreateUserroleOutput(UserEntity user, RoleEntity role);

   UserroleEntity updateUserroleInputToUserroleEntity(UpdateUserroleInput userroleDto);

   @Mappings({ 
   @Mapping(source = "user.emailAddress", target = "userDescriptiveField"),
   @Mapping(source = "user.id", target = "userId"),  
   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                  
   })
   UpdateUserroleOutput userroleEntityToUpdateUserroleOutput(UserroleEntity entity);

   @Mappings({ 
   @Mapping(source = "user.emailAddress", target = "userDescriptiveField"),
   @Mapping(source = "user.id", target = "userId"),  
   @Mapping(source = "role.name", target = "roleDescriptiveField"),                   
   @Mapping(source = "role.id", target = "roleId")                  
   })
   FindUserroleByIdOutput userroleEntityToFindUserroleByIdOutput(UserroleEntity entity);

   @Mappings({
   @Mapping(source = "userrole.roleId", target = "userroleRoleId"),
   @Mapping(source = "userrole.userId", target = "userroleUserId")
   })
   GetUserOutput userEntityToGetUserOutput(UserEntity user, UserroleEntity userrole);
 
   @Mappings({
   @Mapping(source = "userrole.userId", target = "userroleUserId"),
   @Mapping(source = "userrole.roleId", target = "userroleRoleId"),
   @Mapping(source = "role.id", target = "id")
   })
   GetRoleOutput roleEntityToGetRoleOutput(RoleEntity role, UserroleEntity userrole);
 
}
