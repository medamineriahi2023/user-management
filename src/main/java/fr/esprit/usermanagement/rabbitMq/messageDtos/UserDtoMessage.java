package fr.esprit.usermanagement.rabbitMq.messageDtos;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class UserDtoMessage {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
}