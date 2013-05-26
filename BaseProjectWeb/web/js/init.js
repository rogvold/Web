function getSessionDescription(sessionId){
    $.ajax({
        type: 'GET',
        url: '/BaseProjectWeb/resources/internal_sessions/session_descrtiption?sessionId=' + sessionId,
        success: function(data){
            $('#session_description').setValue(data.data);
        }
    });
}