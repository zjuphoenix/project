/**
 * Created by wuhaitao on 2015/12/11.
 */
angular.module('hepatitis', ['angular-storage', 'ui.router', 'ngGrid', 'hepatitis.examination', 'hepatitis.datamining'])
    .constant('ENDPOINT_URI', '/api')
    .constant('BASE_URI', '')
    .config(function($stateProvider, $urlRouterProvider, $httpProvider) {
        $stateProvider
            .state('login', {
                url: '/login',
                templateUrl: 'app/templates/login.html',
                controller: 'LoginCtrl',
                controllerAs: 'login'
            })
            .state('home', {
                url: '/home',
                templateUrl: 'app/templates/home.html'
            })
            .state('users', {
                url: '/users',
                templateUrl: 'app/templates/userlist.html',
                controller: 'UserListCtrl',
                controllerAs: 'userlist'
            })
            .state('userAdd', {
                url: '/users/new',
                templateUrl: 'app/templates/useradd.html',
                controller: 'UserAddCtrl',
                controllerAs: 'useradd'
            })
            .state('userEdit', {
                url: '/users/:username',
                templateUrl: 'app/templates/useredit.html',
                controller: 'UserEditCtrl',
                controllerAs: 'useredit'
            })
            .state('patient', {
                url: '/patient',
                templateUrl: 'app/patient/patient.html',
                controller: 'PatientCtrl',
                controllerAs: 'patient'
            })
        ;

        $urlRouterProvider.otherwise('/home');

        $httpProvider.interceptors.push('APIInterceptor');
    })
    .service('APIInterceptor', function($rootScope, UserContext) {
        var service = this;

        service.request = function(config) {
            var currentUser = UserContext.getCurrentUser(),
                access_token = currentUser ? currentUser.access_token : null;

            if (access_token) {
                config.headers.authorization = access_token;
            }
            return config;
        };

        service.responseError = function(response) {
            if (response.status === 401) {
                $rootScope.$broadcast('unauthorized');
            }
            return response;
        };
    })
    .service('UserContext', function(store) {
        var service = this,
            currentUser = null,
            userPermissions = [];

        service.setCurrentUser = function(user) {
            currentUser = user;
            store.set('user', user);
            return currentUser;
        };

        service.getCurrentUser = function() {
            if (!currentUser) {
                currentUser = store.get('user');
            }
            return currentUser;
        };

        service.setPermissions = function(permissions) {
            userPermissions = permissions;
            store.set('permissions', permissions);
            return userPermissions;
        };

        service.resetPermissions = function() {
            userPermissions = [];
        };

        service.isPermitted = function(permission) {
            if (!userPermissions) {
                userPermissions = store.get('permissions');
            }
            if (userPermissions.length > 0) {
                for (var i = 0; i  < userPermissions.length; i++) {
                    var entry = userPermissions[i];
                    if (permission === entry.perm) {
                        return true;
                    }
                }
            }
            return false;
        }

    })
    .service('UserService', function($http, ENDPOINT_URI) {
        var service = this;

        service.getAllUser = function() {
            return $http.get(ENDPOINT_URI + '/users');
        };

        service.addUser = function(user) {
            return $http.post(ENDPOINT_URI + '/users', user);
        };

        service.getUserById = function(username) {
            return $http.get(ENDPOINT_URI + '/users/' + username);
        };

        service.updateUser = function(user) {
            return $http.put(ENDPOINT_URI + '/users/' + user.USERNAME, user);
        };

        service.deleteUser = function(username) {
            return $http.delete(ENDPOINT_URI + '/users/' + username);
        };

        service.getUserPermissions = function() {
            return $http.post(ENDPOINT_URI + '/permission');
        }

    })
    .service('LoginService', function($http, BASE_URI) {
        var service = this;

        function getLogUrl(action) {
            return getUrl() + action;
        }

        service.login = function(credentials) {
            return $http.post(BASE_URI + '/login', credentials);
        };

        service.logout = function() {
            return $http.post(BASE_URI + '/logout');
        };

        service.register = function(user) {
            return $http.post(BASE_URI + '/register', user);
        };
    })
    .controller('LoginCtrl', function($rootScope, $state, LoginService, UserContext){
        var login = this;

        function signIn(user) {
            LoginService.login(user)
                .then(function(response) {
                    if (response.status == 200) {
                        user.access_token = response.data.id;
                        UserContext.setCurrentUser(user);
                        $rootScope.$broadcast('authorized');
                        $state.go('home');
                    } else {
                        login.message = 'Wrong username or password';
                    }
                });
        }

        function register(user) {
            LoginService.register(user)
                .then(function(response) {
                    login(user);
                });
        }

        function submit(user) {
            login.newUser ? register(user) : signIn(user);
        }

        login.newUser = false;
        login.submit = submit;
        login.message = null;
    })
    .controller('MainCtrl', function ($rootScope, $state, LoginService, UserService, UserContext) {
        var main = this;

        function initPermission() {
            UserService.getUserPermissions()
                .then(function(response) {
                    UserContext.setPermissions(response.data);
                }, function(error) {
                   console.log(error);
                });
        }

        function logout() {
            LoginService.logout()
                .then(function(response) {
                    main.currentUser = UserContext.setCurrentUser(null);
                    UserContext.resetPermissions();
                    $state.go('login');
                }, function(error) {
                    console.log(error);
                });
        }

        $rootScope.$on('authorized', function() {
            main.currentUser = UserContext.getCurrentUser();
            initPermission();
        });

        $rootScope.$on('unauthorized', function() {
            main.currentUser = UserContext.setCurrentUser(null);
            UserContext.resetPermissions();
            $state.go('login');
        });

        initPermission();

        main.logout = logout;
        main.currentUser = UserContext.getCurrentUser();
        main.isPermitted = function(name) {
            return UserContext.isPermitted(name);
        }
    })
    .controller('HomeCtrl', function($scope){

    })
    .controller('UserListCtrl', function($scope, UserService) {

        function getAll() {
            UserService.getAllUser()
                .then(function(result) {
                    $scope.users = result.data;
                }, function(error) {
                    console.log(error);
                });
        }

        getAll();
    })
    .controller('UserAddCtrl', function($state, $scope, UserService) {
         $scope.master = {};

         $scope.addUser = function(user) {
            UserService.addUser(user)
                .then(function(response) {
                    $state.go('users');
                }, function(error) {
                   console.log(error);
                });

            $scope.reset = function() {
                $scope.user = angular.copy($scope.master);
            };

            $scope.reset();
        };

    })
    .controller('UserEditCtrl', function($state, $scope, $stateParams, UserService) {

        function getById(username) {
            UserService.getUserById(username)
                .then(function(response) {
                    $scope.user = response.data;
                }, function(error) {
                    console.log(error);
                });
        };

        $scope.update = function(user) {
                UserService.updateUser(user)
                    .then(function (response) {
                        $state.go('users');
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.delete = function(user) {
            var deleted = confirm('Are you absolutely sure you want to delete?');
            if (deleted) {
                UserService.deleteUser(user.USERNAME)
                    .then(function(response) {
                        $state.go('users');
                    }, function(error) {
                       console.log(error);
                    });
            }
        };

        getById($stateParams.username);

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
    });
