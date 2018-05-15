package com.slesh.gallery.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Ad {
    @Id
    private String id;
    private String name;
    private String tel;
    private Location location;
    private String description;
    private String createdOn;
    @JsonIgnore
    private Banner banner;

    @JsonIgnore
    public String getBannerKey() {
        return id + "_" + banner.getFilename();
    }
}
