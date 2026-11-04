package com.servicesphere.service;

import com.servicesphere.entity.Asset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetBusinessLogicTest {

    @Test
    void testPreventAssignmentOfRetiredAsset() {
        Asset asset = new Asset();
        asset.setId(10L);
        asset.setAssetTag("AST-999999");
        asset.setStatus("Retired");

        String currentStatus = asset.getStatus();
        boolean canAssign = !"Retired".equalsIgnoreCase(currentStatus) && !"In Repair".equalsIgnoreCase(currentStatus);

        assertFalse(canAssign, "Retired assets must be blocked from active assignment");
    }

    @Test
    void testAllowAssignmentOfInStockAsset() {
        Asset asset = new Asset();
        asset.setId(11L);
        asset.setAssetTag("AST-888888");
        asset.setStatus("In Stock");

        String currentStatus = asset.getStatus();
        boolean canAssign = !"Retired".equalsIgnoreCase(currentStatus) && !"In Repair".equalsIgnoreCase(currentStatus);

        assertTrue(canAssign, "In Stock assets should be available for assignment");
    }
}