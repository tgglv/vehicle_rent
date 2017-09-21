function onSetRentalPoint() {
    var countryId = $('#country').val(),
        pointId = $('#point').val();

    if (-1 == countryId) {
        alert('Пожалуйста, выберите страну'); // TODO: заменить на модальное окно
        return false;
    }

    if (-1 == pointId) {
        alert('Пожалуйста, выберите пунтк проката'); // TODO: заменить на модальное окно
        return false;
    }

    jQuery.ajax({
        url: '/select_rental_point/country/' + countryId + '/point/' + pointId,
        type: 'PUT',
        success: function() {
            window.location = '/select_rental_point';
        },
        error: function() {
            alert('Произошла ошибка!'); // TODO: заменить на модальное окно
        }
    });
}

function onButtonSetRentClick() {
    $('#button-set-rent').prop("disabled", true)
    $('#button-set-rent').removeClass("btn-light")
    $('#button-set-rent').addClass("btn-primary");

    $('#button-set-return').prop("disabled", false)
    $('#button-set-return').removeClass("btn-primary")
    $('#button-set-return').addClass("btn-light");

    $('#return-vehicle-container').hide();
    $('#rent-vehicle-container').show();
}

function onButtonSetReturnClick() {
    $('#button-set-rent').prop("disabled", false);
    $('#button-set-rent').removeClass("btn-primary");
    $('#button-set-rent').addClass("btn-light");

    $('#button-set-return').prop("disabled", true)
    $('#button-set-return').removeClass("btn-light");
    $('#button-set-return').addClass("btn-primary");

    $('#rent-vehicle-container').hide();
    $('#return-vehicle-container').show();
}

function getPointId() {
    return $('#point_id').val();
}