(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .controller('MusicDetailController', MusicDetailController);

    MusicDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Music'];

    function MusicDetailController($scope, $rootScope, $stateParams, entity, Music) {
        var vm = this;

        vm.music = entity;

        var unsubscribe = $rootScope.$on('wliiaApp:musicUpdate', function(event, result) {
            vm.music = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
