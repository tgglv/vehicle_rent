var baseUrl = 'http://localhost:8888/vehiclerent';

var countryList = null;
var vehicleTypeList = null;
var rentalPoints = null;

function getCountryIdFromSession() {
    return $('#country_id').val();
}

function getCountryId() {
    return $('#country').val();
}

function setCountryId(id) {
    $('#country').val(id);
}

function getPointIdFromSession() {
    return $('#point_id').val();
}

function getPointId() {
    return $('#point').val();
}

function setPointId(id) {
    $('#point').val(id);
}


function loadCountries(selector, selectedCountryId, callback) {
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/country/list',
        dataType: 'json',
        success: function (o) {
            countryList = o;
            if (null != selector) {
                var html = '';
                for (var key in o) {
                    var value = o[key];
                    html += '<option value="' + key + '">' + value + "</option>";
                }
                $(selector + ' option').after(html);
                $(selector).val(selectedCountryId);
            }

            callback();
        },
        error: function (o) {

        }
    });
}

function loadVehicleTypes(selector) {
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/vehicle/type/list',
        dataType: 'json',
        success: function (o) {
            vehicleTypeList = o;
            if (typeof selector != "undefined") {
                var html = "";
                for (var i in o) {
                    html += '<option value="' + i + '">' + o[i] + '</option>';
                }
                $(selector).after(html);
            }
        },
        error: function (o) {

        }
    });
}

function loadVendors(selector) {
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/vehicle/vendor/list',
        dataType: 'json',
        success: function (o) {
            var html = "";
            for (var i in o) {
                html += '<option value="' + i + '">' + o[i] + '</option>';
            }
            $(selector).after(html);
        },
        error: function (o) {

        }
    });
}

function loadRentalPoints(selector, callback) {
    var fillOptions = function (selector, callback) {
        var html = '';
        for (var countryId in rentalPoints) {
            var points = rentalPoints[countryId];
            html += '<optgroup label="' + countryList[countryId] + '" data-country-id="' + countryId + '">';
            for (var pointId in points) {
                var name = points[pointId];
                html += '<option value="' + pointId + '">' + name + '</option>';
            }
            html += '</optgroup>';
        }
        $(selector + ' option').after(html);

        callback();
    };

    if (null == rentalPoints) {
        $.ajax({
            type: 'GET',
            url: getBaseUrl() + '/rent_point/list',
            dataType: 'json',
            success: function (o) {
                rentalPoints = o;
                fillOptions(selector, callback);
            },
            error: function (o) {

            }
        });
    } else {
        fillOptions(selector, callback);
    }
}

function onCountryChange() {
    var countryId = getCountryId();
    $('#point optgroup').each(function () {
        if (-1 != countryId && $(this).data('countryId') != countryId) {
            $(this).hide();
        }
        if (-1 == countryId || $(this).data('countryId') == countryId) {
            $(this).show();
        }
    });
    if (getSelectedRentalPointCountryId() != countryId) {
        setPointId(-1);
    }
}

function getSelectedRentalPointCountryId() {
    var parent = $('#point option[value="' + getPointId() + '"]').parent();
    return parent.prop("tagName") == 'OPTGROUP' ? parent.data('countryId') : -1;
}

function onRentalPointChange() {
    setCountryId(getSelectedRentalPointCountryId());
    onCountryChange();
}

function getBaseUrl() {
    return baseUrl;
}