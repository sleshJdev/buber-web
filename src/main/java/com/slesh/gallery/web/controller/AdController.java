package com.slesh.gallery.web.controller;


import com.slesh.gallery.persistence.model.Ad;
import com.slesh.gallery.persistence.model.Banner;
import com.slesh.gallery.persistence.repository.AdRepository;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RepositoryRestController
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

    @GetMapping(path = "/ads/banner/{id}")
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

    @ResponseBody
    @PostMapping(value = "/ads",
        consumes = "multipart/form-data",
        produces = "application/hal+json")
    public ResponseEntity<ResourceSupport> save(@RequestPart Ad ad,
                                                @RequestPart MultipartFile file,
                                                PersistentEntityResourceAssembler assembler) {
        ad.setCreatedOn(DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        ad.setBanner(new Banner(file.getOriginalFilename(), file.getContentType(), file.getSize()));
        Ad instance = adRepository.save(ad);
        try {
            file.transferTo(new File(bannersDir, instance.getBannerKey()));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't process file", e);
        }
        return ResponseEntity.ok(assembler.toFullResource(instance));
    }

}
