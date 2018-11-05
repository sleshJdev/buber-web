package com.slesh.gallery.web.controller;


import com.slesh.gallery.persistence.model.Ad;
import com.slesh.gallery.persistence.model.ApplicationUser;
import com.slesh.gallery.persistence.repository.AdRepository;
import com.slesh.gallery.persistence.repository.ApplicationUserRepository;
import org.springframework.data.domain.Example;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final AdRepository adRepository;
    private final ApplicationUserRepository userRepository;

    public UsersController(AdRepository adRepository,
                           ApplicationUserRepository userRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    @DeleteMapping("/{userId}/ads/{adId}")
    public Ad removeAd(@PathVariable String userId, @PathVariable String adId) {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                "User with id=" + userId + " not found"));
        Ad probe = new Ad();
        probe.setId(adId);
        probe.setOwner(user);
        Example<Ad> example = Example.of(probe);
        Ad ad = adRepository.findOne(example)
            .orElseThrow(() -> new RuntimeException(
                "Ad with id=" + adId + " of user " + userId + " not found"));
        adRepository.delete(ad);
        return ad;
    }

    @GetMapping("/{userId}/ads")
    public List<Ad> getUserAds(@PathVariable String userId) {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                "User with id " + userId + " not found"));
        Ad probe = new Ad();
        probe.setOwner(user);
        Example<Ad> example = Example.of(probe);
        return adRepository.findAll(example);
    }
}
