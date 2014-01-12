//to use this template script you should have div with id=mytimeline on a page
// on page ready you should invoke method initTimeline()
var timeline;
var gdata;
google.load("visualization", "1");
function initTimeline(){
    
    $('.myBar').live('touchend', function(){
        //        alert('touchend');
        $(this).click();
    });
    
    gdata = new google.visualization.DataTable();
    gdata.addColumn('datetime', 'start');
    gdata.addColumn('datetime', 'end');
    gdata.addColumn('string', 'content');
               
    prepareDescriptionInplace();             
    console.log('initTimeline');
    selectedStart = new Date( (new Date().getTime() - 1000*3600*24*30*12));
    
    currentScale = ScaleEnum.YEAR;
    sp = new SessionsPool();
    sp.loadSessions(function(sess){
        sp.sessions = sess;
        draw();
        scaleToSession();
        disablePreloader();
    });
//    draw();
}

function prepareDescriptionInplace(){
    $('#session_description').editable({
        type: 'textarea'
    });
    $('#session_description').on('save',  function(e, params){
        updateSessionDescription(selectedSession.id, params.newValue);
    });
}

$('.sessionBar').live('click', function(){
    //    alert($(this).attr('id'));
    
    enablePreloader();
    //    $('#currSess').show();
    
    
    $('#sessionInfo').show();
    
    
    selectedSession = new SimpleSession();
    selectedSession.id = $(this).attr('id');
    selectedSession.start = (new Date()).getTime();
    var sip = new SessionPlot(selectedSession);
    sip.drawSessionPlot('currSess');
    $('.sessionBar').removeClass('selectedBar');
    $(this).addClass("selectedBar");
    drawSessionDescription(selectedSession.id);
//    $('.infAbout').text(moment(sip.session.start).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(sip.session.end).format("MMMM Do YYYY, HH:mm:ss"));
});

function onBarSelect(){
//    $('.selectedIntervalInfo').text(moment(myInterv.start).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(selectedEnd).format("MMMM Do YYYY, HH:mm:ss"));
}
            
function onselect(event){
    //    alert('onselect occured');
    md = event;
    var row = undefined;
    var sel = timeline.getSelection();
    if (sel.length) {
        if (sel[0].row != undefined) {
            var row = sel[0].row;
        }
    }
    //    alert('row = ' + row);
    if (row != undefined) {
        var content = gdata.getValue(row, 0);
                    
        selectedStart = gdata.getValue(row,0);
        selectedEnd = gdata.getValue(row,1);
        onBarSelect();
        zoomIn();
    }
}
            
function draw(){
    //    alert('draw is launched');
    console.log('draw is launched');
    if (selectedStart == undefined){
        selectedStart = (new Date()).getTime();
    }
    try{
        myInterv = sp.getMyInterval(selectedStart.getTime(), currentScale);
        //        selectedStart = new Date(myInterv.start);
        selectedEnd = new Date(myInterv.end);
        onBarSelect();
        drawHistoryVisualization(myInterv);
        if (myInterv.bars.length == 1){
            $('.myBar').click();
        }
    }catch(err){
        
    }

}
            
function zoomOut(){
    if (currentScale == ScaleEnum.SESSION){
        currentScale = getPrevScale(currentScale);
    }
    currentScale = getPrevScale(currentScale);
    draw();
}
            
function zoomIn(){
    currentScale = getNextScale(currentScale);
    //    alert('next is ' + currentScale.name);
    draw();
}


function refresh(){
    currentScale = ScaleEnum.YEAR;
    $('#currSess').hide();
    $('#sessionInfo').hide();
    $('.history_page_info').show();
    draw();
}

function generateBar(val, cssClass){
    var textMinHeight = 20;
    if (cssClass == 'minBar'){
        textMinHeight = 10;
    }
    if (cssClass == 'avrBar'){
        textMinHeight = 20;
    }
    if (cssClass == 'maxBar'){
        textMinHeight = 30;
    }
    
    var style = 'height:' +  (val * alfa + textMinHeight) + 'px;';
    return '<div class="bar '+cssClass+'" style="' + style + '" ><span>' + val + '</span></div>';
//    return '<div class="bar '+cssClass+'" style="' + style + '" >' + '' + '</div>';
}            
            
            
function drawHistoryVisualization(myInterval) {
    //    alert('drawHistoryVisualization');
    if (currentScale == ScaleEnum.DAY){
    //        alert('day drawing');
    }

    if (currentScale != ScaleEnum.SESSION){
        gdata = new google.visualization.DataTable();
        gdata.addColumn('datetime', 'start');
        gdata.addColumn('datetime', 'end');
        gdata.addColumn('string', 'content');
    }
    // Create and populate a data table.
    var data = [];
                
    var globMax = -1;
    for (var i = 0; i < myInterval.bars.length; i++) {
        if (myInterval.bars[i].max > globMax){
            globMax = myInterval.bars[i].max;
        }
    }
    //    
    alfa = 180.0/globMax;
    currentEnd = (new Date()).getTime();
             
    for (var i = 0; i < myInterval.bars.length; i++) {
        var bar = myInterval.bars[i];

        var start = new Date(bar.start);
        var end = new Date(bar.end);
        var maxim = generateBar(bar.max, 'maxBar');
        var average = generateBar(bar.avr, 'avrBar');
        var minim = generateBar(bar.min, 'minBar');
        //        var tensTP = generateBar(bar.stressTimePercents, 'tensTP');
        var tensTP = '<span style="display: none;" class="tensTP">' + bar.stressTimePercents * 100 + '</span>'
        
        console.log('minim = ' + bar.min);
        var dataDiv = '';
        var sId = '';
        var cClass = '';
        
        if (!isNaN(bar.min)){
            //            console.log(bar);
            //            drawSessionInfo(bar);
            if (currentScale == ScaleEnum.DAY){
                sId = ' id = \''+ bar.sessionId+'\' ';
                cClass = 'sessionBar';
            }
            dataDiv = '<div class="myBar myTooltip ' + cClass + '"' + sId + '  title=" <ul  style=\'color: lightyellow;\'><li>Maximal tension = '+bar.max+' </li> <li>Minimal tension = '+bar.min+' </li> <li>Average tension = '+bar.avr+' </li> </li> <li>You were stressed '+ Math.floor(10000.0 * bar.stressTimePercents)/100.0 +'% of time </li> </ul> " >'+ maxim+average+minim + tensTP +'</div>'
        //            dataDiv = '<div class="myBar myTooltip"  title=" <ul  style=\'color: lightyellow;\'><li>Maximal tension = '+bar.max+' </li> <li>Minimal tension = '+bar.min+' </li> <li>Average tension = '+bar.avr+' </li> </ul> " >'+ average +'</div>'
        }else{
            continue;
        }
        currentEnd = end;
        
        var item = {
            'group': '',
            'start': start,
            'end': end,
            'content': dataDiv
        };
        gdata.addRow([item.start, item.end, item.content]);
        data.push(item);
    //        d = new Date(end);
    }

    // specify options
    var border = (currentEnd.getTime() - myInterval.start) * 0.02;
    var options = {
        'zoomable': false,
        'start': new Date(myInterval.start - border),
        'end': new Date(currentEnd.getTime() + border),
        "min": new Date(myInterval.start - border),
        "max": new Date(currentEnd.getTime() + border),
        "width":  "100%",
        "height": "300px",
        "style": "box"
    };

    // Instantiate our timeline object.
    timeline = new links.Timeline(document.getElementById('mytimeline'));
    google.visualization.events.addListener(timeline, 'select', onselect);
    // Draw our timeline with the created data and options
    timeline.draw(data, options);
    
    $('.timeline-event.timeline-event-range .timeline-event-content .myBar .bar').each(function(){
        if ($(this).parent().parent().width() < 30){
            $(this).children('span').hide();
        }
    });
    
}


function getSessionById(sessionId){
    if (sessionId == undefined){
        return undefined;
    }
    for (var i in sp.sessions){
        if (sp.sessions[i].id == sessionId){
            return sp.sessions[i];
        }
    }
}

function gup( name ){
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");  
    var regexS = "[\\?&]"+name+"=([^&#]*)";  
    var regex = new RegExp( regexS );  
    var results = regex.exec( window.location.href ); 
    if( results == null )    return "";  
    else    return results[1];
}

function scaleToSession(){
    var sessionId = gup('sessionId');
    if (sessionId == undefined){
        return;
    }
    var session = getSessionById(sessionId);
    if (session == undefined){
        return;
    }
    currentScale = ScaleEnum.DAY;
    myInterv = sp.getMyInterval(session.start, currentScale);
    drawHistoryVisualization(myInterv);
    $('#'+sessionId).click();
}