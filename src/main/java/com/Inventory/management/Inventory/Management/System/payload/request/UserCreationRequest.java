package com.Inventory.management.Inventory.Management.System.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserCreationRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private List<String> roles;


}
