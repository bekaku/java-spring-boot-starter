package io.beka.model.data;

import lombok.Getter;

@Getter
public class UsersWithToken {
    private String email;
    private String username;
    private String bio;
    private String image;
    private String token;

    public UsersWithToken(UserData userData, String token) {
        this.email = userData.getEmail();
        this.username = userData.getUsername();
        this.bio = userData.getBio();
        this.image = userData.getImage();
        this.token = token;
    }
}
