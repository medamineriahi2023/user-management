package fr.esprit.usermanagement.controllers;

import fr.esprit.usermanagement.docx.DocService;
import fr.esprit.usermanagement.dtos.Role;
import fr.esprit.usermanagement.dtos.UserDto;
import fr.esprit.usermanagement.exceptions.EntityAlreadyExistException;
import fr.esprit.usermanagement.exceptions.EntityNotFoundException;
import fr.esprit.usermanagement.exceptions.ErrorOccurredException;
import fr.esprit.usermanagement.exceptions.InvalidEntityException;
import fr.esprit.usermanagement.services.IUserService;
import fr.esprit.usermanagement.utils.KeycloakConfig;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/realms/analytix") // Changed the base path
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final IUserService userService;

    @PostMapping("extract/{userId}")
    public void getUserDoc(@PathVariable String userId) throws Exception {
        DocService.createXml();
        DocService.makeWord();
        DocService.makePdfByXcode();
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId){
       return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping("/users") // Changed the path
    public ResponseEntity<?> getAllUserSql() throws SQLException {
        return userService.getAllUsers();
    }

    @PostMapping("/users/{userId}/reset-password/{password}") // Changed the path
    public ResponseEntity<?> resetUserPassword(@PathVariable String userId, @PathVariable String password) {
        return userService.resetUserPassword(userId, password);
    }

    @PostMapping("/token") // No change
    public String getToken() {
        return userService.getToken();
    }

    @PostMapping("/roles/{roleName}") // No change
    public ResponseEntity<?> createRole(@PathVariable(value = "roleName") String roleName) throws EntityAlreadyExistException, EntityNotFoundException {
        return userService.createRole(roleName);
    }

    @GetMapping("/roles/permissions") // No change
    public ResponseEntity<?> getAllPermissions() {
        return userService.getPermissions();
    }

    @PutMapping("/roles") // No change
    public ResponseEntity<?> updateRole(@RequestBody Role role) throws EntityNotFoundException {
        return userService.updateRole(role);
    }

    @DeleteMapping("/roles/{roleName}") // No change
    public ResponseEntity<?> deleteRole(@PathVariable(value = "roleName") String roleName) throws InvalidEntityException, ErrorOccurredException {
        return userService.deleteRole(roleName);
    }

    @DeleteMapping("/users/{userId}") // Changed the path
    public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") String userId) throws InvalidEntityException, ErrorOccurredException {
        return userService.deleteUser(userId);
    }

    @PostMapping("/roles/assign-permissions") // Changed the path
    public ResponseEntity<?> assignCompositeRolesForRole(@RequestBody Map<String, Object> requestBody) throws EntityNotFoundException {
        String roleId = (String) requestBody.get("roleId");
        List<String> rolesIds = (List<String>) requestBody.get("rolesIds");
        return userService.assignCompositeRolesForRole(roleId, rolesIds);
    }

    @GetMapping("/roles") // No change
    public ResponseEntity<?> getAllRoles() {
        return userService.getAllRoles();
    }

    @PostMapping("/users/assign-roles") // Changed the path
    public ResponseEntity<?> assignRolesToUser(@RequestBody Map<String, Object> requestBody) throws EntityNotFoundException {
        String userId = (String) requestBody.get("userId");
        List<String> roleIds = (List<String>) requestBody.get("roleIds");
        return userService.assignRolesToUser(userId, roleIds);
    }

    @PostMapping("/users") // Changed the path
    public ResponseEntity<?> createUser(@RequestBody UserDto user) throws EntityAlreadyExistException {
        return userService.createUser(user);
    }

    @PutMapping("/users") // Changed the path
    public ResponseEntity<?> updateUser(@RequestBody UserDto user) throws EntityAlreadyExistException {
        return userService.editUser(user);
    }
}
