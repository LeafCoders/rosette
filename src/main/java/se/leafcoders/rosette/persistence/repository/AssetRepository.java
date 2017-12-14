package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Asset;

@Repository
public interface AssetRepository extends ModelRepository<Asset> {
    
    public boolean existsByFolderId(Long folderId);
    public boolean existsByFolderIdAndFileName(Long folderId, String fileName);
}
