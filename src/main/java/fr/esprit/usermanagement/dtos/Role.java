package fr.esprit.usermanagement.dtos;

import lombok.Data;

import java.util.List;
@Data
public class Role {
    private String id;
    private String name;

    private List<Role> permissions;

}
