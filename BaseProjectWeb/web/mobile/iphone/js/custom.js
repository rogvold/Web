var pointsAmount = 0;
var DEFAULT_POINTS_AMOUNT = 20;
var pointsAmountInWindow = DEFAULT_POINTS_AMOUNT;


var tensionInitialized = false;

function initPieChart() {
    $('.percentage').easyPieChart({
        animate: 500,
        size:70,
        lineWidth:10,
        lineCap:'square',
        barColor:'#bd362f',
        trackColor:'rgba(0,0,0,0.1)',
        scaleColor:'rgba(0,0,0,0.3)'
    });
};
            
function initTensionPlot(k){
    if (k!=undefined){
        pointsAmountInWindow = k;
    }else{
        pointsAmountInWindow = DEFAULT_POINTS_AMOUNT;
    }
    
    hChart = new Highcharts.Chart({
        chart: {
            type: 'spline',
            lineWidth: 5,
            states: {
                hover: {
                    lineWidth: 6
                }
            },
            renderTo: 'highcharts',
            defaultSeriesType: 'line',
            marginRight: 10,
            marginLeft: 30,
            marginBottom: 30,
            backgroundColor:'rgba(255, 255, 255, 0.01)'
        },
        title: {
            text: 'Plot header'
        },
        subtitle: {
            text: null
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
            title: {
                text: null
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function() {
                return ''+
                new Date(this.x) +': '+ this.y +'';
            }
        },
        plotOptions: {
            line: {
                lineWidth: 3,
                marker: {
                    enabled: false
                }
            }
        },
			
        legend: {
            enabled: false
        },
        series:  [{
            marker: {
                radius: 3
            },
            data: []
        }]
    });
    tensionInitialized = true;
} 
            
function setTensionPercents(p){
    $('.percentage').each(function(){
        $(this).data('easyPieChart').update(p);  
        $('span', this).text(p + '');      
    });     
}
            
function addPoint(x, y){
    if (!tensionInitialized){
        initTensionPlot();
    }
    var xx = x;
    var yy = y;
    pointsAmount++;
    console.log('adding point: x = ' + x + " ; y = " + y);
    if (pointsAmount > pointsAmountInWindow){
        hChart.series[0].addPoint([xx, yy], true, true);
    }else{
        hChart.series[0].addPoint([xx, yy]);
    }
}
            
function addPoints(array){
    for (i in array){
        addPoint(array[i][0], array[i][1]);
    }
}
            
function setStressStateText(text){
    $('#stress_state').text(text);
}
            
function setDurationText(text){
    $('#duration_text').text(text);
}
            
function setRecommendationText(text){
    $('#recommendation_text').text(text);
}
            
function addTestPoint(){
    var x = (new Date()).getTime();
    var y = Math.floor(100 + 100*Math.random());
    addPoint(x, y);
}

function setPulse(pulse){
    $('#pulse').text(pulse);
}

function hrDataUpdated(data) {
    setPulse(data.rate);
    var t = data.timestamp;
    for (i in data.intervals){
        addPoint(t, data.intervals[i]);
        t+=data.intervals[i];
    }
}