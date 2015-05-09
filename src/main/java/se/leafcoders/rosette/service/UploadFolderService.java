package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class UploadFolderService extends MongoTemplateCRUD<UploadFolder> {

	@Autowired
	private SecurityService security;
	@Autowired
	private UploadService uploadService;

	public UploadFolderService() {
		super(UploadFolder.class, PermissionType.UPLOAD_FOLDERS);
	}

	@Override
	public boolean readManyItemFilter(UploadFolder uploadFolder) {
		return security.isPermitted(permissionType, PermissionAction.READ, uploadFolder.getId()) ||
				security.isPermitted(PermissionType.UPLOADS, PermissionAction.READ, uploadFolder.getId());
	}

	@Override
	public void delete(String folderId, HttpServletResponse response) {
		validateNoFilesInFolder(folderId);
		super.delete(folderId, response);
	}

	@Override
	public void insertDependencies(UploadFolder data) {
	}

	public boolean folderExist(final String folderId) {
		return readWithoutPermission(folderId) != null;
	}
	
	public boolean isPermittedMimeType(final String folderId, final String mimeType) {
		UploadFolder folder = readWithoutPermission(folderId);
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
		UploadFolder folder = readWithoutPermission(folderId);
		return (folder != null) && folder.getIsPublic();
	}

	private void validateNoFilesInFolder(String folderId) {
		if (!uploadService.getFileIdsInFolder(folderId).isEmpty()) {
			throw new ForbiddenException("error.referencedBy", "upload");
		}
	}

}
