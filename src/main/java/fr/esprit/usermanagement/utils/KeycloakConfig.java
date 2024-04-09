package fr.esprit.usermanagement.utils;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeycloakConfig {
    public static String serverURL;
    public static String realm;
    public static String clientID;

    public static String clientSecret;

    private static String USERNAME;


    private static String PASSWORD;


    @Value("${keycloak.auth-server-url}")
    public void setServerURL(String value) {
        serverURL = value;
    }

    @Value("${keycloak.realm}")
    public void setRealm(String value) {
        realm = value;
    }

    @Value("${keycloak.resource}")
    public void setClientID(String value) {
        clientID = value;
    }
    @Value("${keycloak.credentials.secret}")
    public void setClientSecret(String value){
        clientSecret = value;
    }
    @Value("${admin.username}")
    public void setUSERNAME(String value){
        USERNAME = value;
    }
    @Value("${admin.password}")
    public void setPASSWORD(String value){
        PASSWORD = value;
    }

    private static Keycloak keycloak = null;

    public static Keycloak getInstance() {
        if (keycloak == null) {
            return KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverURL)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.PASSWORD).
                    username(USERNAME).
                    password(PASSWORD)
                    .build();
        }
        return keycloak;
    }

    public static Keycloak getInstance(String username, String password) {
        if (keycloak == null) {
            return KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverURL)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.PASSWORD).
                    username(username).
                    password(password)
                    .build();
        }
        return keycloak;
    }
}
