package com.slesh.gallery.persistence.repository;

import com.slesh.gallery.persistence.model.Ad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends MongoRepository<Ad, String> {
}
