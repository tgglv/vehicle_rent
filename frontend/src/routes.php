<?php

const PAGE_RENT = 'rent_return_vehicle';
const PAGE_SELECT_POINT = 'select_rental_point';
const PAGE_TRACK = 'track_vehicle';

const PAGE_POINTS = 'rental_points';
const PAGE_VEHICLES = 'vehicles';
const PAGE_CUSTOMERS = 'customers';
const PAGE_HISTORY = 'history';
const PAGE_STATISTICS = 'statistics';

$app->get(
    '/[{name}]',
    function ($request, $response, $args) {
        $pageList = [
            PAGE_SELECT_POINT => 'Выбрать пункт',
            PAGE_RENT => 'Выдать/Принять ТС',
            PAGE_TRACK => 'Отследить ТС',
            PAGE_POINTS => 'Пункты проката',
            PAGE_VEHICLES => 'ТС',
            PAGE_CUSTOMERS => 'Клиенты',
            PAGE_HISTORY => 'История выдачи',
            PAGE_STATISTICS => 'Статистика',
        ];

        $availablePageList = array_keys($pageList);

        $page = (isset($args['name']) && in_array($args['name'], $availablePageList))
            ? $args['name'] :
            PAGE_SELECT_POINT;

        $countryId = (isset($this->session->countryId)) ? $this->session->countryId : -1;
        $pointId = (isset($this->session->countryId)) ? $this->session->pointId : -1;
        $linksDisabled = -1 == $countryId && -1 == $pointId;

        return $this->view->render(
            $response,
            $page . '.html.twig',
            [
                'current_page' => $page,
                'page_list' => $pageList,
                'country_id' => $countryId,
                'point_id' => $pointId,
                'default_page' => PAGE_SELECT_POINT,
                'links_disabled' => $linksDisabled,
            ]
        );
    }
);

$app->put(
    '/' . PAGE_SELECT_POINT . '/country/{country_id}/point/{point_id}',
    function ($request, $response, $args) {
        if (isset($args['country_id'])) {
            $this->session->countryId = (int)$args['country_id'];
        }
        if (isset($args['point_id'])) {
            $this->session->pointId = (int)$args['point_id'];
        }
        return 'OK';
    }
);