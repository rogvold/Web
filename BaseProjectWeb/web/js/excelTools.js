function getPrettyDate(tp){
    if (tp == undefined){
        return undefined;
    }
    tp = Number(tp);
    return moment(tp).format('DD.MM.YYYY-HH:mm:ss');
}

function generateTable(rates, dateTimestamp, comment){
    var s1 = ' <table> <tbody> <tr> <td><b>Date: </b></td> <td> ' + getPrettyDate(dateTimestamp) +' </td> </tr> <tr> <td><b>Comment:</b></td> <td> ' + comment + '</td> </tr> </tbody> </table>';
    var s2 = '<table><tbody>';
    for (var i in rates){
        s2 += '<tr><td>' + rates[i] + '</td></tr>';
    }
    s2+='</tbody></table>';
    return s1 + s2;
}

function downloadRates(rates, start, comment, userName){
    var name = getPrettyDate(start);
    if (userName == undefined){
        userName='unknown';
    }
    var a = document.createElement('a');
    var data_type = 'data:application/vnd.ms-excel';
    a.href = data_type + ', ' + generateTable(rates, start, comment);
    a.download = userName + '_' + name + '.xls';
    a.click();
    e.preventDefault();
}

function loadRates(sessionId, start, comment, userName){
    $.ajax({
        type: "GET",
        url: "/BaseProjectWeb/resources/internal_sessions/rates?sessionId="+sessionId,
        success: function(data){
            console.log('rates for session ' + sessionId + ' loaded:');
            console.log(data);
            downloadRates(data.data, start, comment, userName);
        }
    });
};

function initExcelTools(){
    // todo: get user name
    userLabel = $('#lastName').text() +"_" + $('#firstName').text();
    
    $('.download_rates').live('click', function(){
        var id = $(this).attr('data-id');
        var comment = $('#comment_'+id).text();
        var start = $('#timestamp_'+id).attr('data-timestamp');
        console.log('id/comment/start = ' + id + '/'+ comment + '/'+ start);
        loadRates(id, start, comment, userLabel);
    });
    
    //    $('.fixDate').text(getPrettyDate($('#timestamp_'+$(this).attr('data-id')).attr('data-timestamp')));
    $('.fixDate').each(function(){
        var id = $(this).attr('data-id') ;
//        console.log('id = ' + id);
        var tp = $('#timestamp_'+id).attr('data-timestamp');
//        console.log('tp = ' + tp);
        $('#pretty_' + id).text(getPrettyDate(tp));
        
    });
}