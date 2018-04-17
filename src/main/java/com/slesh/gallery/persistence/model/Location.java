package com.slesh.gallery.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Location {
    String formattedAddress;
    String placeId;
}
