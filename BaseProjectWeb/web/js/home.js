function loadLastRates(){
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/last_rates",
        data:{
            amount: 30
        },
        type: "POST",
        success: function(data){
            de = data;
            var rates = getCardioPointsFromPullSession(data.data);
            globalPoints = mergePoints(globalPoints, rates);
            var dura = globalPoints[globalPoints.length - 1][0] - globalPoints[0][0];
            console.log(dura);
            if ($('#status_span').text() == 'online'){
                blink('#logoHeart');
            }
            $('#dura').text(getPrettyDuration(dura));
            $('#startDate').text(moment(globalPoints[0][0]).format("dddd, MMMM Do YYYY, h:mm:ss a"));
            drawTrippleGraph(globalPoints, undefined);
            miniPlot = drawPlot('flot', getLastPoints(globalPoints, 500));

        //            plot = drawPlot('chartline', rates, true);
        }
    });
}

function loadLastSessionRates(){
    initBlinking('#blinkingLamp');
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/last_session_rates",
        
        type: "POST",
        success: function(data){
            de = data;
            if (data.data == undefined){
                disablePreloader();
                return;
            }else{
                $('#currentSessionContent').show();
                $('#noDataText').hide();
            }
            globalPoints = getCardioPointsFromPullSession(data.data);
            startDate = undefined;
            if ( (globalPoints != undefined) && (globalPoints.length != 0)){
                startDate = globalPoints[0][0];
            }
            
            drawTrippleGraph(globalPoints, undefined);
            miniPlot = drawPlot('flot', getLastPointsInTimeWindow(globalPoints, 2 * 60 * 1000));
            
            initTag();
            //            drawSessionDescription
            loadLastRates();
            setInterval(function(){
                loadLastRates();
            }, 5000);
            
            disablePreloader();
        }
    });
}

function loadLastEvaluation(){
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/last_evaluation",
        type: "POST",
        data:{
            amount: 30  
        },
        success: function(data){
            de = data;
            console.log('loadLastEvaluation: data');
            console.log(data);
            
            
            var newEvalu = sortEvaluation(data.data);
            globalEvaluation = mergeEvaluation(globalEvaluation, newEvalu);
            if (globalEvaluation != undefined){
                var lastTension = globalEvaluation[globalEvaluation.length - 1].tension;
                
                if (lastTension > 150){
                    $('#stressLamp').show();
                }else{
                    $('#stressLamp').hide();
                }
                
                $('#lastTensionBtn').text(lastTension);
                $('#tensionStatus').css('color', getColorByTension(lastTension));
                $('#tensionStatus').text(getStressComment(lastTension));
                
                
                $('.percentage').each(function() {
                    var newValue = Math.floor(getTensionPercents(lastTension));       
                    $(this).data('easyPieChart').update(newValue);  
                    $('span', this).text(newValue + '%');      
                });
            }
            
            miniTension = drawTension('chartline', getLastPointsInTimeWindow(getTensionPointsFromEvaluation(globalEvaluation), 3 * 60 * 1000), true, 0);
            
            tensionPlot = drawTension('tension', getTensionPointsFromEvaluation(globalEvaluation), true, 0);
            
            $('#stressResume').text(getStressResume(globalEvaluation));
            //            plot = drawPlot('chartline', getTensionPointsFromEvaluation(globalEvaluation), true);

            
            if (globalEvaluation != undefined){
                drawTrippleGraph(undefined, globalEvaluation[globalEvaluation.length - 1]);
            }
        }
    });
}


function loadLastSessionEvaluation(){
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/last_session_evaluation",
        type: "POST",
        success: function(data){
            de = data;
            console.log('loadLastSessionEvaluation:');
            console.log(data.data);
            if (data.data == undefined){
                return;
            }
            
            globalEvaluation = sortEvaluation(data.data);
            tensionPlot = drawTension('tension', getTensionPointsFromEvaluation(globalEvaluation), true, 0);
            if (globalEvaluation != undefined){
                drawTrippleGraph(undefined, globalEvaluation[globalEvaluation.length - 1]);
            }
            loadLastEvaluation();
            setInterval(function(){
                loadLastEvaluation();
            }, 5000);
        }
    });
}

function mergeEvaluation(oldEv, newEv){
    if (newEv == undefined || oldEv == null){
        return;
    }
    if (newEv.length == 0){
        return oldEv;
    }
    
    if (oldEv.length == 0){
        oldEv.push(newEv[0]);
    }
    var l = oldEv[oldEv.length - 1].timestamp;
    var k = 0;
    console.log('lastEvaluation timestamp = ' + l);
    for (var i = newEv.length - 1; i >= 0; i--){
        if (newEv[i].timestamp == l){
            k = i;
            break;
        }
    }
    for(var i = k + 1; i < newEv.length; i++){
        oldEv.push(newEv[i]);
    }
    
    //    console.log('merged evaluation = ');
    //    console.log(oldEv);
    return oldEv;
}


function getTensionPercents(x){
    if (x == undefined) return undefined;
    if (x < 300){
        return 0.3 * x;
    }else{
        return 10 - 1 / ((x - 300)/100 + 0.1 );
    }
}

function getColorByTension(x){
    if (x < 80) return '#0088cc';
    if (x < 150) return '#65cf2c';
    if (x < 300) return '#c66059';
    return '#942313';
}



function mergePoints(oldPoints, newPoints){
    console.log('merging points....');
    console.log(oldPoints.length);
    console.log(' with ' + newPoints.length + ' points');
    
    if (oldPoints == undefined){
        return newPoints;
    }
    if ( newPoints == undefined || newPoints.length == 0){
        return oldPoints;
    }
    if (oldPoints.length == 0){
        oldPoints.push(newPoints[0]);
    }
    
    var l = oldPoints[oldPoints.length - 1][0];
    var k = 0;
    for (var i = newPoints.length - 1; i >= 0; i--){
        if (newPoints[i][0] == l){
            k = i;
            break;
        }
    }
    for(var i = k + 1; i < newPoints.length; i++){
        oldPoints.push(newPoints[i]);
    }
    return oldPoints;
    
}

function initBlinking(selector){
    $(selector).fadeOut('slow', function(){
        $(this).fadeIn('slow', function(){
            initBlinking(this);
        });
    });
}

function blink(selector){
    $(selector).fadeOut('slow');
    $(selector).fadeIn('slow');
}

function getStressfulTimePercent(eval){
    if (eval == undefined){
        return undefined;
    }
    var st = 0;
    for (var i in eval){
        if (eval[i].tension > 150){
            st++;
        }
    }
    return 1.0*st / eval.length;
}

function getPrettyDuration(u){
    var h = (Math.floor(moment.duration(u).asHours()) > 0) ? Math.floor(moment.duration(u).asHours()) + ' h. ' : '';
    var m = (Math.floor(moment.duration(u).asMinutes() % 60) > 0) ? (Math.floor(moment.duration(u).asMinutes() % 60)) + ' min. ' : '';
    var s = (Math.floor(moment.duration(u).asMinutes() % 60) > 0) ? (Math.floor(moment.duration(u).asSeconds() % 60)) + ' sec. ' : '';
    return h + m + s;
}

function getStressResume(eval){
    if (eval == undefined || eval.length ==0){
        return undefined;
    }
    var perc = getStressfulTimePercent(eval);
    var dura = eval[eval.length - 1].timestamp - eval[0]. timestamp;
    var stressDura = Math.floor(perc * dura);
    if (stressDura == 0){
        return 'you were not stressed during measurement';
    }else{
        return 'you were stressed ' + getPrettyDuration(stressDura) + ' during measurement';
    }
}

function initTag(){
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/current_session_id",
        type: "POST",
        success: function(data){
            var sessionId = data.data;
            console.log('current sessionId = ' + sessionId);
            if (sessionId!=undefined){
                console.log('setting sessionId to historyLink');
                $('#historyLink').attr('href','history.xhtml?sessionId='+sessionId);
                drawSessionTag(sessionId);
            }
        }
    });
}

function drawSessionTag(sessionId){
    console.log('drawSessionDescription: sessionId = ' + sessionId);
    $.ajax({
        type: 'GET',
        url: '/BaseProjectWeb/resources/internal_sessions/session_descrtiption?sessionId=' + sessionId,
        success: function(data){
            console.log(data);
            $('#session_description').editable('setValue',(data.data == undefined) ? '' : data.data).editable('option', 'type', 'textarea');
            $('#session_description').on('save',  function(e, params){
                updateSessionDescription(sessionId, params.newValue);
            });
        }
    });
}