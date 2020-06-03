package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Asset;

@Repository
public interface AssetRepository extends ModelRepository<Asset> {

    public List<Asset> findByFolderId(Long assetFolderId, Sort sort);
    public Asset findOneByFileId(String fileId);
    
    public boolean existsByFolderId(Long folderId);
    public boolean existsByFileId(String fileId);
}
