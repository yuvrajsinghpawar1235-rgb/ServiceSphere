package com.servicesphere.controller;

import com.servicesphere.entity.Asset;
import com.servicesphere.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetRepository assetRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "Laptop", "Desktop", "Monitor", "Phone", "Peripheral", "Network Equipment"
    );

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "In Stock", "Assigned", "In Repair", "Retired"
    );

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        if (status != null) {
            return ResponseEntity.ok(assetRepository.findByStatus(status));
        }
        if (type != null) {
            return ResponseEntity.ok(assetRepository.findByAssetType(type));
        }
        return ResponseEntity.ok(assetRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createAsset(@RequestBody Asset asset, @RequestParam(required = false) Long roleId) {
        if (roleId == null || (roleId != 3 && roleId != 5)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Managers and Administrators can create IT assets."));
        }

        if (asset.getAssetTag() == null || asset.getAssetTag().isEmpty()) {
            asset.setAssetTag("AST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }

        if (asset.getAssetType() == null || !ALLOWED_TYPES.contains(asset.getAssetType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid asset type. Allowed: Laptop, Desktop, Monitor, Phone, Peripheral, Network Equipment"));
        }

        if (asset.getStatus() == null || !ALLOWED_STATUSES.contains(asset.getStatus())) {
            asset.setStatus("In Stock");
        }

        Asset savedAsset = assetRepository.save(asset);
        return ResponseEntity.ok(savedAsset);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignAsset(
            @PathVariable Long id,
            @RequestParam Long employeeId,
            @RequestParam String employeeName,
            @RequestParam String condition,
            @RequestParam Long roleId) {

        if (roleId == null || (roleId != 2 && roleId != 3 && roleId != 5)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Agents and Managers can assign assets."));
        }

        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));

        String currentStatus = asset.getStatus() != null ? asset.getStatus() : "";
        if ("Retired".equalsIgnoreCase(currentStatus) || "In Repair".equalsIgnoreCase(currentStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cannot assign asset. Asset status is currently: " + currentStatus));
        }

        asset.setStatus("Assigned");
        asset.setAssignedToUserId(employeeId);
        asset.setAssignedToUser(employeeName);
        asset.setAssignedDate(LocalDate.now());
        asset.setAssignmentCondition(condition);

        Asset updated = assetRepository.save(asset);
        return ResponseEntity.ok(Map.of("message", "Asset successfully assigned.", "asset", updated));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<?> returnAsset(
            @PathVariable Long id,
            @RequestParam String condition,
            @RequestParam String newStatus,
            @RequestParam Long roleId) {

        if (roleId == null || (roleId != 2 && roleId != 3 && roleId != 5)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Agents and Managers can process asset returns."));
        }

        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setStatus(newStatus);
        asset.setAssignmentCondition(condition);
        asset.setAssignedToUserId(null);
        asset.setAssignedToUser(null);
        asset.setAssignedDate(null);

        Asset updated = assetRepository.save(asset);
        return ResponseEntity.ok(Map.of("message", "Asset successfully returned and set to status: " + newStatus, "asset", updated));
    }

    @GetMapping("/alerts/warranty-expiry")
    public ResponseEntity<List<Asset>> getWarrantyExpiryAlerts() {
        LocalDate expiryLimit = LocalDate.now().plusDays(30);
        return ResponseEntity.ok(assetRepository.findAssetsWithExpiringWarranty(expiryLimit));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Asset> updateAssetStatus(@PathVariable Long id, @RequestParam String status) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setStatus(status);
        return ResponseEntity.ok(assetRepository.save(asset));
    }
}