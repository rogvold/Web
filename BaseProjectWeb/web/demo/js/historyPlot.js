//to use this template script you should have div with id=mytimeline on a page
// on page ready you should invoke method initTimeline()
var timeline;
var gdata;
google.load("visualization", "1");
function initTimeline(){
    gdata = new google.visualization.DataTable();
    gdata.addColumn('datetime', 'start');
    gdata.addColumn('datetime', 'end');
    gdata.addColumn('string', 'content');
                
    console.log('initTimeline');
    selectedStart = new Date( (new Date().getTime() - 1000*3600*24*30*12));
    
    currentScale = ScaleEnum.OVERALL;
    sp = new SessionsPool();
    sp.loadSessions();
    draw();
}

function onBarSelect(){
    $('.selectedIntervalInfo').text(moment(myInterv.start).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(selectedEnd).format("MMMM Do YYYY, HH:mm:ss"));
}
            
function onselect(event){
    md = event;
    var row = undefined;
    var sel = timeline.getSelection();
    if (sel.length) {
        if (sel[0].row != undefined) {
            var row = sel[0].row;
        }
    }

    if (row != undefined) {
        var content = gdata.getValue(row, 0);
                    
        selectedStart = gdata.getValue(row,0);
        selectedEnd = gdata.getValue(row,1);
        onBarSelect();
        zoomIn();
    }
}
            
function draw(){
    if (selectedStart == undefined){
        selectedStart = (new Date()).getTime();
    }
    try{
        myInterv = sp.getMyInterval(selectedStart.getTime(), currentScale);
        //        selectedStart = new Date(myInterv.start);
        selectedEnd = new Date(myInterv.end);
        onBarSelect();
        drawHistoryVisualization(myInterv);
    }catch(err){
        
    }

}
            
function zoomOut(){
    currentScale = getPrevScale(currentScale);
    draw();
}
            
function zoomIn(){
    if (currentScale == ScaleEnum.SESSION) {
        console.log('currentScale is SESSION');
        return;
    }
    currentScale = getNextScale(currentScale);
    draw();
}

function refresh(){
    currentScale = ScaleEnum.OVERALL;
    draw();
}

function generateBar(val, cssClass){
    var style = 'height:' + 2 * val + 'px;';
    return '<div class="bar '+cssClass+'" style="' + style + '" >' + val + '</div>';
}            
            
            
function drawHistoryVisualization(myInterval) {
    if (currentScale != ScaleEnum.SESSION){
        gdata = new google.visualization.DataTable();
        gdata.addColumn('datetime', 'start');
        gdata.addColumn('datetime', 'end');
        gdata.addColumn('string', 'content');
    }
    // Create and populate a data table.
    var data = [];
                
    for (var i = 0; i < myInterval.bars.length; i++) {
        var bar = myInterval.bars[i];

        var start = new Date(bar.start);
        var end = new Date(bar.end);
        var maxim = generateBar(bar.max, 'maxBar');
        var average = generateBar(bar.avr, 'avrBar');
        var minim = generateBar(bar.min, 'minBar');
                    
        var dataDiv = '<div class="myBar myTooltip"  title=" <ul  style=\'color: lightyellow;\'><li>Maximal tension = '+bar.max+' </li> <li>Minimal tension = '+bar.min+' </li> <li>Average tension = '+bar.avr+' </li> </ul> " >'+ maxim+average+minim +'</div>'
        
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
    var options = {
        "min": new Date(myInterval.start - 1000*1800),
        "max": new Date(myInterval.end + 1000*1800),
        "width":  "100%",
        "height": "300px",
        "style": "box"
    };

    // Instantiate our timeline object.
    timeline = new links.Timeline(document.getElementById('mytimeline'));
    google.visualization.events.addListener(timeline, 'select', onselect);
    // Draw our timeline with the created data and options
    timeline.draw(data, options);
}

