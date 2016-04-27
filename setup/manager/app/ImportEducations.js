'use strict';

function ImportEducationsCtrl($scope, serverAPI) {

	var educations = [];
	var educationsFileContent = undefined;
	$scope.recordingsFolderId = 'predikningar';

	$scope.selectedEducationsFileChanged = function () {
		var selectedFiles = document.getElementById('educationsFile').files;
		if (selectedFiles && selectedFiles.length === 1) {
			var selectedFile = selectedFiles[0];

			var fr = new window.FileReader; 
			fr.onload = function (event) {
				educationsFileContent = event.target.result;
				parseEducationsFromFile()
			};
			fr.readAsText(selectedFile);
		}
	};

	function importEducations() {
		serverAPI.readUploads($scope.recordingsFolderId).then(function (uploads) {
			serverAPI.readEducations().then(function (educationsAtServer) {
			
				function findUpload(fileName) {
					for (var index = 0; index < uploads.length; ++index) {
						if (uploads[index].fileName === fileName) {
							return uploads[index];
						}
					}
					return undefined;
				}

				function findEducation(time, title) {
					for (var index = 0; index < educationsAtServer.length; ++index) {
						if (educationsAtServer[index].time.indexOf(time) >= 0 && educationsAtServer[index].title === title) {
							return educationsAtServer[index];
						}
					}
					return undefined;
				}
				
				function importAll(fromIndex) {
					if (fromIndex >= educations.length) {
						return;
					}
					var education = educations[fromIndex];
					education.imported = 'Importing...';

					var recording = findUpload(education.recording);
					if (education.recording && !recording) {
						education.imported = 'FAILED - Could not find specified recording at Rosette server for filename: ' + education.recording;
						importAll(fromIndex + 1);
					} else if (findEducation(education.time, education.title)) {
						education.imported = 'FAILED - Already exist at rosette server';
						importAll(fromIndex + 1);
					} else {
						serverAPI.createEducation(
								education.educationTypeId,
								education.educationThemeId,
								education.time,
								education.authorName,
								education.authorUserId,
								education.title,
								recording
						).then(function () {
							education.imported = 'YES';
				        	importAll(fromIndex + 1);
						}).catch(function (response) {
							education.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
				        	importAll(fromIndex + 1);
						});
					}
				};
				
				importAll(0);
			}).catch(function (response) {
				hasError('Could not read educations from Rosette server', { message: response.statusText });
			});
		}).catch(function (response) {
			hasError('Could not read recordings from Rosette server', { message: response.statusText });
		});
	}

	return {
		educations: educations,
		importEducations: importEducations
	};

	
	function parseEducationsFromFile() {
		var newEducations = [];

		var lines = educationsFileContent.match(/[^\r\n]+/g);
		for (var index = 0; index < lines.length; ++index) {
			var line = lines[index];
			var values = line.split("|");
		  
			var educationTypeId = values[0]
			var educationThemeId = values[1]
			var time = values[2]
			var authorName = values[3]
			var authorUserId = values[4]
			var title = values[5]
			var recording = values[6]
          
			if (hasError("Error was detected in line: \n\n" + line, [
                 { message: "Missing educationTypeId in line: " + line, test: educationTypeId.length > 0 },
                 { message: "Missing educationThemeId in line: " + line, test: educationThemeId.length > 0 },
                 { message: "Missing time in line: " + line, test: time.length > 0 },
                 { message: "One of authorName and authorUserId must be specified in line: " + line, test: !!authorName !== !!authorUserId },
                 { message: "Missing title in line: " + line, test: title.length > 0 }
            ])) {
				return;
			}

			newEducations.push({
				educationTypeId: educationTypeId,
				educationThemeId: educationThemeId,
				time: time,
				authorName: authorName,
				authorUserId: authorUserId,
				title: title,
				recording: recording,
				imported: '-'
			});
		}

		educations.length = 0;
		Array.prototype.push.apply(educations, newEducations);
		$scope.$apply();
	}

	function hasError(errorTitle, tests) {
		var messages = [];
		for (var index = 0; index < tests.length; ++index) {
			if (!tests[index].test) {
				messages.push(tests[index].message);
			}
		}
		if (messages.length) {
			showErrorBox("Import Educations", errorTitle + "\n\n" + messages.join("\n"));
			return true;
		}
		return false;
	}
                             
}

managerApp.controller('ImportEducationsCtrl', ImportEducationsCtrl);
