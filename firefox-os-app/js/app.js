function vehicle_kind(kind_id) {
    switch(kind_id) {
    case 0:
        return {label: "tram", style_class: "vehicle-type-tram"}
    case 1:
        return {label: "trolleybus", style_class: "vehicle-type-trolley"}
    case 2:
        return {label: "bus", style_class: "vehicle-type-bus"}
    case 3:
        return {label: "express", style_class: "vehicle-type-express"}
    case 4:
        return {label: "metro", style_class: "vehicle-type-metro"}
    }
}

function add_new_row(template_row, fill_function, fill_entity) {
    var last = template_row;
    var clone = template_row.clone();

    last.before(clone);
    clone.removeClass("template");
    clone.addClass("non-template");
    fill_function(clone, fill_entity);
    clone.show();
    return clone;
}

function set_line(obj, route) {
    var kind = vehicle_kind(route.vehicle_type);
    $(".vehicle-name", obj).text(route.title);
    if (route.is_barred) {
        $(".vehicle-name", obj).addClass("vehicle-type-barred");
    }
    $(".vehicle-type", obj).text(kind.label);
    $(".vehicle-col, .vehicle-name", obj)
        .removeClass("vehicle-type-tram vehicle-type-trolley vehicle-type-bus vehicle-type-express vehicle-type-metro")
        .addClass(kind.style_class);
    $(".departure-label", obj).text(route.departure);
    $(".destination-label", obj).text(route.destination);
}

function start_loading() {
    $(".table-lines").addClass("hidden");
    $(".table-times").addClass("hidden");
    $(".loading-spinner").removeClass("hidden");
    $(".actions-lines").addClass("hidden");
    $(".actions-times").addClass("hidden");
    $(".navbar-fixed-top .navbar-back-icon").removeClass("hidden");
}

function set_line_row_click(line_row, route) {
    $(line_row).click(function click_line() {
        start_loading();
        $(".actions-times").removeClass("hidden");
        get_route_times(route.route_id, load_times);
    });
}

function load_lines(data) {
    var route, index = 0;
    var template = $(".table-lines .template");
    var line_row;
    template.hide();
    template = template.clone();
    $(".table-lines tbody").empty();
    $(".table-lines tbody").append(template);
    for (index = 0; index < data.routes.length; index++) {
        line_row = add_new_row(template, set_line, data.routes[index]);
        set_line_row_click(line_row, data.routes[index]);
    }
    $(".loading-spinner").addClass("hidden");
    $(".table-lines").removeClass("hidden");
}

function set_station(obj, station) {
    var station_time = $(".station-time", obj);
    $(".station-label", obj).text(station.title);
    station_time.text(station.next_eta);
    station_time.data("station_extid", station.stop_extid);
}

function set_update_times_click(update_btn, data, station_times) {
    $(update_btn).off("click");
    $(update_btn).on("click", function click_update() {
        $.each(station_times, function update_each(index, obj) {
            obj.addClass("station-updating");
            get_station_times(data.route.route_extid, $(obj).data("station_extid"), function complete_update(estimate) {
                obj.text(estimate.eta);
                obj.removeClass("station-updating");
                obj.addClass("station"+index);
            });
        });
    });
}

function load_times(data) {
    var station, index = 0;
    var template = $(".table-times .template");
    var kind = vehicle_kind(data.route.vehicle_type);
    var station_times = [], station_row;
    set_line($(".table-times .table-times-line"), data.route);
    set_station($(".table-times .table-times-line"), data.stops[0]);
    station_times.push($(".table-times .table-times-line .station-time"));

    template.hide();
    template = template.clone();
    $(".table-times tbody").empty();
    $(".table-times tbody").append(template);
    for (index = 1; index < data.stops.length; index++) {
        station_row = add_new_row(template, set_station, data.stops[index]);
        station_times.push($(".station-time", station_row));
    }
    set_update_times_click($(".update-times"), data, station_times);
    $.each(station_times, function style_each(index, obj) {
        obj
            .removeClass("vehicle-type-tram vehicle-type-trolley vehicle-type-bus vehicle-type-express vehicle-type-metro")
            .addClass(kind.style_class);
    });

    $(".loading-spinner").addClass("hidden");
    $(".table-times").removeClass("hidden");
    $(".actions-times").removeClass("hidden");
}

function load_favorites() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    $(".navbar-fixed-top .navbar-back-icon").addClass("hidden");
    get_favorites(load_lines);
}

function load_trams() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trams(load_lines);
}
function load_trolleybuses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trolleybuses(load_lines);
}
function load_busses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_busses(load_lines);
}

function setup_control() {
    load_favorites();
    $(".tram-kinds").click(load_trams)
    $(".trolleybus-kinds").click(load_trolleybuses)
    $(".bus-kinds").click(load_busses)
    $(".navbar-fixed-top .navbar-brand, .navbar-fixed-top .navbar-brand-icon, .navbar-fixed-top .navbar-back-icon").click(load_favorites);
}

$(setup_control)
