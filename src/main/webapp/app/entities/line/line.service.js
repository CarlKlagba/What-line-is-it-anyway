(function() {
    'use strict';
    angular
        .module('wliiaApp')
        .factory('Line', Line);

    Line.$inject = ['$resource', 'DateUtils'];

    function Line ($resource, DateUtils) {
        var resourceUrl =  'api/lines/:id';

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
