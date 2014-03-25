package se.ryttargardskyrkan.rosette.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.mongodb.gridfs.GridFSDBFile;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.service.UploadService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AssetController extends AbstractController {
	@Autowired
	private UploadService uploadHelper;

	@RequestMapping(value = "assets/{folder}/{fileName:.+}", method = RequestMethod.GET)
	public void getAsset(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) {
		folder = folder.toLowerCase();
		checkPermission("read:assets:" + folder);

		GridFSDBFile file = uploadHelper.getFileByName(folder, fileName);
		if (file == null) {
			file = uploadHelper.getFileById(folder, fileName);
		}
		if (file != null) {
			try {
		        response.addHeader("Cache-Control", "public");
		        response.addHeader("Cache-Control", "max-age=86400"); // One day
		        response.addHeader("Content-disposition", "attachment; filename=\"" + file.getFilename() + "\"");
		        response.setContentType(file.getContentType());
		        response.setContentLength((int)file.getLength());

	            response.getOutputStream().write(IOUtils.toByteArray(file.getInputStream()));
	            response.getOutputStream().flush();
			} catch (IOException e) {
				throw new NotFoundException();
			}
        } else {
			throw new NotFoundException();
		}
	}
}
