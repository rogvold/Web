function updateSessionDescription(sessionId, descrtiption){
    $.ajax({
        url:'/BaseProjectWeb/resources/internal_sessions/update_session_description?sessionId='+sessionId,
        type: "POST",
        dataType: "json",
        data: {
            description: descrtiption
        },
        success: function(data){
            ded = data;  
            if (data.data == '1'){
                console.log('successfully updated');
            }
        },
        error: function(){
            alert('cannot update session description');
        }
    });
}

function drawSessionDescription(sessionId){
    console.log('drawSessionDescription: sessionId = ' + sessionId);
    $.ajax({
        type: 'GET',
        url: '/BaseProjectWeb/resources/internal_sessions/session_descrtiption?sessionId=' + sessionId,
        success: function(data){
            disablePreloader();

            console.log(data);
            $('#session_description').editable('setValue',(data.data == undefined) ? '' : data.data).editable('option', 'type', 'textarea');
        }
    });
}