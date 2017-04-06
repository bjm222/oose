$(document).ready(function() {
    /**
     * get all the default location list from database
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
            console.log(xhr.responseText);
            var res = JSON.parse(xhr.responseText);
            sweetAlert("Oops...", res.error.message, "error");
            
        }
    });
    var locationList = '<option></option>';
    $.each(JSON.parse(LocationList), function(i, item) {
        console.log(item.name.toString());
        console.log(item.id);
        var locID = item.id;
        locationList +=
            '<option class=\"locationId\" id=\"' + locID + '\">' + item.name + '</option>';
    });

    document.getElementById("location").innerHTML = locationList;

    /**
     * Load google map and add control
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


            /**
             * Load map ended
             */
        }
    /**
     * A rich text editor
     */
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


    jQuery.fn.displayMap = function() {
        document.getElementById('map').style.display = "block";
        $('#my_div').initializeMap();
    }
    
    /**
     * customize a location for an event
     */
    $("#customerLoc").click(function() {
        $('#my_div').displayMap();
        $("#CustomerLocation").show();
        $("#defaultLoc").hide();
    });
    /**
     * Choose a default location for the event
     */
    $("#defaultLocBtn").click(function() {
        $("#defaultLoc").show();
        $('#map').hide();
        $("#CustomerLocation").hide();
    });

 


    /**
     * Get the default location ID
     */
    var locID;
    $("#location").change(function() {
        locID = $(this).children(":selected").attr("id");
        console.log(locID);
    });
    $("#isEvent").change(function() {
        if ($("#isEvent").val() == "Yes") {
            $("#evntinfo").show();
            $("#defaultLoc").show();
        } else {
            $("#evntinfo").hide();
            document.getElementById('map').style.display = "none";
        }
    });
    /**
     * Post the Announcement/Event information
     */
    var submitArray = {};
    var finalArray = {};
    var accessCode;
    //var timestamp = Date.parse(new Date());
    //$("#myform").submit(function() {
    $("#submitbtn").on('click', function() {
        
        //For get the plain text in the rich text editor "SummerNote"
        //var meta = "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body>";
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
            submitArray['isEvent'] = 'true';

            if ($('#foodProvided').val() == "Yes") {
                submitArray['foodProvided'] = 'true';
            } else {
                submitArray['foodProvided'] = 'false';
            }
            var eventTime1 = $('#eventTime').val();
        if (eventTime1 == "12:00 AM") {
          eventTime1 = "00:00";
        } else if (eventTime1 == "12:30 AM") {
          eventTime1 = "00:30";
        } else {
          if (eventTime1.substring(6,8)=="AM"){
            eventTime1 = eventTime1.substring(0,5);
          } else {
            eventTime1 = (parseInt(eventTime1.substring(0,2))+12)+eventTime1.substring(2,5);
          }
        }
        console.log(eventTime1);
            submitArray['eventTime'] = $('#date').val() + " " + eventTime1 + ":00";
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
            submitArray['isEvent'] = 'false';
        }

        //submitArray['details'] = plainText;

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


        //finalArray[timestamp] = submitArray;
        //console.log(JSON.stringify(finalArray));
        console.log(JSON.stringify(submitArray));

        $.ajax({
            url: API + 'announcement/submit', // url where to submit the request
            async: false,
            type: "POST", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(submitArray), // post data || get data

            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log(result);
                accessCode = JSON.stringify(result);
                swal("Successful!", "We will check your announcement ASAP!", "success");
            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
            }
        });
        console.log(accessCode);
        return false;
    });
    /**
     * Post the access code to go to the management page for the users
     */
    var accessArray = {};
    $("#applicantManageBtn").on('click', function() { 
        accessArray['email'] = $('#applicantEmail').val();
        accessArray['accessCode'] = $('#AccessCode').val();
        console.log(accessArray);
        $.ajax({
            url: API + 'user/access', // url where to submit the request
            async: false,
            type: "POST", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(accessArray), // post data || get data

            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log(result);
                console.log(JSON.stringify(result));
                var res = JSON.parse(JSON.stringify(result));
                console.log(res.token);
                localStorage.setItem("token", res.token);
                location.href = "ApplicantManage.html";
                
            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
            }
        });

    });
    var retrieveEmail = {};
    $("#retrieveCode").on('click', function() {
        retrieveEmail['email'] = $('#applicantEmail').val();
        console.log(retrieveEmail);
        $.ajax({
            url: API + 'user/retrievecode', // url where to submit the request
            async: false,
            type: "POST", // type of action POST || GET
            dataType: 'json', // data type
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(retrieveEmail), // post data || get data

            success: function(result) {
                // you can see the result from the console
                // tab of the developer tools
                console.log(result);
                swal("Ok!", "We will send a new access code to your email ASAP!", "success");
                
            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
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

   
    /**
     * Insert a youtube link
     */

    $("#videoBtn").on('click', function() {
        console.log($("#video").val());
        var myId = getId($("#video").val());
        var myCode = '<iframe width="560" height="315" src="http://www.youtube.com/embed/' + myId + '" frameborder="0" allowfullscreen></iframe>';
        $('#summernote').summernote('pasteHTML', myCode);
        console.log($('#summernote').summernote('code'));
    });
    
    /**
     * Insert a image link
     */
    $("#imageBtn").on('click', function() {
        console.log($("#imagelink").val());
        var url = $("#imagelink").val();
        //var img = '<img src="' + url + '" width="720">';
        var img = '<img src="' + url + '" style=" max-width: 560px;">';
        $('#summernote').summernote('pasteHTML', img);
        console.log(img);
        console.log($('#summernote').summernote('code'));
    });
    
    /**
     * Choose a date for the event
     */
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
    
    /**
     * Choose a time for the event
     */
    $('#eventTime').timepicker({
       format: 'HH:mm'
    });

    var accessKeyid = "";
    var secretKey = "";
    var bucketName = "";
    var key = "";

    /**
     * Insert a image link
     */
    $('#inputimg').change(function(event) {
        var tmppath = URL.createObjectURL(event.target.files[0]);
        $("#img1").fadeIn("fast").attr('src', URL.createObjectURL(event.target.files[0]));
    });


    $("#localimageBtn").on('click', function() {
        // Get S3 access if local dont have it
        if(accessKeyid == "" || secretKey == ""|| bucketName == "" || key == "") {
            try{
                getS3Credential();
            } catch(e) {
                console.log(e);
                sweetAlert("Oops...", "Permission denied, try again!", "error");
            }
        }

        // If you are here you must have S3 Credentials for upload, proceed
        try {
            fileUpload();
        } catch(e) {
            console.log(e);
        }

    });

    /**
     * Get se3 credential
     */
    function getS3Credential(){
        $.ajax({
            url: API+'upload/info', // url where to submit the request
            async: false, 
            type : "GET", // type of action POST || GET
            dataType : 'json', // data type
            contentType:'application/json; charset=utf-8',
              
            headers: {'token': localStorage.getItem('managerID')},
            success : function(result) {

                try {
                    accessKeyid = result['accessKeyId'];
                    secretKey = result['secretAccessKey'];
                    bucketName = result['bucket'];
                    key = result['key'];
                }
                catch(e) {
                    console.log("bad responce\n");
                }

            },
            error: function(xhr, resp, text) {
                console.log(xhr.responseText);
                var res = JSON.parse(xhr.responseText);
                sweetAlert("Oops...", res.error.message, "error");
            }
        });
    } 

    /**
     * upload a local image to S3
     */

    function fileUpload() {

        var s3 = new AWS.S3({
            apiVersion:'2006-03-01', 
            accessKeyId: accessKeyid, 
            secretAccessKey: secretKey
        });

        var UPLOAD_PREFIX = key;

        var files_upload = document.getElementById('inputimg').files[0];

        var params = {
            Bucket: bucketName,
            Key: UPLOAD_PREFIX + '_' + files_upload.name,
            Body: files_upload
        };

       s3.upload(params, function(err, data) {
            if(err) {
                console.log(err);
            }
            if(data) {
                console.log(data);
                $('#summernote').summernote('insertImage', 'https://s3.amazonaws.com/' + bucketName + '/' + UPLOAD_PREFIX + '_' + files_upload.name,
                function ($image) {
                 $image.css('width', 560);
                 
            } );
            }
        });
    }


});