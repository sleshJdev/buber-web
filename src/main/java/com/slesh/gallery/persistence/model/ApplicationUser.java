package com.slesh.gallery.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ApplicationUser {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private List<Role> roles;
}
