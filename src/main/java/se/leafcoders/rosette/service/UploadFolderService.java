package se.leafcoders.rosette.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.model.upload.UploadFolder;

@Service
public class UploadFolderService extends MongoTemplateCRUD<UploadFolder> {

	@Autowired
	private SecurityService security;
	@Autowired
	private UploadService uploadService;

	List<UploadFolder> staticFolders = new ArrayList<UploadFolder>();

	public UploadFolderService() {
		super("uploadFolders", UploadFolder.class);
	}

	@Override
	public UploadFolder create(UploadFolder uploadFolder, HttpServletResponse response) {
		validateNotStatic(uploadFolder.getId());
		return super.create(uploadFolder, response);
	}

	@Override
	public UploadFolder read(String folderId) {
		UploadFolder staticFolder = getStaticFolder(folderId);
		if (staticFolder != null) {
			return staticFolder;
		}
		return super.read(folderId);
	}

	@Override
	public List<UploadFolder> readMany(final Query query) {
		List<UploadFolder> many = super.readMany(query);
		many.addAll(filterPermittedItems(staticFolders));
		return many;
	}

	@Override
	public void update(String folderId, UploadFolder updateData, HttpServletResponse response) {
		validateNotStatic(folderId);
		super.update(folderId, updateData, response);
	}
	
	@Override
	public void delete(String folderId, HttpServletResponse response) {
		validateNotStatic(folderId);
		validateNoFilesInFolder(folderId);
		super.delete(folderId, response);
	}

	@Override
	public void insertDependencies(UploadFolder data) {
	}

	public void addStaticFolder(final UploadFolder folder) {
		if (getStaticFolder(folder.getId()) == null) {
			staticFolders.add(folder);
		}
	}

	public boolean isPermittedMimeType(final String folderId, final String mimeType) {
		UploadFolder folder = getStaticFolder(folderId);
		if (folder != null) {
			for (String match : folder.getMimeTypes()) {
				if (mimeType.startsWith(match)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPublic(final String folderId) {
		UploadFolder folder = getStaticFolder(folderId);
		return (folder != null) && folder.getIsPublic();
	}

	private void validateNotStatic(final String folderId) {
		if (getStaticFolder(folderId) != null) {
			throw new ForbiddenException("Can't modify static folder!");
		}
	}
	
	private void validateNoFilesInFolder(String folderId) {
		if (!uploadService.getFileIdsInFolder(folderId).isEmpty()) {
			throw new ForbiddenException("error.referencedBy", "upload");
		}
	}

	private final UploadFolder getStaticFolder(final String folderId) {
		for (final UploadFolder folder : staticFolders) {
			if (folder.getId().compareTo(folderId) == 0) {
				return folder;
			}
		}
		return null;
	}
}
