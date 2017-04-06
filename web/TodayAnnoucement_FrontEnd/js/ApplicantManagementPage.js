$(document).ready(function() {
    console.log(localStorage.token);

    var manageID = localStorage.managerID;
    var AnnouncementsTemp;
    $.ajax({
        url: API +'announcement/manage', // url where to submit the request
        async: false,
        type: "GET", // type of action POST || GET
        dataType: 'json', // data type
        contentType: 'application/json; charset=utf-8',
        headers: {'token': localStorage.getItem('token')},
        success: function(result) {
            AnnouncementsTemp = JSON.stringify(result);


        },
        error: function(xhr, resp, text) {
            console.log(xhr.responseText);
            var res = JSON.parse(xhr.responseText);
            sweetAlert("Oops...", res.error.message, "error");
        }
    });
    Announcements = JSON.parse(AnnouncementsTemp);
    //console.log(Announcements);
    var click_id = -1;
    var Unapproved = '<ul class=\"list-group\" id=\"Unapproved\">';
    var Approved = '<ul class=\"list-group\" id=\"Approved\">';
    var Declined = '<ul class=\"list-group\" id=\"Declined\">';
    $.each(Announcements, function(i, item) {
        
        var AID = item.id;
        if (item.status == "Unapproved") {
            Unapproved +=
                '<li class=\"list-group-item list-group-item-warning\" id=\"' + AID + '\" >' +
                item.title + '</li>' ;
        }
        if (item.status == "Approved") {
            Approved +=
                '<li class=\"list-group-item list-group-item-info\" id=\"' + AID + '\" >' +
                item.title + '</li>' ;
        }
        if (item.status == "Declined") {
            Declined +=
                '<li class=\"list-group-item list-group-item-danger\" id=\"' + AID + '\" >' +
                item.title + '</li>' ;
        }

    });
    Unapproved += '</ul>';
    Approved += '</ul>';
    Declined += '</ul>';
    document.getElementById("Unapproved").innerHTML = Unapproved;
    document.getElementById("Approved").innerHTML = Approved;
    document.getElementById("Declined").innerHTML = Declined;

    /**
     * Hide all the details when first loading the page
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
    LocationList = JSON.parse(LocationList);
    console.log(LocationList);
    var locationmap = new Object(); 
    $.each(LocationList, function(i, item) { 
        locationmap[item.id] = item.name;
    
    });
    
   console.log(Announcements);
   console.log(locationmap["3"]);
    var showDetails = '';
    $.each(Announcements, function(i, item) { 
        showDetails += '<div class="details hide" id=' + item.id + '>\
    <h4 style=\"color:Navy\">Summary:</h4>\
    <h4>' + item.summary + '</h4><div style="line-height:60%;"><br></div>\
    <h4 style=\"color:Navy\">Date:</h4>\
    <h4>' + item.pushDate + '</h4><div style="line-height:60%;"><br></div>';
        if(item.isEvent == true) {
            showDetails += '<h4 style=\"color:Navy\">Location:</h4>';
            if(item.location == null) {
                showDetails += '<h4>' + item.locationName +'</h4>';
                showDetails += '<h4>Lat: '+item.locationLat+"  "+
                '<span>Lng: </span>'+item.locationLng+'</h4>';
            } else {
                showDetails += '<h4>' + locationmap[item.location] + '</h4>';
                console.log(locationmap[item.location]);

            }
            showDetails += '<div style="line-height:60%;"><br></div><h4 style=\"color:Navy\">Time:</h4>';
            showDetails += '<h4>' + item.eventTime.substring(11) + '</h4>';
        }
        showDetails += '<div style="line-height:60%;"><br></div><h4 style=\"color:Navy\">Contact Information:</h4>\
    <h4>' + item.hostFirstName + '</h4>\
    <h4>' + item.hostEmail + '</h4>\
    <h4>' + item.hostPhone + '</h4>\
    </div>'
    });
    document.getElementById("AnnouncementDetails").innerHTML = showDetails;
    /**
     * Hide all the status when first loading the page
     */
    var showStatus = $('#status');
    $.each(Announcements, function(i, item) {
        if (item.status == "Unapproved") {
             showStatus.append('<div class="details hide" id=' + item.id + '>\
    <h3 style=\"color:#9F9F5F\"><I>' + item.status + '</I></h3></div>');
        }
        else if (item.status == "Approved") {
             showStatus.append('<div class="details hide" id=' + item.id + '>\
    <h3 style=\"color:#32CD99\"><I>' + item.status + '</I></h3></div>');
        }
        else {
             showStatus.append('<div class="details hide" id=' + item.id + '>\
    <h3 style=\"color:#BC8F8F\"><I>' + item.status + '</I></h3></div>');
        }
       
    });



    /**
     * Click on each announcement to show details
     */
    $('.list-group-item.list-group-item-warning').click(function() {
        var id = $(this).attr("id");
        click_id = id;
        console.log(click_id);
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

    $('.list-group-item.list-group-item-info').click(function() {
        var id = $(this).attr("id");
        click_id = id;
        console.log(click_id);
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

    $('.list-group-item.list-group-item-danger').click(function() {
        var id = $(this).attr("id");
        click_id = id;
        console.log(click_id);
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


    /**
     * Click on delete
     */
    $('#DeleteBtn').click(function() {
        if (click_id == -1){

            sweetAlert("Oops...", "Please select an announcement", "error");
        } else {
            $.ajax({
                url: API+'announcement/manage/delete/'+click_id, // url where to submit the request
                type : "DELETE", // type of action POST || GET
                headers: {'token': localStorage.getItem('token')},
                success : function(result) {
                   console.log(result);
                   location.reload();
                },
                error: function(xhr, resp, text) {
                    console.log(xhr.responseText);
                    var res = JSON.parse(xhr.responseText);
                    sweetAlert("Oops...", res.error.message, "error");
                }
            });
        }
        
    });
    /**
     * Click on Modify button
     */
    $('#ModifyBtn').click(function() {
        if (click_id == -1){

            sweetAlert("Oops...", "Please select an announcement", "error");
        } else {
        localStorage.setItem("clickID", click_id);
        localStorage.setItem("token", localStorage.getItem('token'));
        localStorage.setItem("isAdmin", false);
        location.href = "AdminModify.html";
    }
    });
    /**
     * Click on history go button
     */
    $('#historyBtn').click(function() {
        localStorage.setItem("startDate", $('#startDate').val());
        localStorage.setItem("endDate", $('#endDate').val());
        localStorage.setItem("token", localStorage.getItem('token'));
        localStorage.setItem("isAdmin", false);
        location.href = "History.html";
    });
    
    $('#manageback').click(function() {
        
        localStorage.setItem("isAdmin", false);
        location.href = "Submit.html";
    });
    
    /**
     * Click on view details button
     */
    $('#viewDetails').click(function() {  
       console.log(click_id); 
       if (click_id == -1){
            sweetAlert("Oops...", "Please select an announcement", "error");
        } else {
            $.ajax({
          url: API +'announcement/'+click_id, // url where to submit the request
          type: "GET", // type of action POST || GET
          dataType: 'json', // data type
          contentType: 'application/json; charset=utf-8',
          success: function(result) {
            var details = JSON.stringify(result);
            console.log(result.detail);
            document.getElementById("AnnouncementDetailsModal").innerHTML 
            =result.detail;
          },
          error: function(xhr, resp, text) {
            console.log(xhr, resp, text);
            sweetAlert("Oops...", "Please select an announcement!", "error");
          }
        });
        }
       
    });

    /**
     * click to choose a start date for viewing the announcement history
     */  
    $("#startDate").datepicker({
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
     * click to choose a end date for viewing the announcement history
     */  
    $("#endDate").datepicker({
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