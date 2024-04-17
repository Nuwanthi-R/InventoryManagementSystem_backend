package com.Inventory.management.Inventory.Management.System.security.services;
import com.Inventory.management.Inventory.Management.System.model.Asset;
import com.Inventory.management.Inventory.Management.System.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getUserAssets(String username) {
               return assetRepository.findByAssignedTo(username);
    }
    public Map<String, Integer> getTotalAssetsData() {
        Map<String, Integer> totalAssetsData = new HashMap<>();
        List<Asset> assets = assetRepository.findAll();
        for (Asset asset : assets) {
            String assetType = asset.getAssetType();
            totalAssetsData.put(assetType, totalAssetsData.getOrDefault(assetType, 0) + 1);
        }

        return totalAssetsData;
    }

    public Map<String, Integer> getAssignedAssetsData() {
        Map<String, Integer> assignedAssetsData = new HashMap<>();
        List<Asset> assignedAssets = assetRepository.findByAssignedToIsNotNull();
        for (Asset asset : assignedAssets) {
            if (!asset.getAssignedTo().isEmpty()) {
                String assetType = asset.getAssetType();
                assignedAssetsData.put(assetType, assignedAssetsData.getOrDefault(assetType, 0) + 1);
            }
        }
        return assignedAssetsData;
    }

    public Map<String, Integer> getFreeAssetsData() {
        Map<String, Integer> freeAssetsData = new HashMap<>();
        List<Asset> allAssets = assetRepository.findAll();
        for (Asset asset : allAssets) {
            if (asset.getAssignedTo() == null || asset.getAssignedTo().isEmpty()) {
                String assetType = asset.getAssetType();
                freeAssetsData.put(assetType, freeAssetsData.getOrDefault(assetType, 0) + 1);
            }
        }
        return freeAssetsData;
    }

}

