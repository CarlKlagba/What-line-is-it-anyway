(function() {
    'use strict';

    angular
        .module('wliiaApp')
        .controller('MusicDialogController', MusicDialogController);

    MusicDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Music'];

    function MusicDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Music) {
        var vm = this;

        vm.music = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.music.id !== null) {
                Music.update(vm.music, onSaveSuccess, onSaveError);
            } else {
                Music.save(vm.music, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('wliiaApp:musicUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.addDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
