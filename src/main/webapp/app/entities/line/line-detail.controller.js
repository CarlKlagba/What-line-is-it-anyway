(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .controller('LineDetailController', LineDetailController);

    LineDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Line'];

    function LineDetailController($scope, $rootScope, $stateParams, entity, Line) {
        var vm = this;

        vm.line = entity;

        var unsubscribe = $rootScope.$on('wliiaApp:lineUpdate', function(event, result) {
            vm.line = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
