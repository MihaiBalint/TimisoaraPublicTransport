
function get_line_list(route_type, callback) {
    var fields = ["route_id", "route_extid", "title", "vehicle_type", "is_barred", "departure", "destination"];
    var city = "1";
    var loc_split = window.location.href.split("/");
    var origin = loc_split[0]+'//'+loc_split[2];
    $.ajax({
        type: 'GET',
        url: origin+"/rest/v1/cities/"+city+"/"+route_type+"/routes",
        data: {"fields": fields.join(",")},
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            callback(data);
        }
    });
}

function get_favorites(callback) {
    get_line_list("favorite", callback);
}

function get_trams(callback) {
    get_line_list("tram", callback);
}

function get_trolleybuses(callback) {
    get_line_list("trolleybus", callback);
}

function get_busses(callback) {
    get_line_list("any_bus", callback);
}

function get_route_times(route_id, callback) {
    // Called when showing times view after clicking on a line
    var route_fields = ["route_id", "route_extid", "title", "vehicle_type", "is_barred", "departure", "destination"];
    // "stop_id", "title", "stop_extid", "next_eta"
    var stop_fields = ["stop_id", "stop_extid", "title", "next_eta"];

    var resulting_data = {};
    var loc_split = window.location.href.split("/");
    var origin = loc_split[0]+'//'+loc_split[2];
    $.ajax({
        type: 'GET',
        url: origin+"/rest/v1/routes/"+route_id,
        data: {"fields": route_fields.join(",")},
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            if (data.status == "success") {
                resulting_data.route = data.routes[0];
                if (resulting_data.stops) {
                    callback(resulting_data);
                }
            }
        }
    });

    $.ajax({
        type: 'GET',
        url: origin+"/rest/v1/routes/"+route_id+"/stops",
        data: {"fields": stop_fields.join(",")},
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            if (data.status == "success") {
                resulting_data.stops = data.stops;
                if (resulting_data.route) {
                    callback(resulting_data);
                }
            }
        }
    });

}

function get_station_times(route_extid, station_extid, callback) {
    // Called when clicking update button in times view
    // Called once for each stop
    var loc_split = window.location.href.split("/");
    var origin = loc_split[0]+'//'+loc_split[2];
    var url = origin+"/rest/v1/eta/"+route_extid+"/"+station_extid;
    console.log(url);
    $.ajax({
        type: 'GET',
        url: url,
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            callback(data);
        },
        error: function() {
            callback({eta: "Conection error."});
        }
    });
}