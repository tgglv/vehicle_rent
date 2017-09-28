var placeMark;
var map;
var line;

var mapCenterLatitude = null;
var mapCenterLongitude = null;

var selectedVehicleId = null;
var interval = null;

var zoomLevel = 10;

// TODO: раз в 10 секунд запрашиваем текущее положение машины, отрисовываем новую координату на карте

// TODO: вычислять положение цента на основе текущего положения ТС

findVehicleResultButtonCaption = 'Отследить ТС';

var finishVehicleReturn = function (o) {
    selectedVehicleId = $(o).data('vehicleId');

    $('#find-tracking-vehicle-container').hide();

    updateLocation();
    interval = setInterval(updateLocation, 5000);

    $('#track-vehicle-container').show();
};

function returnToVehicleTrackingSelect() {
    $('#track-vehicle-container').hide();

    clearInterval(interval);
    mapCenterLatitude = null;
    mapCenterLongitude = null;

    $('#find-tracking-vehicle-container').show();
}

function updateLocation() {
    $.ajax({
        type: 'GET',
        url: getBaseUrl() + '/tracking?vehicle_id=' + selectedVehicleId,
        dataType: 'json',
        success: function (o, statusText, xhr) {
            if (200 == xhr.status) {

                if (null == mapCenterLatitude && null == mapCenterLongitude) {
                    mapCenterLatitude = o.latitude;
                    mapCenterLongitude = o.longitude;

                    map = new YMaps.Map(document.getElementById("map"));
                    map.setCenter(new YMaps.GeoPoint(o.latitude, o.longitude), zoomLevel);
                    placeMark = new YMaps.Placemark(new YMaps.GeoPoint(o.latitude, o.longitude));

                    map.addOverlay(placeMark);
                    line = new YMaps.Polyline([new YMaps.GeoPoint(o.latitude, o.longitude)], {geodesic: true});
                    map.addOverlay(line);
                } else {
                    if (Math.sqrt(Math.pow(mapCenterLatitude - o.latitude, 2) + Math.pow(mapCenterLongitude - o.longitude, 2)) > 0.0001) {
                        map.setCenter(new YMaps.GeoPoint(o.latitude, o.longitude), zoomLevel);
                        mapCenterLatitude = o.latitude;
                        mapCenterLongitude = o.longitude;
                    }

                    var pt = placeMark.getGeoPoint();
                    pt.setLng(o.latitude);
                    pt.setLat(o.longitude);
                    placeMark.setGeoPoint(pt);
                    line.addPoint(pt);
                    line.update();
                    map.panTo(pt);
                }
            }
        },
        error: function (o) {

        }
    })
}