function updateStatus(){
    $.ajax({
        type:"GET",
        url: "/BaseProjectWeb/resources/internal_sessions/is_active",
        success: function(data){
            if (data.responseCode == 0){
                console.log(data.error.message);
                return;
            }
            if (data.data == 1){
                if ($('#status_span').text() == 'offline'){
                    window.location.reload();
                }
                $('#status_span').text('online');
                $('#status_span').css('color', 'lightgreen');
            }else{
                $('#status_span').text('offline');
                $('#status_span').css('color', 'lightgray');
            }
        },
        error: function(){
            console.log('cannot load userStatus');
        }
        
    });
}

function updatePulse(){
    $.ajax({
        type:"GET",
        url: "/BaseProjectWeb/resources/internal_sessions/current_pulse",
        success: function(data){
            if (data.responseCode == 0){
                console.log(data.error.message);
                return;
            }
            $('#bpm_span').text(data.data);
        },
        error: function(){
            console.log('cannot load userStatus');
        }
        
    });
}

function updateBeatsAmount(){
    $.ajax({
        type:"GET",
        url: "/BaseProjectWeb/resources/internal_sessions/beats_amount",
        success: function(data){
            if (data.responseCode == 0){
                console.log(data.error.message);
                return;
            }
            $('#heart_beats_amount').text(data.data);
        },
        error: function(){
            console.log('cannot load beats amount');
        }
        
    });
}

function initStatusUpdating(){
    
    updateStatus();
    setInterval(function(){
        updateStatus();
    }, 5000);
}

function initPulseUpdating(){
    
    updatePulse();

    setInterval(function(){
        updatePulse();
    }, 5000);
}

function initBeatsAmountUpdating(){
    updateBeatsAmount();
    setInterval(function(){
        updateBeatsAmount();
    }, 5000);
}


function initOnlineIndicators(){
    initStatusUpdating();
    initPulseUpdating();
    initBeatsAmountUpdating();
}

function enablePreloader(){
    $("#status").delay(10).fadeIn("slow");
    $("#preloader").fadeIn();
}

function disablePreloader(){
    $("#status").fadeOut(); // will first fade out the loading animation
    $("#preloader").delay(100).fadeOut("slow"); // will fade out the white DIV that covers the website.D
}