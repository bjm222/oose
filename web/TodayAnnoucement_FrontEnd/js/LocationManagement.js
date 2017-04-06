$(document).ready(function() {

    var LocationList;
    $.ajax({
        url: API + 'location/list', // url where to submit the request
        async: false,
        type: "GET", // type of action POST || GET
        dataType: 'json', // data type
        contentType: 'application/json; charset=utf-8',
        
        success: function(result) {
            LocationList = JSON.stringify(result);

        },
        error: function(xhr, resp, text) {
            console.log(xhr.responseText);
            var res = JSON.parse(xhr.responseText);
            sweetAlert("Oops...", res.error.message, "error");
        }
    });
    var curLoc = '<ul class=\"list-group\" id=\"curLoc\">';
    $.each(JSON.parse(LocationList), function(i, item) {

        var locID = item.id;
        console.log(item);
        curLoc +=
            '<li class=\"list-group-item list-group-item-info\" id=\"' + locID + '\" >' +
            item.name + '</li>' + '<br/>';

    });
    curLoc += '</ul>'
    document.getElementById("AllLocations").innerHTML = curLoc;


    map = new GMaps({
        el: "#map",
        zoom: 16,
        lat: 39.33000290930883,
        lng: -76.62053525447845
    });


    GMaps.geolocate({
        success: function(position) {
            map.setCenter(position.coords.latitude, position.coords.longitude);
        },
        error: function(error) {
            alert('Geolocation failed: ' + error.message);
        },
        not_supported: function() {
            alert("Your browser does not support geolocation");
        },

    });

    map.addControl({
        position: 'top_right',
        content: 'Geolocate',
        style: {
            margin: '5px',
            padding: '1px 6px',
            border: 'solid 1px #717B87',
            background: '#fff'
        },
        events: {
            click: function() {
                GMaps.geolocate({
                    success: function(position) {
                        map.setCenter(position.coords.latitude, position.coords.longitude);
                    },
                    error: function(error) {
                        alert('Geolocation failed: ' + error.message);
                    },
                    not_supported: function() {
                        alert("Your browser does not support geolocation");
                    }
                });
            }
        }
    });

    map.setContextMenu({
        control: 'map',
        options: [{
            title: 'Add marker',
            name: 'add_marker',
            action: function(e) {
                console.log(e.latLng.lat());
                console.log(e.latLng.lng());
                $("#Longitude").val(e.latLng.lng());
                $("#Latitude").val(e.latLng.lat());
                this.addMarker({
                    lat: e.latLng.lat(),
                    lng: e.latLng.lng(),
                    title: 'New marker'
                });
                this.hideContextMenu();
            }
        }, {
            title: 'Center here',
            name: 'center_here',
            action: function(e) {
                this.setCenter(e.latLng.lat(), e.latLng.lng());
            }
        }]
    });
    map.setContextMenu({
        control: 'marker',
        options: [{
            title: 'Center here',
            name: 'center_here',
            action: function(e) {
                this.setCenter(e.latLng.lat(), e.latLng.lng());
            }
        }]
    });

    $('#geocoding_form').submit(function(e) {
        e.preventDefault();

        GMaps.geocode({
            address: $('#address').val().trim(),
            callback: function(results, status) {
                if (status == 'OK') {
                    var latlng = results[0].geometry.location;
                    $("#Longitude").val(latlng.lng());
                    $("#Latitude").val(latlng.lat());
                    map.setCenter(latlng.lat(), latlng.lng());
                    map.addMarker({
                        lat: latlng.lat(),
                        lng: latlng.lng()
                    });

                }
            }
        });

    });
    /**
     * Hide all location details when first loading the page
     */
    var locDetails = $('#locDetails');
    $.each(JSON.parse(LocationList), function(i, item) {
        locDetails.append('<div class="details hide" id=' + item.id + '>\
    <h3><I><u><STRONG>Location Details:</STRONG></u></I></h3>\
    <h4><I><STRONG>Latitude: </STRONG></I>' + item.lat + '</h4>\
    <h4><I><STRONG>Longitude: </STRONG></I>' + item.lng + '</h4>\
    </div>');
    });

    var click_id = 0;
    /**
     * show location details
     */
    $('.list-group-item.list-group-item-info').click(function() {
        //alert($(this).attr("id"));
        var id = $(this).attr("id");
        click_id = id;
        $('.details').each(function() {
            if (!$(this).hasClass('hide')) {
                $(this).addClass('hide');
            }

            var detail_id = $(this).attr("id");
            if (id == detail_id) {
                if ($(this).hasClass('hide')) {
                    $(this).removeClass('hide');
                }

            }
        });
    });

    var LocationInfo = {};
    $("#AddLocationBtn").on('click', function() {
        //console.log("hello");

        LocationInfo['name'] = $('#LocationName').val();
        LocationInfo['lng'] = $('#Longitude').val();
        LocationInfo['lat'] = $('#Latitude').val();
        //LocationInfo['LocationName'] = $('#LocationName').val();
        console.log(LocationInfo);
        console.log(JSON.stringify(LocationInfo));
        console.log($("#LocationInfo_form").serialize());

        $.ajax({
            url: API + 'location/add', // url where to submit the request
            type: "POST", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(LocationInfo), // post data || get data
             headers: {'token': localStorage.getItem('token')},
            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log(result);
                location.reload();
            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
            }
        });
    });


    /**
     * Click on delete
     */
    $('#DeleteBtn').click(function() {
        $('.list-group-item.list-group-item-info').each(function() {
            if (click_id == $(this).attr("id")) {
                $(this).remove();
            }
        });

        $('.details').each(function() {
            if (click_id == $(this).attr("id")) {
                $(this).remove();
            }
        });
        $.ajax({
            url: API + 'location/delete/' + click_id, // url where to submit the request
            type: "DELETE", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            headers: {'token': localStorage.getItem('token')},
            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log(result);
                location.reload();
            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
            }
        });

    });

     $('#locationpageback').click(function() {
        location.href = "AdminManage.html"; 
    });


});