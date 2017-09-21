var countryList = null;
var vehicleTypeList = null;

function loadCountries(selector, selectedCountryId, callback) {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8000/country/list',
        dataType: 'json',
        success: function (o) {
            countryList = o;
            var html = '';
            for (var key in o) {
                var value = o[key];
                html += '<option value="' + key + '">' + value + "</option>";
            }
            $(selector + ' option').after(html);
            $(selector).val(selectedCountryId);

            callback();
        },
        error: function (o) {

        }
    });
}

function loadVehicleTypes(selector) {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8000/vehicle/type/list',
        dataType: 'json',
        success: function (o) {
            vehicleTypeList = o;
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

function loadVendors(selector) {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8000/vehicle/vendor/list',
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