
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
add_onclick("install_app", install_app);


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

function set_line_station(obj, station, route) {
    set_line(obj, route);
    set_station(obj, station, route);
}


function load_times(data) {
    var index = 0;
    var station_template, line_template;

    line_template = prepare_template(
        $(".table-times thead"),
        $(".table-times thead .template"));
    add_new_row(line_template, set_line_station, data.stops[0], data.route);

    station_template = prepare_template(
        $(".table-times tbody"),
        $(".table-times tbody .template"));
    for (index = 1; index < data.stops.length; index++) {
        add_new_row(station_template, set_station, data.stops[index], data.route);
    }
    $(".loading-spinner").addClass("hidden");
    $(".table-times").removeClass("hidden");
    $(".actions-times").removeClass("hidden");
    update_station_times();
}

function line_row_onclick(route_id) {
    get_route_times(route_id, load_times);
}
add_onclick("line_row_onclick", line_row_onclick);


function setup_control() {
    load_favorites();
    setup_onclick();
    maybe_show_install_app();
}

$(setup_control);
