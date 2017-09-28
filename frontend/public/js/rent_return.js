var vehicleList = null;

var selectedVehicleId = null;
var selectedCustomerId = null;
var returnVehicleList = null;

var findVehicleResultButtonCaption = 'Принять ТС';

window.onload = function () {
    loadVehicleTypes('#type option, #return-type option');
    loadVendors('#vendor option, #return-vendor option');

    loadCountries('#country', -1, function () {
    });
};

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

function findVehicleToRent() {
    var type = $('#type').val();
    var vendorId = $('#vendor').val();

    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/rent_point/vehicle/list?' + $.param({
            point_id: getPointIdFromSession(),
            type: type,
            vendor: vendorId
        }),
        dataType: 'json',
        success: function (o) {
            vehicleList = o;
            var html = "";
            for (var vehicleId in o) {
                var vehicle = o[vehicleId];
                html += '<tr data-id="' + vehicleId + '">' +
                    '<td>' + vehicleTypeList[vehicle['vehicle_type']] + '</td>' +
                    '<td>' + vehicle['vendor_name'] + '</td>' +
                    '<td>' + vehicle['vehicle_name'] + '</td>' +
                    '<th scope="row">' + vehicle['license_plate'] + '</th>' +
                    '<td><button type="button" class="btn btn-success" data-vehicle-id="' + vehicleId +
                    '" onclick="selectVehicleToRent(this);">Выбрать</button>' +
                    '</tr>';
            }
            if (html.length > 0) {
                $('#tbl-vehicles-to-rent tbody').html(html)
                $('#tbl-vehicles-to-rent').show();
            }
        },
        error: function (o) {

        }
    })
    ;
}

function selectVehicleToRent(o) {
    selectedVehicleId = $(o).data('vehicleId');

    $('#select-vehicle-container').hide();
    $('#select-customer-container').show();
}

function findRentVehicleCustomer() {
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/customer/list?country=' + getCountryId() + '&customer=' + $('#customer').val(),
        dataType: 'json',
        success: function (o) {
            var html = "";
            for (var customerId in o) {
                html += '<tr data-id="' + customerId + '">' +
                    '<td class="country-name">' + o[customerId]['country_name'] + '</td>' +
                    '<td class="customer-name">' + o[customerId]['customer_name'] + '</td>' +
                    '<td><button type="button" class="btn btn-success" data-customer-id="' + customerId + '" onclick="selectRentVehicleCustomer(this);">Выбрать</button>' +
                    '</tr>';
            }
            $('#tbl-customers tbody').html(html);
            $('#tbl-customers').show();

        },
        error: function (o) {

        }
    });
}

function selectRentVehicleCustomer(o) {
    selectedCustomerId = $(o).data('customerId');

    var tr = $(o).parent().parent();
    var customerName = tr.find('td.customer-name').text();
    var countryName = tr.find('td.country-name').text();

    $('#select-customer-container').hide();

    // TODO: убедиться, что vehicleList заполнен
    $('#confirm-vehicle-type').text(vehicleList[selectedVehicleId]['vehicle_type']);
    $('#confirm-vendor').text(vehicleList[selectedVehicleId]['vendor_name']);
    $('#confirm-vehicle').text(vehicleList[selectedVehicleId]['vehicle_name']);
    $('#confirm-license-plate').text(vehicleList[selectedVehicleId]['license_plate']);
    $('#confirm-customer').text(customerName);
    $('#confirm-customer-country').text(countryName);

    $('#confirm-rent-container, #confirm-form').show();
}

function finishVehicleRent() {
    if (confirm("Вы уверены в том что выдаёте ТС?")) {
        $.ajax({
            type: 'POST',
            url: getBaseUrl() + '/rent_point/rent?' + $.param({
                point_id: getPointIdFromSession(),
                vehicle_id: selectedVehicleId,
                customer_id: selectedCustomerId
            }),
            dataType: 'json',
            success: function (o, statusText, xhr) {
                if (200 == xhr.status) {
                    alert("Вы успешно выдали ТС");
                    window.location.href = '/rent_return_vehicle';
                } else {
                    alert("Что-то пошло не так: " + o.error);
                }
            },
            error: function (o) {

            }
        });
    }
}

function returnToVehicleSelect() {
    selectedVehicleId = null;
    selectedCustomerId = null;
    $('#select-customer-container').hide();
    $('#confirm-rent-container').hide();
    $('#select-vehicle-container').show();
}

function returnToCustomerSelect() {
    selectedCustomerId = null;
    $('#select-vehicle-container').hide();
    $('#confirm-rent-container').hide();
    $('#select-customer-container').show();
}

function findVehicleToReturn() {
    var vehicleType = $('#return-type').val();
    var vendorId = $('#return-vendor').val();
    var vehicleName = $('#return-vehicle-name').val();
    var licensePlate = $('#return-license-plate').val();
    var customer = $('#return-customer').val();

    if (-1 == getPointIdFromSession()) {
        alert("Недостаточно данных для выполнения запроса");
        return false;
    }

    $.ajax({
        type: "GET",
        url: getBaseUrl() + '/vehicle/return?' + $.param({
            license_plate: licensePlate,
            type: vehicleType,
            vendor: vendorId,
            name: vehicleName,
            customer: customer
        }),
        dataType: 'json',
        success: function (o, status, xhr) {
            if ("200" != xhr.status) { // Отладить
                alert("Что-то пошло не так, " + status);
            } else {
                returnVehicleList = o;
                var html = "";
                for (var rentRecordId in o) {
                    html += '<tr data-id="' + rentRecordId + '">' +
                        '<td>' + o[rentRecordId]['vehicle_type'] + '</td>' +
                        '<td>' + o[rentRecordId]['vendor_name'] + '</td>' +
                        '<td>' + o[rentRecordId]['vehicle_name'] + '</td>' +
                        '<td>' + o[rentRecordId]['license_plate'] + '</td>' +
                        '<td>' + o[rentRecordId]['rent_time'] + '</td>' +
                        '<td>' + o[rentRecordId]['customer_name'] + '</td>' +
                        '<td>' + o[rentRecordId]['country_name'] + '</td>' +
                        '<td><button type="button" class="btn btn-success" data-rent-record-id="' +
                        rentRecordId + '" data-vehicle-id="' + o[rentRecordId]['vehicle_id'] +
                        '" onclick="finishVehicleReturn(this);">' + findVehicleResultButtonCaption + '</button>' +
                        '</tr>'
                }

                $('#tbl-vehicles-to-return tbody').html(html);
                $('#tbl-vehicles-to-return').show();
            }
        },
        error: function (o) {

        }
    });
}

var finishVehicleReturn = function (o) {
    var rentRecordId = $(o).data('rentRecordId');
    var type = vehicleTypeList[returnVehicleList[rentRecordId]['vehicle_type']].toLowerCase();
    var vendor = returnVehicleList[rentRecordId]['vendor_name'];
    var model = returnVehicleList[rentRecordId]['vehicle_name'];
    var licensePlate = returnVehicleList[rentRecordId]['license_plate'];

    var info = type + " " + vendor + " " + model + " с рег. номером " + licensePlate;

    if (confirm("Вы уверены, что хотите принять " + info + "?")) {

        $.ajax({
            type: 'POST',
            url: getBaseUrl() + '/vehicle/return?' + $.param({
                point_id: getPointIdFromSession(),
                rent_record_id: rentRecordId
            }),
            dataType: 'json',
            success: function (o, status, xhr) {
                if (200 == xhr.status) {
                    alert("Вы успешно приняли " + info);
                    window.location.href = '/rent_return_vehicle';
                } else {
                    alert(o.error);
                }
            },
            error: function (o) {

            }
        });
    }
};

function clearFilterVehicleSelect() {
    $('#type, #vendor').val(-1);
    $('#tbl-vehicles-to-rent').hide();
    $('#tbl-vehicles-to-rent tbody').html('');
}

function clearFilterCustomerSelect() {
    $('#country').val(-1);
    $('#customer').val("");

    $('#tbl-customers').hide();
    $('#tbl-customers tbody').html('');
}

function clearFilterReturn() {
    $('#return-type, #return-vendor').val(-1);
    $('#return-vehicle-name, #return-license-plate, #return-customer').val("");
}
