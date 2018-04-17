package com.slesh.gallery.persistence.repository;

import com.slesh.gallery.persistence.model.Ad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface AdRepository extends MongoRepository<Ad, String> {
    @RestResource(path = "recent", rel = "recent")
    Page<Ad> findByNameLikeAndLocationFormattedAddressLike(
        @Param("name") String name,
        @Param("location") String location,
        Pageable pageable);
}
