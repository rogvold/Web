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