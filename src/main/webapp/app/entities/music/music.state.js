(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('music', {
            parent: 'entity',
            url: '/music',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'wliiaApp.music.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/music/music.html',
                    controller: 'MusicController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('music');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('music-detail', {
            parent: 'entity',
            url: '/music/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'wliiaApp.music.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/music/music-detail.html',
                    controller: 'MusicDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('music');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Music', function($stateParams, Music) {
                    return Music.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('music.new', {
            parent: 'music',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-dialog.html',
                    controller: 'MusicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                link: null,
                                addDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: true });
                }, function() {
                    $state.go('music');
                });
            }]
        })
        .state('music.edit', {
            parent: 'music',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-dialog.html',
                    controller: 'MusicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Music', function(Music) {
                            return Music.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('music.delete', {
            parent: 'music',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-delete-dialog.html',
                    controller: 'MusicDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Music', function(Music) {
                            return Music.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
