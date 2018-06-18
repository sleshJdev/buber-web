package com.slesh.gallery.persistence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private String address;
    private GeoJsonPoint point;

    @JsonCreator
    public Location(@JsonProperty(value = "address", required = true) String address,
                    @JsonProperty(value = "point", required = true) Map<String, Double> coordinates) {
        this(address, new GeoJsonPoint(
            coordinates.get("lng"),
            coordinates.get("lat")));
    }


    @JsonGetter(value = "point")
    public Map<String, Double> getLngLat() {
        if (point == null) {
            return Collections.emptyMap();
        }
        Map<String, Double> coordinates = new HashMap<>(2);
        coordinates.put("lng", point.getX());
        coordinates.put("lat", point.getY());
        return coordinates;
    }
}
