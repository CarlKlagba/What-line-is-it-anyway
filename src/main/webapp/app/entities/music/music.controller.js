(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .controller('MusicController', MusicController);

    MusicController.$inject = ['$scope', '$state', 'Music'];

    function MusicController ($scope, $state, Music) {
        var vm = this;
        
        vm.music = [];

        loadAll();

        function loadAll() {
            Music.query(function(result) {
                vm.music = result;
            });
        }
    }
})();
