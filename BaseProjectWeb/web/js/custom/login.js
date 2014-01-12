function showError(message){
    $.pnotify({
        text: message, 
        type: 'error', 
        fixed: true
    });
}
            
function showInfo(message){
    $.pnotify({
        text: message, 
        type: 'info', 
        fixed: true
    });
}
           
function PELoginCallback(){
    window.location.href = "/BaseProjectWeb/faces/approbation/profile.xhtml";
}           
function loginCallback(){
    document.location.href = "/BaseProjectWeb/faces/index.xhtml";
//    document.location.reload(true);
}
           
function LoginViewModel(){
    self = this;
    self.email = ko.observable();
    self.password = ko.observable();
    
    this.firstName = ko.observable();
    this.lastName = ko.observable();
    this.phone = ko.observable();
    
    
    
    self.logIn = function(callback){
        $.ajax({
            type: "POST"
            ,
            url: "/BaseProjectWeb/resources/SecureAuth/create_session"
            ,
            data: "email="+self.email()+"&password="+self.password()
            ,
            success: function(data){
                d = data;
                if (data.responseCode == 0){
                    showError(data.error.message);
                }else{
                    if (data.data == 1){
                        showInfo('correct');
                        loginCallback();
//                        document.location.reload(true);
                    }else{
                        showError('incorrect pair email/password');
                    }
                };
            }
        });
    };
    self.register = function(){
        if ($('#p2').val() != $("#c2").val()){
            return;
        }
        $.ajax({
            type: "POST"
            ,
            url: "/BaseProjectWeb/resources/SecureAuth/create_user"
            ,
            data: "email="+self.email() + "&password="+self.password()
            ,
            success: function(data){
                d = data;
                if (data.responseCode == 0){
                    showError(data.error.message);
                }else{
                    if (data.data == 1){
                        self.logIn(loginCallback);
                    }else{
                        showError("can not register user with email '"+ self.email() +"'");
                    }
                };
            }
        });
    };
    
    self.registerPE = function(){
        if ($('#p2').val() != $("#c2").val()){
            return;
        }
        $.ajax({
            type: "POST"
            ,
            url: "/BaseProjectWeb/resources/SecureAuth/create_user"
            ,
            data: "email="+self.email() + "&password="+self.password()
            ,
            success: function(data){
                d = data;
                if (data.responseCode == 0){
                    showError(data.error.message);
                }else{
                    if (data.data == 1){
                        self.logIn(PELoginCallback);
                    }else{
                        showError("can not register user with email '"+ self.email() +"'");
                    }
                };
            }
        });
    };
    
    
}
            
function initLoginForm(){
    var rul = {
        password: {
            required: true,
            minlength: 1
        },
        confirm_password: {
            required: true,
            minlength: 2,
            equalTo: "#p2"
        },
        email: {
            required: true,
            email: true
        }
    };

                
    var mes = {
        password: {
            required: "Please provide a password",
            minlength: "Your password must be at least 1 character long"
        },
        confirm_password: {
            required: "Please provide a password",
            minlength: "Your password must be at least 5 characters long",
            equalTo: "Please enter the same password as above"
        },
        email: "Please enter a valid email address"
    }
                
    $("#loginForm").validate({
        rules: rul, 
        messages: mes
    });
    $("#registerForm").validate({
        rules: rul,
        messages: mes
    });
    ko.applyBindings(new LoginViewModel(), document.getElementById('authorizationDiv'));
}