package com.catholic.hangarae.domain.crawl.repository;

import com.catholic.hangarae.domain.crawl.entity.NoticeFailure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeFailureRepository extends JpaRepository<NoticeFailure, Long> {

    Optional<NoticeFailure> findByUrl(String url);

    void deleteByUrl(String url);
}
