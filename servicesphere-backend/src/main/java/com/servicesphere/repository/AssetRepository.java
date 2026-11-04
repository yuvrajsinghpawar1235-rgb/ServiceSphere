package com.servicesphere.repository;

import com.servicesphere.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetTag(String assetTag);

    List<Asset> findByAssetType(String assetType);

    List<Asset> findByStatus(String status);

    List<Asset> findByAssignedToUserId(Long assignedToUserId);

    // Warranty expiry warning: Assets whose warranty ends on or before the given date and are not retired
    @Query("SELECT a FROM Asset a WHERE a.warrantyEndDate <= :expiryLimit AND a.status != 'Retired'")
    List<Asset> findAssetsWithExpiringWarranty(@Param("expiryLimit") LocalDate expiryLimit);
}