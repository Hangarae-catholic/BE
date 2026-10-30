package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByProviderAndProviderId(String provider, String providerId);
}