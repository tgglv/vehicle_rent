window.onload = function () {
    loadCountries('#country', getCountryIdFromSession(), function() {
        loadRentalPoints('#point', function(){
            setPointId(getPointIdFromSession());
            // скроем пункты проката пока не выбрана страна
            onCountryChange();
        });
    });
};

function onSetRentalPoint() {
    var countryId = getCountryId(),
        pointId = getPointId();

    if (-1 == countryId) {
        alert('Пожалуйста, выберите страну'); // TODO: заменить на модальное окно
        return false;
    }

    if (-1 == pointId) {
        alert('Пожалуйста, выберите пунтк проката'); // TODO: заменить на модальное окно
        return false;
    }

    jQuery.ajax({
        url: '/select_rental_point/country/' + countryId + '/point/' + pointId + '?name=' + $("#point option:selected").text(),
        type: 'PUT',
        success: function() {
            window.location = '/select_rental_point';
        },
        error: function() {
            alert('Произошла ошибка!'); // TODO: заменить на модальное окно
        }
    });
}

function clearSelectRentalPointFilter() {
    setCountryId(-1);
    onCountryChange();
    setPointId(-1);
}