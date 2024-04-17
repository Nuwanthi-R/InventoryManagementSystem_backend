package com.Inventory.management.Inventory.Management.System.controller;
import com.Inventory.management.Inventory.Management.System.model.Asset;
import com.Inventory.management.Inventory.Management.System.security.services.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    @GetMapping("/my-assets")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<Asset>> getUserAssets(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        logger.info("Getting assets for user: {}", username);
        List<Asset> userAssets = dashboardService.getUserAssets(username);
        logger.debug("Retrieved {} assets for user: {}", userAssets.size(), username);
        return ResponseEntity.ok(userAssets);
    }

    @GetMapping("/total-assets")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Integer>> getTotalAssetsData() {
        logger.info("Getting total assets data");
        Map<String, Integer> totalAssetsData = dashboardService.getTotalAssetsData();
        logger.debug("Retrieved total assets data: {}", totalAssetsData);
        return ResponseEntity.ok(totalAssetsData);
    }

    @GetMapping("/assigned-assets")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Integer>> getAssignedAssetsData() {
        logger.info("Getting assigned assets data");
        Map<String, Integer> assignedAssetsData = dashboardService.getAssignedAssetsData();
        logger.debug("Retrieved assigned assets data: {}", assignedAssetsData);
        return ResponseEntity.ok(assignedAssetsData);
    }

    @GetMapping("/free-assets")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Integer>> getFreeAssetsData() {
        logger.info("Getting free assets data");
        Map<String, Integer> freeAssetsData = dashboardService.getFreeAssetsData();
        logger.debug("Retrieved free assets data: {}", freeAssetsData);
        return ResponseEntity.ok(freeAssetsData);
    }
}
