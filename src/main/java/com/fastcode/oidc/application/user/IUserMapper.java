package com.fastcode.oidc.application.user;

import com.fastcode.oidc.application.user.dto.*;
import com.fastcode.oidc.domain.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    /*
    CreateUserInput => User
    User => CreateUserOutput
    UpdateUserInput => User
    User => UpdateUserOutput
    User => FindUserByIdOutput
    Permission => GetPermissionOutput
    Role => GetRoleOutput
     */

    UserEntity createUserInputToUserEntity(CreateUserInput userDto);
//    @Mappings({
//    	@Mapping(source = "role.id", target = "roleId"),
//    	@Mapping(source = "role.displayName", target = "roleDescriptiveField"),
//    	@Mapping(source = "entity.id", target = "id")
//    })
 //   @Mapping(source = "entity.organization.id", target = "tenantId")
    CreateUserOutput userEntityToCreateUserOutput(UserEntity entity);
 //   CreateUserOutput userEntityToCreateUserOutput(UserEntity entity, RoleEntity role);
    @Mappings({
    	@Mapping(source = "userProfile.emailAddress", target = "emailAddress"),
    })
    UpdateUserInput findUserWithAllFieldsByIdOutputAndUserProfileToUpdateUserInput(FindUserWithAllFieldsByIdOutput user, UserProfile userProfile);
    
    UserEntity findUserWithAllFieldsByIdOutputToUserEntity(FindUserWithAllFieldsByIdOutput user);
    
    UserProfile updateUserOutputToUserProfile(UpdateUserOutput userDto);
    
    UserProfile findUserByIdOutputToUserProfile(FindUserByIdOutput user);
    
    UserEntity updateUserInputToUserEntity(UpdateUserInput userDto);
    
//    @Mappings({
////    	@Mapping(source = "role.id", target = "roleId"),
////    	@Mapping(source = "role.displayName", target = "roleDescriptiveField"),
//    	@Mapping(source = "entity.organization.id", target = "tenantId")
//    })
    UpdateUserOutput userEntityToUpdateUserOutput(UserEntity entity);
//    UpdateUserOutput userEntityToUpdateUserOutput(UserEntity entity,RoleEntity role);
    
//    @Mappings({
////    	@Mapping(source = "role.id", target = "roleId"),
////    	@Mapping(source = "role.displayName", target = "roleDescriptiveField"),
//    	@Mapping(source = "entity.organization.id", target = "tenantId")
//    })
    FindUserByIdOutput userEntityToFindUserByIdOutput(UserEntity entity);
//    FindUserByIdOutput userEntityToFindUserByIdOutput(UserEntity entity, RoleEntity role);
     
    FindUserByNameOutput userEntityToFindUserByNameOutput(UserEntity entity);

    @Mappings({
//    	@Mapping(source = "role.id", target = "roleId"),
//    	@Mapping(source = "role.displayName", target = "roleDescriptiveField"),
 //   	@Mapping(source = "entity.version", target = "version")
    })
    FindUserWithAllFieldsByIdOutput userEntityToFindUserWithAllFieldsByIdOutput(UserEntity entity);
 //   FindUserWithAllFieldsByIdOutput userEntityToFindUserWithAllFieldsByIdOutput(UserEntity entity, RoleEntity role);
  
}
