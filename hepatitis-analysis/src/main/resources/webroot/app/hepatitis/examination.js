/**
 * Created by wuhaitao on 2015/12/13.
 */
angular.module('hepatitis.examination', ['ui.router'])
    .config(function($stateProvider) {
        $stateProvider
            .state('examinations', {
                url: '/examinations',
                templateUrl: 'app/hepatitis/examinations.html',
                controller: 'ExaminationListCtrl',
                controllerAs: 'examinations'
            })
            .state('examination', {
                url: '/examination/:id',
                templateUrl: 'app/hepatitis/examination-detail.html',
                controller: 'ExaminationCtrl'
            })
        ;
    })
    .controller('ExaminationListCtrl2', function($scope, $http, $state, $stateParams) {
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        $scope.totalServerItems = 0;
        $scope.pagingOptions = {
            pageSizes: [5, 10, 20],
            pageSize: 5,
            currentPage: 1
        };
        $scope.setPagingData = function(data, page, pageSize) {
            /*jQuery.noConflict();*/
            /*var pagedData = data.slice((page - 1) * pageSize, page * pageSize);*/
            console.log(data);
            var pagedData = data;
            $scope.examinations = pagedData;
            $scope.totalServerItems = data.length;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };

        console.log($stateParams);
        $scope.getPagedDataAsync = function(pageSize, page, searchText) {
            $http.get('/examinations')
                .success(function(data) {
                    $scope.setPagingData(data, page, pageSize);
                });
        };

        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);

        $scope.$watch('pagingOptions', function(newVal, oldVal) {
            if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);
        $scope.$watch('filterOptions', function(newVal, oldVal) {
            if (newVal !== oldVal) {
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);

        $scope.gridOptions = {
            data: 'examinations',
            rowTemplate: '<div style="height: 100%"><div ng-style="{ \'cursor\': row.cursor }" ng-repeat="col in renderedColumns" ng-class="col.colIndex()" class="ngCell ">' +
            '<div class="ngVerticalBar" ng-style="{height: rowHeight}" ng-class="{ ngVerticalBarVisible: !$last }"> </div>' +
            '<div ng-cell></div>' +
            '</div></div>',
            multiSelect: false,
            enableCellSelection: true,
            enableRowSelection: false,
            enableCellEdit: true,
            enablePinning: true,
            columnDefs: [{
                field: 'id',
                displayName: '检查id',
                width: 60,
                pinnable: false,
                sortable: false
            }, {
                field: 'gender',
                displayName: '性别',
                enableCellEdit: true
            }, {
                field: 'age',
                displayName: '年龄',
                enableCellEdit: true,
                width: 220
            }, {
                field: 'occupation',
                displayName: '职业',
                enableCellEdit: true,
                width: 120
            }, {
                field: 'city',
                displayName: '所在城市',
                enableCellEdit: true,
                width: 120
            }, {
                field: 'id',
                displayName: '操作',
                enableCellEdit: false,
                sortable: false,
                pinnable: false,
                cellTemplate: '<div><a ui-sref="bookdetail({id:row.getProperty(col.field)})" id="{{row.getProperty(col.field)}}">详情</a></div>'
            }],
            enablePaging: true,
            showFooter: true,
            totalServerItems: 'totalServerItems',
            pagingOptions: $scope.pagingOptions,
            filterOptions: $scope.filterOptions,
            plugins: [new ngGridFlexibleHeightPlugin()]
        };
    })
    .controller('ExaminationListCtrl', function($scope, $http, $state, $stateParams) {
        $http.get('/examinations')
            .success(function(result) {
                console.log(result);
                $scope.examinations = result;
            });
    })
    .controller('ExaminationCtrl', function($scope, $http, $state, $stateParams) {
        $http.get('/examinations/detail/'+$stateParams.id)
            .success(function(result) {
                $scope.examination = result;
            });

    });