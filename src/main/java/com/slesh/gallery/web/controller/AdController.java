package com.slesh.gallery.web.controller;


import com.slesh.gallery.persistence.model.Ad;
import com.slesh.gallery.persistence.repository.AdRepository;
import com.slesh.gallery.persistence.repository.ApplicationUserRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    private static final String API_ADS_PICTURE_URL = "/api/ads/picture/";
    private final File bannersDir;
    private final AdRepository adRepository;
    private final ApplicationUserRepository userRepository;

    public AdController(AdRepository adRepository,
                        ApplicationUserRepository userRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;

        String userHome = System.getProperty("user.home");
        bannersDir = Paths.get(userHome, ".buber/ads").toFile();
        if (!bannersDir.exists()) {
            if (!bannersDir.mkdirs()) {
                throw new RuntimeException(String.format("Could create %s directory", bannersDir));
            }
        }
    }

    @GetMapping("/cities")
    public Set<String> getCities() {
        return adRepository.getCities().stream()
            .map(AdRepository.City::value)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
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
        @RequestParam(required = false, defaultValue = "") String city,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "25") int size,
        @RequestParam(required = false, defaultValue = "desc") String direction,
        @RequestParam(required = false, defaultValue = "createdOn") String sortBy,
        @RequestParam(required = false, defaultValue = "") String ownerId
    ) {
        Ad probe = new Ad();
        if (StringUtils.hasText(name)) {
            probe.setName(name);
        }
        if (StringUtils.hasText(city)) {
            probe.setCity(city);
        }
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
            .withMatcher("name", contains().ignoreCase())
            .withMatcher("city", exact());
        ExampleMatcher finalMatcher = Optional.of(ownerId)
            .filter(StringUtils::hasText)
            .flatMap(userRepository::findById)
            .map(owner -> {
                probe.setOwner(owner);
                return matcher.withMatcher("owner", exact());
            }).orElse(matcher);

        Example<Ad> example = Example.of(probe, finalMatcher);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        return adRepository.findAll(example, pageable);
    }

    @GetMapping(path = "/picture/{key}")
    public void getPicture(@PathVariable String key, HttpServletResponse response) {
        File file = new File(bannersDir, key);
        String subtype = findFileExtension(file.toString()).orElse("*");
        response.setStatus(HttpStatus.OK.value());
        response.setContentLengthLong(file.length());
        response.setContentType(MediaType.valueOf("image/" + subtype).toString());

        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file " + file);
        }
    }

    @PostMapping
    public Ad save(@RequestPart(name = "ad") Ad update,
                   @RequestPart(name = "avatar", required = false) MultipartFile avatar,
                   @RequestPart(name = "newphoto", required = false) List<MultipartFile> newPhotos,
                   Principal principal) {
        Ad ad = update.getId() == null ? update :
            adRepository.findById(update.getId()).orElseThrow(() ->
                new RuntimeException("Ad with id " + update.getId() + " not found "));

        if (ad.getId() == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ad.setCreatedOn(formatter.format(LocalDateTime.now(Clock.systemUTC())));
            ad.setOwner(userRepository.findByUsername(principal.getName()));
        }

        Map<String, MultipartFile> keyPhotoMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(newPhotos)) {
            keyPhotoMap = newPhotos.stream().collect(
                Collectors.toMap(this::toKey, Function.identity()));
        }
        String avatarKey = ad.getAvatar();
        Map<String, MultipartFile> attachments = new HashMap<>(keyPhotoMap);
        if (avatar != null) {
            avatarKey = toKey(avatar);
            attachments.put(avatarKey, avatar);
        }
        atomicSave(attachments).ifPresent(throwable -> {
            throw new RuntimeException("Couldn't save ad", throwable);
        });

        if (ad.getId() == null || !avatarKey.equals(ad.getAvatar())) {
            if (StringUtils.hasText(ad.getAvatar())) {
                deletePhotoQuiet(ad.getAvatar());
            }
            ad.setAvatar(API_ADS_PICTURE_URL + avatarKey);
        }

        Set<String> photoUrls = keyPhotoMap.keySet().stream()
            .map(photoKey -> API_ADS_PICTURE_URL + photoKey)
            .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(update.getPhotos())) {
            photoUrls.addAll(update.getPhotos());
        }
        if (!CollectionUtils.isEmpty(ad.getPhotos())) {
            for (String photo : ad.getPhotos()) {
                if (!photoUrls.contains(photo)) {
                    deletePhotoQuiet(photo);
                }
            }
        }
        ad.setPhotos(photoUrls);

        ad.setPrice(update.getPrice());
        ad.setTitle(update.getTitle());
        ad.setCity(update.getCity());
        ad.setBirthyear(update.getBirthyear());
        ad.setName(update.getName());
        ad.setEthnicity(update.getEthnicity());
        ad.setAvailability(update.getAvailability());
        ad.setUrl(update.getUrl());
        ad.setTagline(update.getTagline());
        ad.setTel(update.getTel());
        ad.setDescription(update.getDescription());

        return adRepository.save(ad);
    }

    private void deletePhotoQuiet(String url) {
        deleteQuiet(StringUtils.delete(url, API_ADS_PICTURE_URL));
    }

    private void deleteQuiet(String key) {
        try {
            FileUtils.forceDelete(new File(bannersDir, key));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toKey(MultipartFile file) {
        String hash = UUID.randomUUID().toString();
        return hash + "." + findFileExtension(file.getOriginalFilename())
            .orElseThrow(() -> new RuntimeException("Couldn't resolve file extension"));
    }

    private Optional<Throwable> atomicSave(Map<String, MultipartFile> keyFileMap) {
        for (Map.Entry<String, MultipartFile> entry : keyFileMap.entrySet()) {
            try {
                String pictureKey = entry.getKey();
                MultipartFile file = entry.getValue();
                file.transferTo(new File(bannersDir, pictureKey));
            } catch (IOException saveFileException) {
                saveFileException.printStackTrace();
                for (String storedFile : keyFileMap.keySet()) {
                    deleteQuiet(storedFile);
                }
                return Optional.of(saveFileException);
            }
        }
        return Optional.empty();
    }

    private Optional<String> findFileExtension(String file) {
        int dot = file.lastIndexOf('.');
        return dot >= 0
            ? Optional.of(file.substring(dot + 1))
            : Optional.empty();
    }
}
