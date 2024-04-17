package com.Inventory.management.Inventory.Management.System.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;




@Getter
public class SignupRequest {
    @Setter
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Setter
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String telephoneNumber;

    private Set<String> roles;

    @Setter
    @NotBlank
    @Size(min = 5, max = 40)
    private String password;

    public void setRole(Set<String> roles) {
        this.roles = roles;
    }
}
