
function load_route_stops(data) {
    $(".loading-spinner").addClass("hidden");
    $(".table-times").removeClass("hidden");
    $(".actions-times").removeClass("hidden");
}

function line_row_onclick(route_id) {
    start_loading();
    $(".actions-times").removeClass("hidden");
    get_route_times(route_id, load_route_stops);
}
add_onclick("line_row_onclick", line_row_onclick);

function setup_control() {
    load_favorites();
    setup_onclick();
}

$(setup_control);
