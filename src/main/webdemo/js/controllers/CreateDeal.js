'use strict';

define([
    'angular',
    'maskedInput',
    'utils/semantic',
    'services/NodeApi',
    'Deal'
], (angular, maskedInput, semantic, nodeApi, Deal) => {
    angular.module('irsViewer').controller('CreateDealController', function CreateDealController($http, $scope, $location, nodeService) {
        semantic.init();
        $scope.isLoading = nodeService.isLoading;
        $scope.deal = nodeService.newDeal();
        $scope.createDeal = () => {
            nodeService.createDeal(new Deal($scope.deal))
            .then((tradeId) => $location.path('#/deal/' + tradeId), (resp) => {
                $scope.formError = resp.data;
            });
        };
        $('input.percent').mask("9.999999%", {placeholder: "", autoclear: false});
    });
});