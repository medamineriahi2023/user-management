package fr.esprit.usermanagement.services;

import fr.esprit.usermanagement.dtos.Role;
import fr.esprit.usermanagement.dtos.UserDto;
import fr.esprit.usermanagement.exceptions.EntityAlreadyExistException;
import fr.esprit.usermanagement.exceptions.EntityNotFoundException;
import fr.esprit.usermanagement.exceptions.ErrorOccurredException;
import fr.esprit.usermanagement.exceptions.InvalidEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IUserService {

    UserDto getUser(String userId);

    String getToken();

    ResponseEntity<?> resetUserPassword(String userId, String newPassword);

    ResponseEntity<List<UserDto>> getAllUsers();
    ResponseEntity<List<Role>> getPermissions();

    ResponseEntity<?> deleteRole(String roleName) throws InvalidEntityException, ErrorOccurredException;
    ResponseEntity<?> deleteUser(String userId) throws InvalidEntityException, ErrorOccurredException;

    ResponseEntity<?> updateRole(Role role) throws EntityNotFoundException;


    ResponseEntity<?> createRole(String roleName) throws EntityNotFoundException, EntityAlreadyExistException;

    ResponseEntity<?> assignCompositeRolesForRole(String roleId, List<String> rolesIds) throws EntityNotFoundException;

    ResponseEntity<List<Role>> getAllRoles();


    ResponseEntity<?> assignRolesToUser(String userId, List<String> roleIds) throws EntityNotFoundException;


    ResponseEntity<?> editUser(UserDto userRequest) throws EntityAlreadyExistException;


    ResponseEntity<?> createUser(UserDto user) throws EntityAlreadyExistException;

}
