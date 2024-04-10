package fr.esprit.usermanagement.services.impl;


import fr.esprit.usermanagement.utils.KeycloakConfig;
import fr.esprit.usermanagement.dtos.Role;
import fr.esprit.usermanagement.dtos.UserDto;
import fr.esprit.usermanagement.exceptions.EntityAlreadyExistException;
import fr.esprit.usermanagement.exceptions.EntityNotFoundException;
import fr.esprit.usermanagement.exceptions.ErrorOccurredException;
import fr.esprit.usermanagement.exceptions.InvalidEntityException;
import fr.esprit.usermanagement.handlers.ErrorCodes;
import fr.esprit.usermanagement.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    @Override
    public UserDto getUser(String userId) {
        Keycloak keycloak = KeycloakConfig.getInstance();
        UserRepresentation userRepresentation = keycloak.realm(KeycloakConfig.realm).users().get(userId).toRepresentation();
        return mapper(userRepresentation);
    }

    @Override
    public String getToken() {
        return KeycloakConfig.getInstance().tokenManager().getAccessTokenString();
    }

    @Override
    public ResponseEntity<?> resetUserPassword(String userId, String newPassword) {
        Keycloak keycloak = KeycloakConfig.getInstance();
        UserResource userResource = keycloak.realm(KeycloakConfig.realm).users().get(userId);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(newPassword);
        credentialRepresentation.setTemporary(false);
        userResource.resetPassword(credentialRepresentation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UserDto mapper(UserRepresentation userRepresentation){
        Keycloak keycloak = KeycloakConfig.getInstance();
        List<RoleRepresentation> roleRepresentations = keycloak.realm(KeycloakConfig.realm).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
        UserDto userDto = new UserDto();
        userDto.setId(userRepresentation.getId());
        userDto.setEmail(userRepresentation.getEmail());
        userDto.setUserName(userRepresentation.getUsername());
        userDto.setFirstName(userRepresentation.getFirstName());
        userDto.setLastName(userRepresentation.getLastName());
        userDto.setActive(userRepresentation.isEnabled());
        List<Role> roles = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            Role role = new Role();
            role.setId(roleRepresentation.getId());
            role.setName(roleRepresentation.getName());
            roles.add(role);
        }
        userDto.setRoles(roles);
        return userDto;
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        Keycloak keycloak = KeycloakConfig.getInstance();
        List<UserDto> userDtoList =  keycloak.realm(KeycloakConfig.realm).users().list().stream().map(this::mapper).toList();
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Role>> getPermissions() {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);

        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(KeycloakConfig.clientID)
                .get(0);
        List<RoleRepresentation> roleRepresentations = realmResource.clients().get(clientRepresentation.getId()).roles()
                .list();
        List<Role> roles = new ArrayList<>();

        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            Role role = new Role();
            role.setId(roleRepresentation.getId());
            role.setName(roleRepresentation.getName());
            roles.add(role);
        }

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> deleteRole(String roleName) throws InvalidEntityException, ErrorOccurredException {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        RoleResource roleResource = realmResource.roles().get(roleName);

        try {
            roleResource.toRepresentation();
        }catch (NotFoundException e) {
            throw new InvalidEntityException("Role with this name " + roleName + " is not found", ErrorCodes.INVALID_ENTITY_EXCEPTION, new ArrayList<>(Collections.singleton("Role with this id " + roleName + " is not found")));
        }

        List<UserRepresentation> users = realmResource.users().list().stream().filter(u -> {
            UserResource userResource = realmResource.users().get(u.getId());
            RoleMappingResource roleMappingResource = userResource.roles();
            return roleMappingResource.realmLevel().listAll().stream().anyMatch(r -> r.getName().equals(roleName));
        }).collect(Collectors.toList());

        if (!users.isEmpty()) {

            throw new ErrorOccurredException("Cannot delete role as it is assigned to one or more users", ErrorCodes.INVALID_ENTITY_EXCEPTION, new ArrayList<>(Collections.singleton("Cannot delete role as it is assigned to one or more users")));

        }

        List<GroupRepresentation> groups = realmResource.groups().groups().stream().filter(g -> {
            GroupResource groupResource = realmResource.groups().group(g.getId());
            RoleMappingResource roleMappingResource = groupResource.roles();
            return roleMappingResource.realmLevel().listAll().stream().anyMatch(r -> r.getName().equals(roleName));
        }).collect(Collectors.toList());

        if (!groups.isEmpty()) {
            throw new ErrorOccurredException("Cannot delete role as it is assigned to one or more groups", ErrorCodes.ERROR_OCCURRED_EXCEPTION, new ArrayList<>(Collections.singleton("Cannot delete role as it is assigned to one or more groups")));

        }

        roleResource.remove();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<?> deleteUser(String userId) {

        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        realmResource.users().get(userId).remove();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateRole(Role role) throws EntityNotFoundException {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);

        List<RoleRepresentation> existingRoles = realmResource.roles().list();
        if (existingRoles.stream().noneMatch(r -> r.getId().equals(role.getId()))) {
            throw new EntityNotFoundException("Role with this id does not exist", ErrorCodes.ENTITY_NOT_FOUND, new ArrayList<>(Collections.singleton("Role with this id does not exist")));

        }

        RoleRepresentation updatedRole = new RoleRepresentation();
        updatedRole.setId(role.getId());
        updatedRole.setName(role.getName());

        RoleRepresentation roleRepresentation = realmResource.rolesById().getRole(role.getId());
        realmResource.roles().get(roleRepresentation.getName()).update(updatedRole);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createRole(String roleName) throws EntityAlreadyExistException {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);

        List<RoleRepresentation> existingRoles = realmResource.roles().list();
        if (existingRoles.stream().anyMatch(r -> r.getName().equals(roleName))) {
            throw new EntityAlreadyExistException("Role with this name already exists", ErrorCodes.ENTITY_ALREADY_EXIST, new ArrayList<>(Collections.singleton("Role with this name already exists")));

        }

        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        role.setClientRole(false);
        realmResource.roles().create(role);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> assignCompositeRolesForRole(String roleId, List<String> rolesIds) throws EntityNotFoundException {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);

        RoleRepresentation role = realmResource.rolesById().getRole(roleId);
        RoleResource roleResource = realmResource.roles().get(role.getName());

        if (role == null) {
            throw new EntityNotFoundException("Role with ID " + roleId + " not found.", ErrorCodes.ENTITY_NOT_FOUND, new ArrayList<>(Collections.singleton("Role with ID " + roleId + " not found.")));
        }

        Set<RoleRepresentation> existingComposites = roleResource.getRoleComposites();
        List<RoleRepresentation> compositesToRemove = existingComposites.stream()
                .filter(r -> !rolesIds.contains(r.getId())).collect(Collectors.toList());
        if (!compositesToRemove.isEmpty()) {
            roleResource.deleteComposites(compositesToRemove);
        }

        List<RoleRepresentation> compositesToAdd = rolesIds.stream().map(id -> realmResource.rolesById().getRole(id))
                .filter(Objects::nonNull).collect(Collectors.toList());
        roleResource.addComposites(compositesToAdd);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<Role>> getAllRoles() {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        RolesResource rolesResource = realmResource.roles();
        List<RoleRepresentation> roleRepresentations = rolesResource.list();
        List<Role> roles = new ArrayList<>();

        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            Role role = new Role();
            role.setId(roleRepresentation.getId());
            role.setName(roleRepresentation.getName());

            Set<RoleRepresentation> compositeRoles = rolesResource.get(roleRepresentation.getName()).getRoleComposites();
            List<Role> permissions = new ArrayList<>();

            for (RoleRepresentation compositeRole : compositeRoles) {
                Role permission = new Role();
                permission.setId(compositeRole.getId());
                permission.setName(compositeRole.getName());
                permissions.add(permission);
            }

            role.setPermissions(permissions);
            roles.add(role);
        }

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> assignRolesToUser(String userId, List<String> roleIds) throws EntityNotFoundException {
        Keycloak keycloak = KeycloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeycloakConfig.realm);
        UserResource userResource = realmResource.users().get(userId);

        if (userResource == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found", ErrorCodes.ENTITY_NOT_FOUND, new ArrayList<>(Collections.singleton("User with ID " + userId + " not found")));
        }

        RoleMappingResource roleMappingResource = userResource.roles();
        RoleScopeResource roleScopeResource = roleMappingResource.realmLevel();
        List<RoleRepresentation> existingRoles = roleScopeResource.listEffective();

        for (RoleRepresentation existingRole : existingRoles) {
            if (!roleIds.contains(existingRole.getId())) {
                roleScopeResource.remove(Collections.singletonList(existingRole));
            }
        }
        List<RoleRepresentation> rolesToAdd = new ArrayList<>();
        for (String roleId : roleIds) {
            RoleRepresentation roleRepresentation = realmResource.roles().list().stream()
                    .filter(a -> a.getId().equals(roleId)).findFirst().get();
            rolesToAdd.add(roleRepresentation);
        }
        roleScopeResource.add(rolesToAdd);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> editUser(UserDto userRequest) throws EntityAlreadyExistException {
        RealmResource realmResource = KeycloakConfig.getInstance().realm(KeycloakConfig.realm);
        List<UserRepresentation> existingUsers = realmResource.users().search(userRequest.getUserName(), null, null, null,
                0, 1);
        if (!existingUsers.isEmpty()) {
            for (UserRepresentation existingUser : existingUsers) {
                if (!existingUser.getId().equals(userRequest.getId())) {
                    throw new EntityAlreadyExistException("Username already exists", ErrorCodes.ENTITY_ALREADY_EXIST, new ArrayList<>(Collections.singleton("Username already exists")));

                }
            }
        }

        existingUsers = realmResource.users().search(null, userRequest.getEmail(), null, null, 0, 1);
        if (!existingUsers.isEmpty()) {
            for (UserRepresentation existingUser : existingUsers) {
                if (!existingUser.getId().equals(userRequest.getId())) {
                    throw new EntityAlreadyExistException("Email already exists", ErrorCodes.ENTITY_ALREADY_EXIST, new ArrayList<>(Collections.singleton("Email already exists")));
                }
            }
        }

        UserRepresentation user = realmResource.users().get(userRequest.getId()).toRepresentation();
        user.setUsername(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEnabled(userRequest.isActive());
        realmResource.users().get(userRequest.getId()).update(user);
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<?> createUser(UserDto user) throws EntityAlreadyExistException {
        final int resp = 201;
        UsersResource usersResource = KeycloakConfig.getInstance().realm(KeycloakConfig.realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUserName());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(user.isActive());
        kcUser.setEmailVerified(false);

        Response response = usersResource.create(kcUser);

        if (response.getStatus() == resp) {
            String userId = getIdFromLocationHeader(response.getHeaderString("Location"));
            UserRepresentation userRepresentation = usersResource.get(userId).toRepresentation();
            return new ResponseEntity<>(userRepresentation, HttpStatus.CREATED);
        }
        throw new EntityAlreadyExistException("this user is already exist", ErrorCodes.ENTITY_ALREADY_EXIST, new ArrayList<>(Collections.singleton("this user is already exist")));

    }


    private String getIdFromLocationHeader(String locationHeader) {
        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
