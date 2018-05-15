package com.slesh.gallery.web.controller;


import com.slesh.gallery.persistence.model.Ad;
import com.slesh.gallery.persistence.model.Banner;
import com.slesh.gallery.persistence.repository.AdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    private final AdRepository adRepository;
    private final File bannersDir;

    public AdController(AdRepository adRepository) {
        this.adRepository = adRepository;

        String userHome = System.getProperty("user.home");
        bannersDir = Paths.get(userHome, ".gallery/banners").toFile();
        if (!bannersDir.exists()) {
            if (!bannersDir.mkdirs()) {
                throw new RuntimeException(String.format("Could create %s directory", bannersDir));
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ad> ad(@PathVariable String id) {
        return adRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<Ad> ads(
        @RequestParam(required = false, defaultValue = "") String name,
        @RequestParam(required = false, defaultValue = "") String address,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "25") int size,
        @RequestParam(required = false, defaultValue = "desc") String direction,
        @RequestParam(required = false, defaultValue = "createdOn") String property
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), property);
        PageRequest pageable = PageRequest.of(page, size, sort);
        return adRepository.findByNameContainingAndLocationAddressContaining(name, address, pageable);
    }

    @GetMapping(path = "/{id}/banner")
    public void getBanner(@PathVariable String id, HttpServletResponse response) {
        Ad ad = adRepository.findById(id).orElseThrow(() ->
            new RuntimeException(String.format("Ad[id=%s] not found", id)));

        File file = new File(bannersDir, ad.getBannerKey());
        try {
            String path = file.toString();
            String subtype = "*";
            int dot = path.lastIndexOf('.');
            if (dot != -1) {
                subtype = path.substring(dot + 1).toLowerCase();
            }
            response.setStatus(HttpStatus.OK.value());
            response.setContentLengthLong(ad.getBanner().getSize());
            response.setContentType(MediaType.valueOf("image/" + subtype).toString());
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file " + file);
        }
    }

    @PostMapping
    public Ad save(@RequestPart Ad ad, @RequestPart MultipartFile file) {
        ad.setCreatedOn(DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        ad.setBanner(new Banner(file.getOriginalFilename(), file.getContentType(), file.getSize()));
        Ad instance = adRepository.save(ad);
        try {
            file.transferTo(new File(bannersDir, instance.getBannerKey()));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't process file", e);
        }
        return instance;
    }

}
