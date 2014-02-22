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

function add_new_row(template_row, fill_function) {
    var last = template_row;
    var clone = template_row.clone();

    last.before(clone);
    clone.removeClass("template");
    clone.addClass("populated-template");
    if (arguments.length === 3) {
        fill_function(clone, arguments[2]);
    } else if (arguments.length === 4) {
        fill_function(clone, arguments[2], arguments[3]);
    }
    clone.show();
    return clone;
}

function set_line(obj, route) {
    var kind = vehicle_kind(route.vehicle_type);
    $(".vehicle-name", obj).text(route.title);
    $(".vehicle-name", obj).toggleClass("vehicle-type-barred", route.is_barred);
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

function set_station(obj, station, route) {
    var kind = vehicle_kind(route.vehicle_type);
    var station_time = $(".station-time", obj);
    $(".station-label", obj).text(station.title);
    station_time.text(station.next_eta);
    station_time.data("station_extid", station.stop_extid);
    station_time.data("route_extid", route.route_extid);
    station_time
        .removeClass("vehicle-type-tram vehicle-type-trolley vehicle-type-bus vehicle-type-express vehicle-type-metro")
        .addClass(kind.style_class);
}

function update_station_times() {
    // TODO disable update button click abuse
    var update_time = function update_time() {
        var obj = $(this);
        var station_extid = obj.data("station_extid");
        var route_extid = obj.data("route_extid");
        obj.addClass("station-updating");
        get_station_times(route_extid, station_extid, function complete_update(estimate) {
            obj.text(estimate.eta);
            obj.removeClass("station-updating");
        });
    };
    $(".table-times .table-times-line .station-time, .table-times tbody .populated-template .station-time").each(update_time);
    return false;
}

function load_times(data) {
    var station, index = 0;
    var template = $(".table-times .template");
    set_line($(".table-times .table-times-line"), data.route);
    set_station($(".table-times .table-times-line"), data.stops[0], data.route);

    template.hide();
    template = template.clone();
    $(".table-times tbody").empty();
    $(".table-times tbody").append(template);
    for (index = 1; index < data.stops.length; index++) {
        add_new_row(template, set_station, data.stops[index], data.route);
    }
    $(".loading-spinner").addClass("hidden");
    $(".table-times").removeClass("hidden");
    $(".actions-times").removeClass("hidden");
    update_station_times();
}

function load_favorites() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    $(".navbar-fixed-top .navbar-back-icon").addClass("hidden");
    get_favorites(load_lines);
    return false;
}

function load_trams() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trams(load_lines);
    return false;
}
function load_trolleybuses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trolleybuses(load_lines);
    return false;
}
function load_busses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_busses(load_lines);
    return false;
}


function install_app() {
    var baseURL = location.href.substring(0, location.href.lastIndexOf("/"));
    var canInstall = !!(navigator.mozApps && navigator.mozApps.install);
    if (canInstall) {
        var req = navigator.mozApps.install(baseURL + '/manifest.webapp');
        req.onsuccess = function() {
            $("#toastModal .modal-body").text("Installation complete.");
            $("#toastModal").modal('show');
        };
        req.onerror = function() {
            $("#toastModal .modal-body").text("Installation failed: "+this.error.name);
            $("#toastModal").modal('show');
        };
    } else {
        $("#toastModal .modal-body").text("Open web apps are not supported on this platform. Try FirefoxOS devices from Mozilla.");
        $("#toastModal").modal('show');
    }
    return false;
}

function maybe_show_install_app() {
    var baseURL = location.href.substring(0, location.href.lastIndexOf("/"));
    var canCheckInstall = !!(navigator.mozApps && navigator.mozApps.checkInstalled);
    var installCheck, appSelf;
    if (canCheckInstall) {
        installCheck = navigator.mozApps.checkInstalled(baseURL + '/manifest.webapp');
        installCheck.onsuccess = function() {
            if (installCheck.result == null) {
                appSelf = navigator.mozApps.getSelf();
                appSelf.onsuccess = function() {
                    if (appSelf.result == null) {
                        $(".install-btn").removeClass("hidden", installCheck.result);
                    }
                };
            };
        };
    }
}

function setup_control() {
    load_favorites();
    $(".tram-kinds").on("click", load_trams);
    $(".trolleybus-kinds").on("click", load_trolleybuses);
    $(".bus-kinds").on("click", load_busses);
    $(".update-times").on("click", update_station_times);
    $(".navbar-fixed-top .navbar-brand, .navbar-fixed-top .navbar-brand-icon, .navbar-fixed-top .navbar-back-icon").on("click", load_favorites);

    $(".install-btn").on("click", install_app);
    maybe_show_install_app();
}

$(setup_control);
