'use strict';

function ImportUploadsCtrl($scope, serverAPI) {

	var uploads = [];
	$scope.uploadToFolderId = '';

	$scope.selectedFilesChanged = function () {
		var newUploads = [];

		var selectedFiles = document.getElementById('filesToUpload').files;
		for (var index = 0; index < selectedFiles.length; ++index) {
			var file = selectedFiles[index];
		
			newUploads.push({
				fileName: file.name,
				file: file,
				imported: '-'
			});
		}

		uploads.length = 0;
		Array.prototype.push.apply(uploads, newUploads);
		$scope.$apply();
	};
	
	function importUploads() {
		uploadAll(0);
	}

	return {
		uploads: uploads,
		importUploads: importUploads
	};
	
	function uploadAll(fromIndex) {
		if (fromIndex >= uploads.length) {
			return;
		}
		var upload = uploads[fromIndex];
		upload.imported = 'Importing...';
        
    		serverAPI.uploadFile($scope.uploadToFolderId, upload.file)
    			.then(function (data) {
    				upload.imported = 'YES';
    				uploadAll(fromIndex + 1);
    			})
    			.catch(function(response) {
    				upload.imported = 'FAILED: ' + response.statusText;
    				if (response.status === 400) {
    					upload.imported += ' - ' + response.data[0].message;
    				}
    				uploadAll(fromIndex + 1);
    			});
	}
}

managerApp.controller('ImportUploadsCtrl', ImportUploadsCtrl);
