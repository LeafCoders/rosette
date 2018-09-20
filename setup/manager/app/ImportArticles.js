'use strict';

function ImportArticlesCtrl($scope, serverAPI) {

	var articles = [];
	var articlesFileContent = undefined;
	$scope.recordingsFolderId = '';
	$scope.articleTypeId = '';
	$scope.authorResourceTypeId = '';

	$scope.selectedArticlesFileChanged = function () {
		var selectedFiles = document.getElementById('articlesFile').files;
		if (selectedFiles && selectedFiles.length === 1) {
			var selectedFile = selectedFiles[0];

			var fr = new window.FileReader; 
			fr.onload = function (event) {
				articlesFileContent = event.target.result;
				parseArticlesFromFile();
			};
			fr.readAsText(selectedFile);
		}
	};

	function createArticleSeries() {
		serverAPI.readArticleSeries($scope.articleTypeId).then(function (articleSeriesAtServer) {
			
			function findArticleSerie(articleSerieTitle) {
				for (var index = 0; index < articleSeriesAtServer.length; ++index) {
					if (articleSeriesAtServer[index].title === articleSerieTitle) {
						return articleSeriesAtServer[index];
					}
				}
				return undefined;
			}

			function importAll(fromIndex) {
				if (fromIndex >= articles.length) {
					return;
				}
				var article = articles[fromIndex];
				article.imported = 'Importing...';

				var articleSerie = findArticleSerie(article.articleSerieTitle);
				if (articleSerie) {
					article.imported = 'SKIPPED - Article serie already exist at Rosette server with name: ' + article.articleSerieTitle;
					importAll(fromIndex + 1);
				} else if (!article.articleSerieTitle) {
					article.imported = 'SKIPPED - Has no specified article serie';
					importAll(fromIndex + 1);
				} else {
					serverAPI.createArticleSerie(
							$scope.articleTypeId,
							String.fromCharCode(25*Math.random()+97, 25*Math.random()+97, 25*Math.random()+97, 25*Math.random()+65, 25*Math.random()+65, 25*Math.random()+65),
							article.articleSerieTitle,
					).then(function (createdArticleSerie) {
						article.imported = 'YES - Created article serie with name: ' + article.articleSerieTitle;
						articleSeriesAtServer.push(createdArticleSerie);
						importAll(fromIndex + 1);
					}).catch(function (response) {
						article.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
			        		importAll(fromIndex + 1);
					});
				}
			};
			
			importAll(0);
		}).catch(function (response) {
			hasError('Could not read resources from Rosette server', { message: response.statusText });
		});
	}
		
	function createResources() {
		serverAPI.readResources().then(function (resourcesAtServer) {
			
			function findResource(resourceName) {
				for (var index = 0; index < resourcesAtServer.length; ++index) {
					if (resourcesAtServer[index].name === resourceName) {
						return resourcesAtServer[index];
					}
				}
				return undefined;
			}

			function importAll(fromIndex) {
				if (fromIndex >= articles.length) {
					return;
				}
				var article = articles[fromIndex];
				article.imported = 'Importing...';

				var authorResource = findResource(article.authorName);
				if (authorResource) {
					article.imported = 'SKIPPED - Resource already exist at Rosette server with name: ' + article.authorName;
					importAll(fromIndex + 1);
				} else if (!article.authorName) {
					article.imported = 'SKIPPED - Has no specified author';
					importAll(fromIndex + 1);
				} else {
					serverAPI.createResource(
							article.authorName,
					).then(function (createdResource) {
						article.imported = 'YES - Created resource with name: ' + article.authorName;
						resourcesAtServer.push(createdResource);
						
						serverAPI.addResourceTypeToResource(createdResource.id, $scope.authorResourceTypeId).then(function () {
							importAll(fromIndex + 1);
						}).catch(function (response) {
							article.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
				        		importAll(fromIndex + 1);
						});
					}).catch(function (response) {
						article.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
			        		importAll(fromIndex + 1);
					});
				}
			};
			
			importAll(0);
		}).catch(function (response) {
			hasError('Could not read resources from Rosette server', { message: response.statusText });
		});
	}
		
	function importArticles() {
		serverAPI.readUploads($scope.recordingsFolderId).then(function (uploads) {
			serverAPI.readResources().then(function (resourcesAtServer) {
				serverAPI.readArticleSeries($scope.articleTypeId).then(function (articleSeriesAtServer) {
					serverAPI.readArticles($scope.articleTypeId).then(function (articlesAtServer) {
	
						function findUpload(fileName) {
							for (var index = 0; index < uploads.length; ++index) {
								if (uploads[index].fileName === fileName) {
									return uploads[index];
								}
							}
							return undefined;
						}
		
						function findResource(resourceName) {
							for (var index = 0; index < resourcesAtServer.length; ++index) {
								if (resourcesAtServer[index].name === resourceName) {
									return resourcesAtServer[index];
								}
							}
							return undefined;
						}
		
						function findArticleSerie(articleSerieTitle) {
							for (var index = 0; index < articleSeriesAtServer.length; ++index) {
								if (articleSeriesAtServer[index].title === articleSerieTitle) {
									return articleSeriesAtServer[index];
								}
							}
							return undefined;
						}

						function findArticle(time, title) {
							for (var index = 0; index < articlesAtServer.length; ++index) {
								if (articlesAtServer[index].time.indexOf(time) >= 0 && articlesAtServer[index].title === title) {
									return articlesAtServer[index];
								}
							}
							return undefined;
						}
						
						function importAll(fromIndex) {
							if (fromIndex >= articles.length) {
								return;
							}
							var article = articles[fromIndex];
							article.imported = 'Importing...';
		
							var recording = findUpload(article.recording);
							var authorResource = findResource(article.authorName);
							var articleSerie = findArticleSerie(article.articleSerieTitle);
							if (article.recording && !recording) {
								article.imported = 'FAILED - Could not find specified recording at Rosette server for filename: ' + article.recording;
								importAll(fromIndex + 1);
							} else if (article.authorName && !authorResource) {
								article.imported = 'FAILED - Could not find specified author resource at Rosette server for name: ' + article.authorName;
								importAll(fromIndex + 1);
							} else if (!articleSerie) {
								article.imported = 'FAILED - Could not find specified article serie at Rosette server for title: ' + article.articleSerieTitle;
								importAll(fromIndex + 1);
							} else if (findArticle(article.time, article.title)) {
								article.imported = 'FAILED - Already exist at rosette server';
								importAll(fromIndex + 1);
							} else {
								serverAPI.createArticle(
										article.articleTypeId,
										articleSerie.id,
										article.time,
										authorResource ? authorResource.id : undefined,
										article.title,
										article.content,
										recording ? recording.id : undefined
								).then(function () {
									article.imported = 'YES';
						        		importAll(fromIndex + 1);
								}).catch(function (response) {
									article.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
						        		importAll(fromIndex + 1);
								});
							}
						};
						
						importAll(0);
					}).catch(function (response) {
						hasError('Could not read articles from Rosette server', { message: response.statusText });
					});
				}).catch(function (response) {
					hasError('Could not read article series from Rosette server', { message: response.statusText });
				});
			}).catch(function (response) {
				hasError('Could not read resources from Rosette server', { message: response.statusText });
			});
		}).catch(function (response) {
			hasError('Could not read recordings from Rosette server', { message: response.statusText });
		});
	}

	return {
		articles: articles,
		createArticleSeries: createArticleSeries,
		createResources: createResources,
		importArticles: importArticles,
	};

	
	function parseArticlesFromFile() {
		var newArticles = [];

		var lines = articlesFileContent.match(/[^\r\n]+/g);
		for (var index = 0; index < lines.length; ++index) {
			var line = lines[index];
			var values = line.split("|");
		  
			var articleTypeId = values[0];
			var articleSerieTitle = values[1];
			var time = values[2];
			var authorName = values[3];
			var title = values[4];
			var content = values[5];
			var recording = values[6];
          
			if (hasError("Error was detected in line: \n\n" + line, [
                 { message: "Missing articleTypeId in line: " + line, test: articleTypeId.length > 0 },
                 { message: "Missing articleSerieTitle in line: " + line, test: articleSerieTitle.length > 0 },
                 { message: "Missing time in line: " + line, test: time.length > 0 },
                 { message: "Missing title in line: " + line, test: title.length > 0 }
            ])) {
				return;
			}

			newArticles.push({
				articleTypeId: articleTypeId,
				articleSerieTitle: articleSerieTitle,
				time: time,
				authorName: authorName ? authorName.trim() : undefined,
				title: title,
				content: content,
				recording: recording,
				imported: '-'
			});
		}

		articles.length = 0;
		Array.prototype.push.apply(articles, newArticles);
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
			showErrorBox("Import Articles", errorTitle + "\n\n" + messages.join("\n"));
			return true;
		}
		return false;
	}
                             
}

managerApp.controller('ImportArticlesCtrl', ImportArticlesCtrl);
