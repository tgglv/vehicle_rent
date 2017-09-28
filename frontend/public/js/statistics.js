window.onload = function () {
    loadCountries('#country', getCountryIdFromSession(), function() {
        loadRentalPoints('#point', function(){
            setPointId(getPointIdFromSession());
            // скроем пункты проката пока не выбрана страна
            onCountryChange();
        });
    });
    loadVehicleTypes();
};

function getStatistics() {
    hideTablesAndLables();
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/statistics?' + $.param({
            country_id: getCountryId(),
            point_id: getPointId()
        }),
        dataType: 'json',
        success: function (o) {
            if (0 in o) {
                var html = "";
                for (var vehicleType in o[0]) {
                    html += '<tr><td>' + vehicleTypeList[vehicleType] + ' </td><td>' + o[0][vehicleType] + '</td></tr>';
                }
                $('#tbl-statistics-by-vehicle-type tbody').html(html);
                $('#tbl-statistics-by-vehicle-type, #lbl-statistics-by-vehicle-type').show();
            }
            if (1 in o) {
                var html = "";
                for (var vehicleType in o[1]) {
                    for (var vendorName in o[1][vehicleType]) {
                        html += '<tr><td>' + vehicleTypeList[vehicleType] + ' </td>' +
                            '<td>' + vendorName + '</td>' +
                            '<td>' + o[1][vehicleType][vendorName] + '</td></tr>';
                    }
                }
                $('#tbl-statistics-by-vehicle-type-and-vendor tbody').html(html);
                $('#tbl-statistics-by-vehicle-type-and-vendor, #lbl-statistics-by-vehicle-type-and-vendor').show();
            }
        },
        error: function (o) {

        }
    });
}

function hideTablesAndLables() {
    $('#tbl-statistics-by-vehicle-type, #tbl-statistics-by-vehicle-type-and-vendor,' +
        '#lbl-statistics-by-vehicle-type, #lbl-statistics-by-vehicle-type-and-vendor').hide();
}

function clearStatisticsFilter() {
    $('#country, #point').val(-1);

    hideTablesAndLables();
}