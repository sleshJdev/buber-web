package com.slesh.gallery.persistence.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class Ad {
    @Id
    private String id;
    private Float price;
    private String title;
    private String city;
    private Integer age;
    private String birthday;
    private String avatar;
    private String name;
    private String ethnicity;
    private String availability;
    private String url;
    private String tagline;
    @Field("phone")
    private String tel;
    private String description;
    private String[] photos;
    private String createdOn;
    @DBRef
    private ApplicationUser owner;
}
