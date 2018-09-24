'use strict';

function ImportEventsCtrl($scope, serverAPI) {

	var events = [];
	var eventsFileContent = undefined;

	$scope.selectedEventsFileChanged = function () {
		var selectedFiles = document.getElementById('eventsFile').files;
		if (selectedFiles && selectedFiles.length === 1) {
			var selectedFile = selectedFiles[0];

			var fr = new window.FileReader; 
			fr.onload = function (event) {
				eventsFileContent = event.target.result;
				parseEventsFromFile();
			};
			fr.readAsText(selectedFile);
		}
	};

	function importEvents() {
		function importAll(fromIndex) {
			if (fromIndex >= events.length) {
				return;
			}
			var event = events[fromIndex];
			event.imported = 'Importing...';

			serverAPI.createEvent(
					event.eventTypeId,
					event.startTime,
					event.endTime,
					event.title,
					event.description,
					event.isPublic,
			).then(function () {
				event.imported = 'YES';
	        		importAll(fromIndex + 1);
			}).catch(function (response) {
				event.imported = 'FAILED - ' + response.data.error + ', ' + response.data.reason;
	        		importAll(fromIndex + 1);
			});
		}

		importAll(0);
	}

	return {
		events: events,
		importEvents: importEvents,
	};

	
	function parseEventsFromFile() {
		var newEvents = [];

		var lines = eventsFileContent.match(/[^\r\n]+/g);
		for (var index = 0; index < lines.length; ++index) {
			var line = lines[index];
			var values = line.split("|");
		  
			var eventTypeId = values[0];
			var startTime = values[1];
			var endTime = values[2];
			var title = values[3];
			var description = values[4];
			var isPublic = values[5];
          
			if (hasError("Error was detected in line: \n\n" + line, [
                 { message: "Missing eventTypeId in line: " + line, test: eventTypeId.length > 0 },
                 { message: "Missing startTime in line: " + line, test: startTime.length > 0 },
                 { message: "Missing title: " + line, test: title.length > 0 },
            ])) {
				return;
			}

			newEvents.push({
				eventTypeId: eventTypeId,
				startTime: startTime,
				endTime: endTime,
				title: title,
				description: description,
				isPublic: !!isPublic,
				imported: '-'
			});
		}

		events.length = 0;
		Array.prototype.push.apply(events, newEvents);
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
			showErrorBox("Import Events", errorTitle + "\n\n" + messages.join("\n"));
			return true;
		}
		return false;
	}
                             
}

managerApp.controller('ImportEventsCtrl', ImportEventsCtrl);
