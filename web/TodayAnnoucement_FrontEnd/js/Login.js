$(document).ready(function() {
  /**
  $.post('http://www.demo.com/sign_in',{username:username,password:password},function(callback){
    if(callback.status){
        location.href='http://www.demo.com'
    }else{
        alert(callback.msg);
    }
},'json')//maybe you haven't append "json" parameter that lead to the ajax error
  */ 
   var submitArray = {};
   $("#LoginBtn").on('click', function(){ 
      submitArray['username'] = $('#form-username').val(); 
      submitArray['password'] = $('#form-password').val();
      
      console.log(JSON.stringify(submitArray));
       
        $.ajax({
                url: API+'user/login', // url where to submit the request
                async: false, 
                type : "POST", // type of action POST || GET
                dataType : 'json', // data type
                contentType:'application/json; charset=utf-8',
                data : JSON.stringify(submitArray),// post data || get data
                
                success : function(result) {
                    // you can see the result from the console
                    // tab of the developer tools
                    console.log(result);
                    console.log(JSON.stringify(result));
                    var res = JSON.parse(JSON.stringify(result));
                    console.log(res.token);
                    localStorage.setItem("token", res.token);
                    location.href = "AdminManage.html";
                    
                },
                error: function(xhr, resp, text) {
                    console.log(xhr.responseText);
                    var res = JSON.parse(xhr.responseText);
                    sweetAlert("Oops...", res.error.message, "error");
                }
        });
   });
    

});






