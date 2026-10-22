package com.catholic.hangarae.domain.image.repository;

import com.catholic.hangarae.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
