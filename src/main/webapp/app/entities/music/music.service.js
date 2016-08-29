(function() {
    'use strict';
    angular
        .module('wliiaApp')
        .factory('Music', Music);

    Music.$inject = ['$resource', 'DateUtils'];

    function Music ($resource, DateUtils) {
        var resourceUrl =  'api/music/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.addDate = DateUtils.convertDateTimeFromServer(data.addDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
