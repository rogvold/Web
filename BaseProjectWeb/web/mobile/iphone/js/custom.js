var pointsAmount = 0;
var DEFAULT_POINTS_AMOUNT = 20;
var pointsAmountInWindow = DEFAULT_POINTS_AMOUNT;

var collectingTempMaxTime = 120;
var collectingTime = 0;
var collectingTexts = [
'Fast, free and incredibly easy to use, the Ubuntu operating system powers millions of desktop PCs, laptops and servers around the world',
'Enjoy the simplicity of Ubuntu\'s stylish, intuitive interface. Take Ubuntu for a test drive in your browser and download when you\'re ready.',
'Want to talk to other Ubuntu users straightaway? Share ideas and get advice and help from our large, active community of IT professionals. As a community, we set high standards for friendliness and tolerance, we welcome your questions and contributions!',
'Perfect for business use, Ubuntu is secure, intuitive and lightweight. Maintain access to legacy applications without paying for licenses you don\'t need. And with remote management, your IT staff can do much more with less.',
'Ubuntu provides the largest selection of development tools and the fastest path to cloud deployment. Professional support is available from Canonical and the huge Ubuntu developer community is always a click away!',
'Ubuntu comes with a set of pre-installed software, including a complete office productivity suite and more than 3,000 downloadable apps including Steam, the world’s most popular gaming platform.',
'Our global community is made up of thousands of people who want to help build the best open-source operating system in the world. They volunteer their time and skills to make sure that Ubuntu keeps getting better and better.'
];

var tensionInitialized = false;

function getCurrentCollectingText(texts, p){
    return texts[ Math.floor(texts.length * p)];
}

function updateCollectingText(p){
    $('#collectingText').text(getCurrentCollectingText(collectingTexts, p));
}

function collectingOnTimerFunction(){
    var p = 1.0 * collectingTime / collectingTempMaxTime;
    addTestPoint();
    updateCollectingText(p);
    setPieChartPercents(Math.floor(100*p), '%');
    collectingTime++;
    if (p == 1.0){
        clearInterval(collectingSetInterval);
        alert('finished');
    }
}

function startCollectingTempAnimation(){
    initCollectingPieChart();
    initPlot(undefined, 'RR-intervals');
    collectingTime = 0;
//    collectingSetInterval = setInterval(function(){
//        collectingOnTimerFunction();
//    }, 1000);
}

function initCollectingPieChart() {
    $('.percentage').easyPieChart({
        animate: 500,
        size:120,
        lineWidth:10,
        lineCap:'square',
        barColor:'#bd362f',
        trackColor:'rgba(0,0,0,0.1)',
        scaleColor:'rgba(0,0,0,0.3)'
    });
};

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
    initPlot(k);
    tensionInitialized = true;
} 

function initPlot(k, name){
    if (k!=undefined){
        pointsAmountInWindow = k;
    }else{
        pointsAmountInWindow = DEFAULT_POINTS_AMOUNT;
    }
    if (name == undefined){
        name = '';
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
            marginLeft: 45,
            marginBottom: 45,
            backgroundColor:'rgba(255, 255, 255, 0.01)'
        },
        title: {
            text: name
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
                radius: 1
            },
            data: []
        }]
    });
    tensionInitialized = true;
}
  
  
  
function setPieChartPercents(p, s){
    if (s == undefined){
        s = '';
    }
    $('.percentage').each(function(){
        $(this).data('easyPieChart').update(p);  
        $('span', this).text(p + s);      
    });     
}

function setTensionPercents(p, s){
    setPieChartPercents(p, s);   
    updateCollectingText(p);
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
    var y = Math.floor(1000 + 100*Math.random());
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