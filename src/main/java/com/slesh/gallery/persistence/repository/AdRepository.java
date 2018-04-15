package com.slesh.gallery.persistence.repository;

import com.slesh.gallery.persistence.model.Ad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AdRepository extends CrudRepository<Ad, String> {
}
