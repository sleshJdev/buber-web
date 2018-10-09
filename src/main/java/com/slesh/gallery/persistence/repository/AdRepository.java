package com.slesh.gallery.persistence.repository;

import com.slesh.gallery.persistence.model.Ad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends MongoRepository<Ad, String> {
    @Query(value = "{}", fields = "{city: 1}")
    List<City> getCities();

    interface City {
        @Value("#{target.city}")
        String value();
    }
}
