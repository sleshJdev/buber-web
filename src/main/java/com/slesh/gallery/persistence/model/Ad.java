package com.slesh.gallery.persistence.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @DBRef(lazy = true)
    @JsonIgnore
    private ApplicationUser owner;
    @JsonIgnore
    private Banner banner;

    @JsonGetter("ownerId")
    public String getOwnerId() {
        if (owner == null) {
            return null;
        }
        return owner.getId();
    }

    @JsonGetter("ownerName")
    public String getOwnerName() {
        if(owner == null) {
            return null;
        }
        return owner.getUsername();
    }

    @JsonIgnore
    public String getBannerKey() {
        if(banner == null) {
            return null;
        }
        return id + "_" + banner.getFilename();
    }
}
