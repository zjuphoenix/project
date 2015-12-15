/**
 * Created by wuhaitao on 2015/12/12.
 */
/*var patientModule = angular.module('hepatitis.patient', ['ui.router']);
patientModule.config(function($stateProvider) {
    $stateProvider
        .state('patient', {
            url: '/patient',
            templateUrl: 'app/patient/patient.html',
            controller: 'PatientCtrl',
            controllerAs: 'patient'
        });
})
.service('PatientService', function($http) {
    var service = this;

    service.getAllPatient = function() {
        return $http.get('/patients');
    };

    service.getPatientById = function(id) {
        return $http.get('/patients/' + id);
    };

})
.controller('PatientCtrl', function($scope, PatientService) {
    function getAll() {
        PatientService.getAllPatient()
            .then(function(result) {
                $scope.patients = result.data;
            }, function(error) {
                console.log(error);
            });
    }
    getAll();
});*/
