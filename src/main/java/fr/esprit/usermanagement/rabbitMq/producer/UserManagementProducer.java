package fr.esprit.usermanagement.rabbitMq.producer;

import fr.esprit.usermanagement.dtos.UserDto;
import fr.esprit.usermanagement.rabbitMq.messageDtos.UserDtoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(UserDto userDto){
        UserDtoMessage userDtoMessage =
                UserDtoMessage.builder()
                        .userId(userDto.getId())
                        .firstName(userDto.getFirstName())
                        .userName(userDto.getUserName())
                        .lastName(userDto.getLastName())
                        .email(userDto.getEmail())
                        .build();
        rabbitTemplate.convertAndSend("userManagementService", userDtoMessage);

    }
}
