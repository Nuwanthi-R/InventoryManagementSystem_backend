package com.Inventory.management.Inventory.Management.System.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "assets")
public class Asset {
    @Id
    private String assetId;
    private String assetName;
    private String assetType;
    private String assignedTo;
}