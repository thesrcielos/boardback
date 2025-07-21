package com.board.auth.dto;

import lombok.Data;

@Data
public class GoogleUserInfo {
    private String sub;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String email;
    private Boolean emailVerified;
    private String locale;
}
