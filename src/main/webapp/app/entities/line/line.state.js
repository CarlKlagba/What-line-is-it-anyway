(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('line', {
            parent: 'entity',
            url: '/line',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'wliiaApp.line.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/line/lines.html',
                    controller: 'LineController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('line');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('line-detail', {
            parent: 'entity',
            url: '/line/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'wliiaApp.line.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/line/line-detail.html',
                    controller: 'LineDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('line');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Line', function($stateParams, Line) {
                    return Line.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('line.new', {
            parent: 'line',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-dialog.html',
                    controller: 'LineDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                line: null,
                                description: null,
                                author: null,
                                from: null,
                                addDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('line');
                });
            }]
        })
        .state('line.edit', {
            parent: 'line',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-dialog.html',
                    controller: 'LineDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Line', function(Line) {
                            return Line.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('line.delete', {
            parent: 'line',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-delete-dialog.html',
                    controller: 'LineDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Line', function(Line) {
                            return Line.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
