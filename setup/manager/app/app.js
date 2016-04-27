'use strict';

var managerApp = angular.module('managerApp', ['ngResource']); //[, 'ngAnimate', 'ngSanitize']);


function isRunningWithElectron() {
	return window && window.process && window.process.type;
}

function showErrorBox(title, message) {
	if (isRunningWithElectron()) {
		var dialog = require('electron').remote.dialog;
		dialog.showErrorBox(title, message);
	} else {
		alert(title + '\n\n' + message);
	}
}