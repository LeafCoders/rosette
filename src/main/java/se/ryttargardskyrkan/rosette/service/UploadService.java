package se.ryttargardskyrkan.rosette.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Service
public class UploadService {
	public static final String METADATA_FOLDER = "folder";
	public static final String METADATA_WIDTH = "width";
	public static final String METADATA_HEIGHT = "height";

	@Autowired
	private GridFsTemplate gridFsTemplate;

	public GridFSFile storeFile(byte [] fileData, String fileName, String mimeType, DBObject metaData) {
		InputStream inputStream = new ByteArrayInputStream(fileData);
		try {
			return gridFsTemplate.store(inputStream, fileName, mimeType, metaData);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	public GridFSDBFile getFileById(String folder, String id) {
		if (ObjectId.isValid(id)) {
			Query query = new Query(Criteria.where("_id").is(new ObjectId(id)).and("metadata." + METADATA_FOLDER).is(folder));
			return gridFsTemplate.findOne(query);
		}
		return null;
	}

	public GridFSDBFile getFileByName(String folder, String fileName) {
		Query query = new Query(Criteria.where("filename").is(fileName).and("metadata." + METADATA_FOLDER).is(folder));
		return gridFsTemplate.findOne(query);
	}

	public List<GridFSDBFile> getFilesInFolder(String folder) {
		Query query = new Query(Criteria.where("metadata." + METADATA_FOLDER).is(folder));
		return gridFsTemplate.find(query);
	}

	public boolean deleteFileById(String folder, String id) {
		if (getFileById(folder, id) != null) {
			Query query = new Query(Criteria.where("_id").is(new ObjectId(id)).and("metadata." + METADATA_FOLDER).is(folder));
			gridFsTemplate.delete(query);
			return true;
		}
		return false;
	}

	public String getMetadataFolder(GridFSFile file) {
		return file.getMetaData().get(METADATA_FOLDER).toString();
	}

	public Long getMetadataWidth(GridFSFile file) {
		Object width = file.getMetaData().get(METADATA_WIDTH);
		if (width != null) {
			return Long.parseLong(width.toString());	
		}
		return null;
	}

	public Long getMetadataHeight(GridFSFile file) {
		Object height = file.getMetaData().get(METADATA_HEIGHT);
		if (height != null) {
			return Long.parseLong(height.toString());	
		}
		return null;
	}
}
