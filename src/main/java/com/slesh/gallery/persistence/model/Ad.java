package com.slesh.gallery.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Data
@Document
public class Ad {
    @Id
    private String id;
    private Float price;
    private String title;
    private String city;
    private Integer birthyear;
    private String avatar;
    private String name;
    private String ethnicity;
    private String availability;
    private String url;
    private String tagline;
    @Field("phone")
    private String tel;
    private String description;
    private Set<String> photos;
    private String createdOn;
    @DBRef
    @JsonIgnore
    private ApplicationUser owner;
}
