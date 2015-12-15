/**
 * Created by wuhaitao on 2015/12/13.
 */
angular.module('hepatitis.datamining', ['ui.router', 'hepatitis.datamining.analyse'])
    .config(function($stateProvider) {
        $stateProvider
            .state('datamining', {
                url: '/datamining',
                templateUrl: 'app/datamining/datamining.html',
                controller: 'DataminingCtrl',
                controllerAs: 'datamining'
            })
        ;
    })
    .controller('DataminingCtrl', function($scope, $http, $state, $stateParams) {
        $scope.compute = function(){
            $scope.test = $scope.sum - $scope.train;
        };
        $http.get('/examinations/count')
            .success(function(result) {
                console.log(result);
                var count = parseInt(result);
                $scope.sum = count;
                $scope.train = parseInt(count/2);
                $scope.test = $scope.sum - $scope.train;
            });
        $scope.train = function(){

        };
    });