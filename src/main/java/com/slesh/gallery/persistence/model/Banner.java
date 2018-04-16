package com.slesh.gallery.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Banner {
    private String filename;
    private String contentType;
    private long size;

    public Banner(String originalFilename, String contentType, long size) {
        filename = originalFilename;
        this.contentType = contentType;
        this.size = size;
    }
}
