'use strict';

function LoginCtrl($scope, serverAPI) {

	$scope.rosetteUrl = 'http://localhost:9000';
	$scope.username = 'admin@admin.se';
	$scope.password = 'password';
	
	serverAPI.setRosetteUrl($scope.rosetteUrl);
	
	function login() {
		serverAPI.login($scope.username, $scope.password).catch(function (response) {
			showErrorBox('Failed to login', response.statusText);
		});
	}

	return {
		login: login,
		setRosetteUrl: serverAPI.setRosetteUrl,
		isAuthenticated: serverAPI.isAuthenticated
	};
}

managerApp.controller('LoginCtrl', LoginCtrl);
