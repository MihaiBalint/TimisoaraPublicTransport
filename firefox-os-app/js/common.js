/* Framework parts */
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

function prepare_template(container, template) {
    var template_clone;
    template.hide();
    template_clone = template.clone();
    container.empty();
    container.append(template_clone);
    return template_clone;
}

function add_onclick(docmodel_ref, click_handler) {
    var mvc;
    if (document.mib_mvc) {
        mvc = document.mib_mvc;
    } else {
        mvc = {};
        document.mib_mvc = mvc;
    }
    mvc[docmodel_ref] = click_handler;
}

function find_onclick_handler(docmodel_ref) {
    if (document.mib_mvc) {
        return document.mib_mvc[docmodel_ref];
    } else {
        return null;
    }
}

function setup_onclick() {
    $("[data-onclick]").on("click", function data_onclick() {
        var onclick = find_onclick_handler($(this).data("onclick"));
        if (onclick) {
            onclick(this);
        }
    });
}

function start_loading() {
    // TODO make this generic for all view templates
    $(".table-lines").addClass("hidden");
    $(".table-times").addClass("hidden");
    $(".loading-spinner").removeClass("hidden");
    $(".actions-lines").addClass("hidden");
    $(".actions-times").addClass("hidden");
    $(".navbar-fixed-top .navbar-back-icon").removeClass("hidden");
}



/* Business parts */
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

/* Business view parts */
function set_line(obj, route) {
    var kind = vehicle_kind(route.vehicle_type);
    $(".vehicle-name", obj).text(route.title);
    $(".vehicle-name", obj).toggleClass("vehicle-type-barred", route.is_barred);
    $(".vehicle-type", obj).text(kind.label);
    $(".vehicle-col, .vehicle-name", obj).addClass(kind.style_class);
    $(".departure-label", obj).text(route.departure);
    $(".destination-label", obj).text(route.destination);

    obj.on("click", function click_line() {
        var onclick = find_onclick_handler(obj.data("onclick"));
        if (onclick) {
            onclick(route.route_id);
        }
    });
}

function set_station(obj, station, route) {
    var kind = vehicle_kind(route.vehicle_type);
    var station_time = $(".station-time", obj);
    $(".station-label", obj).text(station.title);
    station_time.text(station.next_eta);
    station_time.data("station_extid", station.stop_extid);
    station_time.data("route_extid", route.route_extid);
    station_time.addClass(kind.style_class);
}

/* Business routes views */
function load_lines(data) {
    var index = 0;
    var template;

    template = prepare_template(
        $(".table-lines tbody"),
        $(".table-lines .template")
    );
    for (index = 0; index < data.routes.length; index++) {
        add_new_row(template, set_line, data.routes[index]);
    }
    $(".loading-spinner").addClass("hidden");
    $(".table-lines").removeClass("hidden");
}

function load_favorites() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    $(".navbar-fixed-top .navbar-back-icon").addClass("hidden");
    get_favorites(load_lines);
    return false;
}
add_onclick("load_favorites", load_favorites);

function load_trams() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trams(load_lines);
    return false;
}
add_onclick("load_trams", load_trams);

function load_trolleybuses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_trolleybuses(load_lines);
    return false;
}
add_onclick("load_trolleybuses", load_trolleybuses);

function load_busses() {
    start_loading();
    $(".actions-lines").removeClass("hidden");
    get_busses(load_lines);
    return false;
}
add_onclick("load_busses", load_busses);

/* Business route stops view */
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
    $(".table-times .populated-template .station-time").each(update_time);
    return false;
}
add_onclick("update_station_times", update_station_times);



