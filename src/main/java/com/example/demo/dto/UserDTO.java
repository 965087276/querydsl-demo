package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String userId;
    private String username;
    private String nickname;
    private String birthday;
}