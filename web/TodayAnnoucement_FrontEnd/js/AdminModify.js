$(document).ready(function() {
    console.log(localStorage.clickID);
    /**
     * Load the location list
     */
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
            console.log(xhr, resp, text);
            sweetAlert("Oops...", "Internal error, try again!", "error");
        }
    });
    var locID;
    var locationList = '<option></option>';
    $.each(JSON.parse(LocationList), function(i, item) {
        console.log(item.name.toString());
        console.log(item.id);
        locID = item.id;
        locationList +=
            '<option class=\"locationId\" id=\"' + locID + '\">' + item.name + '</option>';
    });

    document.getElementById("location").innerHTML = locationList;
    /**
     * Load the location list end
     */

    /**
     * Initialize map
     */
    jQuery.fn.initializeMap = function() {
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
                        $("#Longtitude").val(e.latLng.lng());
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
        }
        /**
         * Load map ended
         */

    jQuery.fn.displayMap = function() {
        document.getElementById('map').style.display = "block";
        $('#my_div').initializeMap();
    }

    //$('#summernote').summernote();
    $('#summernote').summernote({
        toolbar: [
            // [groupName, [list of button]]
            ['style', ['style']],
            ['Fontstyle', ['bold', 'italic', 'underline', 'strikethrough',
                'superscript', 'subscript', 'clear'
            ]],
            ['fontsize', ['fontsize', 'fontname']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']],
            ['insert', ['table']],
            ['Misc', ['fullscreen', 'codeview', 'undo', 'redo', 'help']]
        ]
    });
    var click_id = localStorage.clickID;
    /**
     * Get the clicked announcement
     */
    var AnnouncementTemp;
    $.ajax({
        url: API + 'announcement/' + click_id, // url where to submit the request
        async: false,
        type: "GET", // type of action POST || GET
        dataType: 'json', // data type
        contentType: 'application/json; charset=utf-8',
        success: function(result) {
            AnnouncementTemp = JSON.stringify(result);
        },
        error: function(xhr, resp, text) {
            console.log(xhr, resp, text);
            sweetAlert("Oops...", "Internal error, try again!", "error");
        }
    });
    Announcement = JSON.parse(AnnouncementTemp);
    console.log(Announcement);
    $("#title").val(Announcement.title);
    $("#date").val(Announcement.pushDate);
    $("#summary").val(Announcement.summary);
    $("#hostFirstName").val(Announcement.hostFirstName);
    $("#hostLastName").val(Announcement.hostLastName);
    $("#hostOrganization").val(Announcement.hostOrganization);
    $("#phone").val(Announcement.hostPhone);



    $('#summernote').summernote('code', Announcement.detail);
    if (Announcement.isEvent) {
        $("#evntinfo").show();
        $("#isEvent").val("Yes");

        if (Announcement.foodProvided) {
            $("#foodProvided").val("Yes");
        }
        var eventTime = Announcement.eventTime;
        time = eventTime.substring(eventTime.length - 8, eventTime.length - 3);
        $("#eventTime").val(time);

        if (Announcement.location != null) {
            $("#defaultLoc").show();
            $('#map').hide();
            $("#CustomerLocation").hide();
            $.each(JSON.parse(LocationList), function(i, item) {
                var locID = item.id;
                if (locID == Announcement.location) {
                    $("#location").val(item.name);
                }
            });
        } else {
            $('#my_div').displayMap();
            $("#CustomerLocation").show();
            $("#defaultLoc").hide();
            $("#locationName").val(Announcement.locationName);
            $("#locationDesc").val(Announcement.locationDesc);
            $("#Longtitude").val(Announcement.locationLng);
            $("#Latitude").val(Announcement.locationLat);
        }
    }

    $("#date").datepicker({
        //showOn: both - datepicker will come clicking the input box as well as the calendar icon
        //showOn: button - datepicker will come only clicking the calendar icon

        changeMonth: true,
        changeYear: true,
        showAnim: 'slideDown',
        duration: 'fast',
        dateFormat: 'yy-mm-dd',
        beforeShow: function() {
            $(".ui-datepicker").css('font-size', 18)
        }
    });
    $("#customerLoc").click(function() {
        $('#location').val('');
        $('#my_div').displayMap();
        $("#CustomerLocation").show();
        $("#defaultLoc").hide();
    });
    $("#defaultLocBtn").click(function() {
        $('#locationName').val('');
        $('#locationDesc').val('');
        $('#Latitude').val('');
        $('#Longtitude').val('');
        $("#defaultLoc").show();
        $('#map').hide();
        $("#CustomerLocation").hide();
    });
    /**
     * submit the modified information
     */
    var submitArray = {};
    
    var accessCode;
    $("#submitbtn").on('click', function() {

        //For get the plain text in the rich text editor "SummerNote"
        var code = $('#summernote').summernote('code');
        var text = code.replace(/<p>/gi, " ");
        var plainText = $("<div />").html(text).text();
        console.log(plainText);
        console.log($('#summernote').summernote('code'));
        submitArray['title'] = $('#title').val();
        submitArray['summary'] = $('#summary').val();
        submitArray['pushDate'] = $('#date').val();
        submitArray['detail'] = code;

        if ($('#isEvent').val() == "Yes") {
            submitArray['isEvent'] = true;

            if ($('#foodProvided').val() == "Yes") {
                submitArray['foodProvided'] = true;
            } else {
                submitArray['foodProvided'] = false;
            }
            submitArray['eventTime'] = $('#date').val() + " " + $('#eventTime').val() + ":00";
            if ($('#location').val() != "") {
                //submitArray['location'] = $('#location').val();
                submitArray['location'] = locID;
            } else {
                if ($('#locationName').val() != "") {
                    submitArray['locationName'] = $('#locationName').val();
                }
                if ($('#locationDesc').val() != "") {
                    submitArray['locationDesc'] = $('#locationDesc').val();
                }
                if ($('#Latitude').val() != "") {
                    submitArray['locationLat'] = $('#Latitude').val();
                }
                if ($('#Longtitude').val() != "") {
                    submitArray['locationLng'] = $('#Longtitude').val();
                }
            }
        } else {
            submitArray['isEvent'] = false;
        }
        submitArray['hostFirstName'] = $('#hostFirstName').val();
        if ($('#hostLastName').val() != "") {
            submitArray['hostLastName'] = $('#hostLastName').val();
        }
        if ($('#hostOrganization').val() != "") {
            submitArray['hostOrganization'] = $('#hostOrganization').val();
        }

        submitArray['hostEmail'] = $('#email').val();

        if ($('#phone').val() != "") {
            submitArray['hostPhone'] = $('#phone').val();
        }
        console.log(JSON.stringify(submitArray));

        $.ajax({
            url: API + 'announcement/manage/modify/' + click_id, // url where to submit the request
            async: false,
            type: "PUT", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(submitArray), // post data || get data
            headers: {'token': localStorage.getItem('token')},
            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log("hello");
                console.log(result);
                if(localStorage.isAdmin=="true"){
                    location.href = "AdminManage.html";
                } else {
                    location.href = "ApplicantManage.html";
                }
                

            },
            error: function(xhr, resp, text) {
                console.log(xhr, resp, text);
                sweetAlert("Oops...", "Something went wrong!", "error");
            }
        });

    });

    function getId(url) {
        var regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
        var match = url.match(regExp);

        if (match && match[2].length == 11) {
            return match[2];
        } else {
            return 'error';
        }
    }

    //var myId = getId('https://www.youtube.com/watch?v=omobhuutrE4&list=RDUk_UG1zjvpU&index=27');


    $("#videoBtn").on('click', function() {
        console.log($("#video").val());
        var myId = getId($("#video").val());
        var myCode = '<iframe width="560" height="315" src="http://www.youtube.com/embed/' + myId + '" frameborder="0" allowfullscreen></iframe>';
        $('#summernote').summernote('pasteHTML', myCode);
        console.log($('#summernote').summernote('code'));
    });

    $("#imageBtn").on('click', function() {
        console.log($("#imagelink").val());
        var url = $("#imagelink").val();
        //var img = '<img src="' + url + '" width="720">';
        var img = '<img src="' + url + '" style=" max-width: 560px;">';
        $('#summernote').summernote('pasteHTML', img);
        console.log(img);
        console.log($('#summernote').summernote('code'));
    });



    $('#inputimg').change(function(event) {
        var tmppath = URL.createObjectURL(event.target.files[0]);
        $("#img1").fadeIn("fast").attr('src', URL.createObjectURL(event.target.files[0]));

        //$("#disp_tmp_path").html("Temporary Path(Copy it and try pasting it in browser address bar) --> <strong>[" + tmppath + "]</strong>");
    });

    $("#date").datepicker({
        //showOn: both - datepicker will come clicking the input box as well as the calendar icon
        //showOn: button - datepicker will come only clicking the calendar icon

        changeMonth: true,
        changeYear: true,
        showAnim: 'slideDown',
        duration: 'fast',
        dateFormat: 'yy-mm-dd',
        beforeShow: function() {
            $(".ui-datepicker").css('font-size', 18)
        }
    });


});