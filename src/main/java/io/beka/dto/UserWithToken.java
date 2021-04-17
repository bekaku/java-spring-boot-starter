package io.beka.dto;

import lombok.Getter;

@Getter
public class UserWithToken {
    private String email;
    private String username;
    private String image;
    private String token;

    public UserWithToken(UserData userData, String token) {
        this.email = userData.getEmail();
        this.username = userData.getUsername();
        this.image = userData.getImage();
        this.token = token;
    }
}
