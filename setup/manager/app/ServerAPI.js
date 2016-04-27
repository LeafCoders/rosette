'use strict';

function ServerAPIService($resource) {

    var rosetteUrl = '';
    var authJwt = undefined;

    this.setRosetteUrl = function (url) {
    	rosetteUrl = url ? url : '';
    }
    
    this.isAuthenticated = function () {
    	return !!authJwt;
    }
    
	this.login = function (username, password) {
        return $resource(rosetteUrl + '/auth/login')
        		.save({ username: username, password: btoa(password) }, {}, function success(data, header) {
        			authJwt = header('X-AUTH-TOKEN');
        		}, function failure(response) {
        			authJwt = undefined;
        			console.error(response);
        		})
    			.$promise;
	};

	this.uploadFile = function (folderId, file) {
        return $resource(
        		rosetteUrl + '/api/v1/uploads/:folderId',
        		{ folderId: '@folderId' },
        		{
        			upload: {
        				method: 'POST',
    					transformRequest: function formDataObject(data) {
    						var fd = new FormData();
    						fd.append('folderId', data.folderId);
    						fd.append('file', data.file);
    						fd.append('fileName', data.fileName);
    						return fd;
    					},
    					headers: { 'Content-Type': undefined, enctype: 'multipart/form-data', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
        		).upload({ folderId: folderId }, { file: file, fileName: file.name }).$promise;
	};

	this.readUploads = function (folderId) {
        return $resource(
        		rosetteUrl + '/api/v1/uploads/:folderId',
        		{ folderId: '@folderId' },
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
        		).read({ folderId: folderId }).$promise;
	};
	
	this.createEducation = function (educationTypeId, educationThemeId, time, authorName, authorUserId, title, recording) {
        return $resource(
        		rosetteUrl + '/api/v1/educations',
        		{},
        		{
        			create: {
        				method: 'POST',
    					headers: { 'Content-Type': 'application/json', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
        		).create({
        			type: 'simple',
        			educationType: { id: educationTypeId },
        			educationTheme: { id: educationThemeId },
        			time: toModelDate(time),
        			title: title,
        			recording: recording,
        			author: { ref: (authorUserId ? { id: authorUserId } : undefined), text: authorName }
    			}).$promise;
	};
	
	this.readEducations = function () {
        return $resource(
        		rosetteUrl + '/api/v1/educations',
        		{},
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
        		).read().$promise;
	};
	
	function toModelDate(time) {
		return time + ' Europe/Stockholm';
	}
}

managerApp.service('serverAPI', ServerAPIService);
