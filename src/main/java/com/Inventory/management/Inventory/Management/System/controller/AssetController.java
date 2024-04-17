package com.Inventory.management.Inventory.Management.System.controller;
import com.Inventory.management.Inventory.Management.System.model.Asset;
import com.Inventory.management.Inventory.Management.System.payload.request.AssignRequest;
import com.Inventory.management.Inventory.Management.System.security.services.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssignRequest assignRequest;

    @Autowired
    AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAssets")
    public ResponseEntity<List<Asset>> getAllAssets() {
        logger.info("Fetching all assets");
        List<Asset> assets = assetService.getAllAssets();
        logger.debug("Retrieved {} assets", assets.size());
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/createAsset")
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        logger.info("Creating asset: {}", asset);
        Asset createdAsset = assetService.createAsset(asset);
        logger.info("Asset created successfully: {}", createdAsset);
        return new ResponseEntity<>(createdAsset, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/getAsset/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String assetId) {
        logger.info("Fetching asset with ID: {}", assetId);
        Asset asset = assetService.getAssetById(assetId);
        logger.debug("Retrieved asset: {}", asset);
        return new ResponseEntity<>(asset, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/my-assets")
    public ResponseEntity<List<Asset>> getAssetsAssignedTo(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Fetching assets assigned to user: {}", username);
        List<Asset> assets = assetService.getAssetsAssignedToUser(username);

        if (assets.isEmpty()) {
            logger.info("No assets assigned to user: {}", username);
            return ResponseEntity.noContent().build();
        }
        logger.debug("Retrieved {} assets assigned to user: {}", assets.size(), username);
        return ResponseEntity.ok(assets);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/updateAsset/{assetId}")
    public ResponseEntity<Asset> updateAsset(@PathVariable String assetId, @RequestBody Asset asset) {
        logger.info("Updating asset with ID {}: {}", assetId, asset);
        Asset updatedAsset = assetService.updateAsset(assetId, asset);
        logger.info("Asset updated successfully: {}", updatedAsset);
        return new ResponseEntity<>(updatedAsset, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAsset/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String assetId) {
        logger.info("Deleting asset with ID: {}", assetId);
        assetService.deleteAsset(assetId);
        logger.info("Asset deleted successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> assignAssetToUser(@RequestBody AssignRequest assignRequest) {
        try {
            assetService.assignAssetToUser(assignRequest);
            logger.info("Asset assigned successfully");
            return ResponseEntity.ok("Asset assigned successfully");
        } catch (Exception e) {
            logger.error("Failed to assign asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to assign asset: " + e.getMessage());
        }
    }

}