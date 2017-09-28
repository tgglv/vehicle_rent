var vehicleRentHistoryData = null;

window.onload = function () {
    loadVehicleTypes('#type option, #return-type option');
    loadVendors('#vendor option, #return-vendor option');

    loadCountries(null, null, function () {
        var emptyCallback = function () {
        };
        loadRentalPoints("#rent-point", emptyCallback);
        loadRentalPoints("#return-point", emptyCallback);
    });
};

function findVehicleToHistoryView() {
    $('#tbl-vehicles-to-history-view').hide();
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/history?' + $.param({
            type: $('#type').val(),
            vendor_id: $('#vendor').val(),
            license_plate: $('#license-plate').val(),
            date_rent_from: $('#date-rent-from').val(),
            date_rent_to: $('#date-rent-to').val(),
            date_return_from: $('#date-return-from').val(),
            date_return_to: $('#date-return-to').val(),
            customer: $('#customer').val(),
            rent_point_id: $('#rent-point').val(),
            return_point_id: $('#return-point').val(),
            show_in_rent_vehicle_only: $('#vehicle-in-rent-only:checked').length
        }),
        dataType: 'json',
        success: function (o) {
            vehicleRentHistoryData = o;
            var html = "";
            for (var vehicleId in o) {
                var data = o[vehicleId];

                if (typeof data['return_point_name'] == "undefined") {
                    data['return_point_name'] = '-';
                }
                if (typeof data['return_date'] == "undefined") {
                    data['return_date'] = '';
                } else {
                    data['return_date'] = '(' + data['return_date'] + ')'
                }

                html += '<tr data-rent-record-id="' + vehicleId + '">'
                    + '<td>' + vehicleTypeList[data['vehicle_type']] + '<br/>'
                    + data['vendor_name'] + '<br/>'
                    + data['vehicle_name'] + '<br/>'
                    + data['license_plate'] + '</td>'
                    + '<td>' + data['customer_name'] + '<br/>'
                    + '(' + data['customer_country_name'] + ')</td>'
                    + '<td>' + data['rent_point_name'] + '<br/>'
                    + '(' + data['rent_date'] + ')</td>'
                    + '<td style="text-align: center; vertical-align: middle;">' + data['return_point_name'] + '<br/>'
                    + data['return_date'] + '</td>'
                    + '</tr>';
            }
            $('#tbl-vehicles-to-history-view tbody').html(html);
            $('#tbl-vehicles-to-history-view').show();
        },
        error: function (o) {

        }
    });
}

function clearHistoryFilter() {
    $('#tbl-vehicles-to-history-view').hide();
    $('#tbl-vehicles-to-history-view tbody').html('');

    $('#type, #vendor, #rent-point, #return-point').val(-1);
    $('#license-plate, #date-rent-from, #date-rent-to, #date-return-from, #date-return-to, #customer').val('');
    $('#vehicle-in-rent-only').prop("checked", false)
}