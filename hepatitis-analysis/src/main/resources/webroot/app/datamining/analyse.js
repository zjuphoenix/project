/**
 * Created by wuhaitao on 2015/12/13.
 */
angular.module('hepatitis.datamining.analyse', ['ui.router'])
    .constant('ENDPOINT_URI', '/api')
    .config(function($stateProvider) {
        $stateProvider
            .state('analyse', {
                url: '/datamining/analyse',
                templateUrl: 'app/datamining/analyse.html',
                controller: 'AnalyseCtrl',
                controllerAs: 'analyse'
            })
            .state('result', {
                url: '/datamining/analyse/:result',
                templateUrl: 'app/datamining/result.html',
                controller: 'AnalyseResultCtrl'
            })
        ;
    })
    .directive('fileModel', function ($parse) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                console.log(attrs);
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;

                element.bind('change', function(){
                    scope.$apply(function(){
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    })
    .service('AnalyseService', function ($http) {
        var service = this;

        service.uploadFileToAnalyse = function(file){
            var fd = new FormData();
            fd.append('file', file);
            return $http.post('/datamining/analyse', fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            });
        }
    })
    .controller('AnalyseCtrl', function($scope, $state,  AnalyseService) {
        $scope.myFile = null;
        $scope.filename = null;

        $scope.uploadFile = function() {
            console.log('Start to upload..');
            var file = $scope.myFile;
            AnalyseService.uploadFileToAnalyse(file)
                .then(function(response) {
                    $state.go('result', {result : response.data.result});
                }, function(error) {
                    console.log(error);
                });
        };
    })
    .controller('AnalyseResultCtrl', function($scope, $stateParams) {
        $scope.result = $stateParams.result;
        console.log($scope.result);
    });