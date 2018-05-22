package com.slesh.gallery.persistence.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Ad {
    @Id
    private String id;
    private String name;
    private String tel;
    private String birthday;
    private Location location;
    private String description;
    private String createdOn;
    @DBRef(lazy = true)
    @JsonIgnore
    private ApplicationUser owner;
    @JsonIgnore
    private Banner banner;

    @JsonGetter("ownerId")
    public String getOwnerId() {
        return owner.getId();
    }

    @JsonGetter("ownerName")
    public String getOwnerName() {
        return owner.getUsername();
    }

    @JsonIgnore
    public String getBannerKey() {
        return id + "_" + banner.getFilename();
    }
}
