(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .controller('LineController', LineController);

    LineController.$inject = ['$scope', '$state', 'Line'];

    function LineController ($scope, $state, Line) {
        var vm = this;
        
        vm.lines = [];

        loadAll();

        function loadAll() {
            Line.query(function(result) {
                vm.lines = result;
            });
        }
    }
})();
