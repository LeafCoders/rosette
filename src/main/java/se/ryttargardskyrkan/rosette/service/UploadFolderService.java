package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.UploadFolder;

@Service
public class UploadFolderService {

	@Autowired
	private SecurityService security;
	List<UploadFolder> folders = new ArrayList<UploadFolder>();

	public void addFolder(final UploadFolder folder) {
		if (!folderExist(folder.getName())) {
			folders.add(folder);
		}
	}

	public List<UploadFolder> getAllPermitted() {
		List<UploadFolder> permittedFolders = new ArrayList<UploadFolder>();
		for (final UploadFolder f : folders) {
			// Use same key as for uploads when testing for permitted folder name
			if (security.isPermitted("read:uploads:" + f.getName())) {
				permittedFolders.add(f);
			}
		}
		return permittedFolders;
	}

	public boolean folderExist(final String folderName) {
		return getFolder(folderName) != null;
	}

	public boolean isPermittedMimeType(final String folderName, final String mimeType) {
		UploadFolder folder = getFolder(folderName);
		if (folder != null) {
			for (String match : folder.getMimeTypes()) {
				if (mimeType.startsWith(match)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private final UploadFolder getFolder(final String folderName) {
		for (final UploadFolder folder : folders) {
			if (folder.getName().compareTo(folderName) == 0) {
				return folder;
			}
		}
		return null;
	}
}
