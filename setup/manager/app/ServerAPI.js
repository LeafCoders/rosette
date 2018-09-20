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
        		.save({}, { username: username, password: password }, function success(data, header) {
        			authJwt = header('X-AUTH-TOKEN');
        		}, function failure(response) {
        			authJwt = undefined;
        			console.error(response);
        		})
    			.$promise;
	};

	this.uploadFile = function (folderId, file) {
        return $resource(
        		rosetteUrl + '/api/files',
        		{},
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
    		).upload({}, { folderId: folderId, file: file, fileName: file.name }).$promise;
	};

	this.readUploads = function (assetFolderId) {
        return $resource(
        		rosetteUrl + '/api/assets',
        		{ assetFolderId: assetFolderId },
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).read({ assetFolderId: assetFolderId }).$promise;
	};
	
	this.createResource = function (name) {
        return $resource(
        		rosetteUrl + '/api/resources',
        		{},
        		{
        			create: {
        				method: 'POST',
    					headers: { 'Content-Type': 'application/json', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).create({
    			name: name
		}).$promise;
	};
	
	this.addResourceTypeToResource = function (resourceId, resourceTypeId) {
        return $resource(
        		rosetteUrl + '/api/resources/' + resourceId + '/resourceTypes/' + resourceTypeId,
        		{},
        		{
        			create: {
        				method: 'POST',
        				isArray: true,
    					headers: { 'Content-Type': 'application/json', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).create({}).$promise;
	};
	
	this.readResources = function () {
        return $resource(
        		rosetteUrl + '/api/resources',
        		{},
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).read({}).$promise;
	};
	
	this.createArticleSerie = function (articleTypeId, idAlias, title) {
        return $resource(
        		rosetteUrl + '/api/articleSeries',
        		{},
        		{
        			create: {
        				method: 'POST',
    					headers: { 'Content-Type': 'application/json', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).create({
    			articleTypeId: articleTypeId,
    			idAlias: idAlias,
    			title: title
		}).$promise;
	};
	
	this.readArticleSeries = function (articleTypeId) {
        return $resource(
        		rosetteUrl + '/api/articleSeries',
        		{ articleTypeId: articleTypeId },
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
		).read({ articleTypeId: articleTypeId }).$promise;
	};
	
	this.createArticle = function (articleTypeId, articleSerieId, time, authorId, title, content, recordingId) {
        return $resource(
        		rosetteUrl + '/api/articles',
        		{},
        		{
        			create: {
        				method: 'POST',
    					headers: { 'Content-Type': 'application/json', 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).create({
    			articleTypeId: articleTypeId,
    			articleSerieId: articleSerieId,
    			time: toModelDate(time),
    			authorIds: [authorId],
    			title: title,
    			contentRaw: content,
    			contentHtml: content,
    			recordingId: recordingId,
		}).$promise;
	};
	
	this.readArticles = function (articleTypeId) {
        return $resource(
        		rosetteUrl + '/api/articles',
        		{ articleTypeId: articleTypeId },
        		{
        			read: {
        				method: 'GET',
        				isArray: true,
    					headers: { 'X-AUTH-TOKEN': authJwt }
        			}
        		}
    		).read({ articleTypeId: articleTypeId }).$promise;
	};
	
	function toModelDate(time) {
		return time;
	}
}

managerApp.service('serverAPI', ServerAPIService);
