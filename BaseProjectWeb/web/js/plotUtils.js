function drawPlot(divId, points, yAxis, minY){
    //    if (minY == undefined){
    //        minY = 0;
    //    }
    console.log('drawing plot in ' + divId);
    var options = {
        series: {
            shadowSize: 0, 
            color : '#CF3300'
        },
        yaxis: {
            color: '#92d5ea',
            min: minY
        },
        xaxis: {
            mode: "time",
            color: '#92d5ea'
        },
        selection: {
            mode: "x"
        },
        grid: {
            borderWidth: 0
        } 
    };
    
    var pl = $.plot($("#"+divId),
        [   {
            data: points, 
            lines: {
                show:true,
                spline: true
            }
        } ], options);
    
    if (yAxis == undefined || yAxis == false){
        $('#'+divId +" div.y1Axis").hide();
    }
    
    
    return pl;
}

function drawPie(divId, hf, lf, vlf, ulf){
    var d = [];
    d = [{
        label: "HF", 
        data: hf
    }, {
        label: "LF", 
        data: lf
    }, {
        label: "VLF", 
        data: vlf
    }, {
        label: "ULF", 
        data: ulf
    } ];

    $.plot($("#" + divId), d, 
    {
        series: {
            pie: { 
                show: true
                            
            }
        },
        legend: {
            show: false
        },
        grid: {
            borderWidth: 0
        } 
    });
}

function drawScatter(divId, mas, minX, maxX){
    //    console.log('drawScatter: input = ');
    //    console.log(mas);
    console.log('center = ');
    var center = findCenter(mas);
    console.log(center);
    var centData = new Array();
    centData.push(center);
    
    var optimum = findOptimum(center, mas);
    
    var a = optimum[0];
    var b = optimum[1];
    
    var el = generateEllipse(center, a, b);
    //    console.log('ellipse:');
    //    console.log(el);
    //    
    //    console.log('percent inside:');
    //    console.log(getPercentInside(center, a, b, mas));
    
    myData = [ 
    {
        data: mas, 
        lines: {
            show:false
        }, 
        points: {
            show:true,
            symbol: 'circle'
        }
    },
    {
        data: centData,
        points: {
            symbol: "cross",
            radius: 5,
            show: true
        }
    },
    {
        lines: {
            show:true,
            spline: true
        },
        data: el
    },
    {
        lines: {
            show:true
        },
        data: [[minX, minX], [maxX, maxX]]
    }
    ];
                
    return $.plot($("#" + divId), myData, {
        yaxis: {
            color: '#92d5ea',
            min: minX,
            max: maxX
        } 
    });
}

function getOneDimArray(d){
    var arr = new Array();
    for (var i in d){
        arr.push(d[i][1]);
    }
    return arr;
}

function getTensionPointsFromEvaluation(ev){
    var arr = new Array();
    for (var i in ev){
        arr.push([ev[i].timestamp, ev[i].tension]);
    }
    return arr;
}

function getLastPoints(points, amount){
    if (points == undefined || amount == undefined){
        return undefined;
    }
    var arr = new Array();
    if (amount > points) return points;
    
    for (var i = points.length - amount; i < points.length; i++){
        arr.push(points[i]);
    }
    return arr;
}

function getLastPointsInTimeWindow(points, span){
    if (points == undefined || span == undefined){
        return undefined;
    }
    var arr = new Array();
    var t = points[points.length - 1][0] - span;
    for (var i = points.length - 1; i >= 0; i--){
        if (points[i][0] < t){
            break;
        }
        arr.unshift(points[i]);
    }
    return arr;
}

function getCardioPointsFromPullSession(ev){
    var arr = new Array();
    for (var i in ev){
        arr.push([ev[i].timestamp, ev[i].value]);
    }
    return arr;
}

function drawDiffHistogramm(divId, d){
    var xmin = -500;
    var xmax = 500;
    var width = 8;
    var mas = [];
    for (var i=0; i <  Math.floor(1000 / width); i++){
        mas.push(0);
    }
    var rates = getOneDimArray(d);
    for (var i = 1; i < rates.length; i++){
        var u = Math.floor((rates[i-1] - rates[i] - xmin)/width);
        mas[Math.floor((rates[i-1] - rates[i] - xmin)/width)]++;
    }
    var mas2 = [];
                
    var imin = 0;
    for (var i = 0; i < mas.length; i++){
        if (mas[i] > 0 ){
            imin = i;
            break;
        }
    }
    var imax = mas.length - 1;
    for (var i = mas.length; i>0; i--){
        if (mas[i] > 0 ){
            imax = i;
            break;
        }
    }
                
    for (var i = imin; -i > - imax; i++){
        mas2.push([xmin + i * width , mas[i]]);
    }
                
    myData = [ 
    {
        data: mas2,
        bars: {
            show: true, 
            barWidth : width
        }
    }
    ];
    return $.plot($("#" + divId), myData);
}

function drawHistogramm(divId, d){
    var xmin = 300;
    var xmax = 1400;
    var width = 50;
    var mas = [];
    for (var i=0; i <  Math.floor(1000 / width); i++){
        mas.push(0);
    }
    var rates = getOneDimArray(d);
    for (var i = 0; i < rates.length; i++){
        //        var u = Math.floor((rates[i-1] - rates[i] - xmin)/width);
        mas[Math.floor((rates[i] - xmin)/width)]++;
    }
    var mas2 = [];
                
    var imin = 0;
    //    for (var i = 0; i < mas.length; i++){
    //        if (mas[i] > 0 ){
    //            imin = i;
    //            break;
    //        }
    //    }
    var imax = mas.length - 1;
    //    for (var i = mas.length; i>0; i--){
    //        if (mas[i] > 0 ){
    //            imax = i;
    //            break;
    //        }
    //    }
                
    for (var i = imin; i <  imax; i++){
        mas2.push([xmin + i * width , mas[i]]);
    }
                
    myData = [ 
    {
        data: mas2,
        bars: {
            show: true, 
            barWidth : width
        }
    }
    ];
    return $.plot($("#" + divId), myData);
}

function setSelection(plot, from, to){
    plot.setSelection({
        xaxis: {
            from: from, 
            to: to
        }
    });

}

function generatePoints(){
    var t = 0;
    var array = new Array();
    for (var i = 0; i < pointsAmount; i++){
        var span = 900 + Math.floor(100*Math.random());
        var p = [t, span];
        t+=span;
        array.push(p);
    }
    return array;
}
            
function generateEvaluation(d){
    var end = getPlotEnd(d);
    var t = window_width;
    var ar = new Array();
    while(t <= end){
        var p = {
            timestamp : t,
            tension : 500*Math.random(),
            ulf : 100*Math.random(),
            vlf : 100*Math.random(),
            lf : 100*Math.random(),
            hf : 100*Math.random()
        };
        ar.push(p);
        t+= step;
    }
    return ar;
}

            

            
function getCurrentEvaluationIndex(ev, to){
    for(var i = ev.length - 1; i >=0; i--){
        if (ev[i].timestamp - 2000 <= to){
            return i;
        }
    }
}
            
function getPlotStart(d){
    return d[0][0];
}
            
function getPlotEnd(d){
    return d[d.length - 1][0];
}
            
function getTotalDuration(d){
    return getPlotEnd(d) - getPlotStart(d);
}
            
function getPointsInRange(d, from, to){
    var a = new Array();
    for (var i in d){
        if ( (d[i][0] <= to) && (d[i][0] >= from) ){
            a.push(d[i]);
        }
    }
    return a;
}

function getMinInArray(points){
    if (points == undefined || points.length == 0){
        return undefined;
    }
    var mini = points[0][1];
    var p;
    for (var i in points){
        if (points[i][1] < mini){
            mini = points[i][1];
        }
    }
    console.log('getMinInArray: min = ' + mini);
    return mini;
}

function getMaxInArray(points){
    if (points == undefined || points.length == 0){
        return undefined;
    }
    var maxi = points[0][1];
    var p;
    for (var i in points){
        if (points[i][1] > maxi){
            maxi = points[i][1];
        }
    }
    console.log('maxi = ' + maxi);
    return maxi;
}

function getSkattergram(d){
    if (d == undefined){
        return undefined;
    }
    var arr = new Array();
    for (var i = 1; i < d.length; i++){
        arr.push([d[i-1][1],d[i][1]]);
    }
    return arr;
}

function drawTension(divId, d){
    if (d == undefined){
        return undefined;
    }
    
    if (d.length == 1){
        $('#' + divId).html('<div style="text-align: right; font-size: 18px; margin-right: 150px; " > your tension for last 2 minutes is <b>' + d[0][1] + '</b> </div>');
        return undefined;
    }
    
    console.log('drawTension');
    console.log(d);
    
    var options = {
        series: {
            shadowSize: 0, 
            color : 'green'
        },
        yaxis: {
            color: '#92d5ea'
        },
        selection: {
            mode: "x", 
            color: 'green'
        },
        xaxis: {
            mode: "time", 
            color: '#92d5ea'
        }
    };
    var opt = [ {
        data: d,
                        
        constraints: [      

        {
            threshold: {
                below:150
            },
            color: "#99FF33"
        },
        {
            threshold: {
                below:80
            },
            color: "#CCFF99"
        }
        ,
        
        {
            threshold: {
                above:150
            },
            color: "#FF6633"
        }        
        ]
    } ] ;
                
    return $.plot("#" + divId, opt , options);
}

function sortEvaluation(evalu){
    if (evalu == undefined){
        return undefined;
    }
    for (var i = 0; i < evalu.length; i++){
        for (var j = i+1; j<evalu.length; j++){
            //            console.log('i/j='+i+'/'+j);
            if (evalu[j].timestamp < evalu[i].timestamp){
                var c = evalu[i];
                evalu[i] = evalu[j];
                evalu[j] = c;
            }
        }
    }
    return evalu;
}

function getPointsInTheRighestWindow(points){
    window_width = 2 * 60 * 1000;
    if (points == undefined || points.length == 0){
        return undefined;
    }
    var arr = new Array();
    var et = points[points.length - 1][0] - window_width;
    for (var i in points){
        if (points[i][0] >= et){
            arr.push(points[i]);
        }
    }
    return arr;
}

function drawHistoryPlotWithoutSlider(points, ev){
    console.log('drawHistoryPlots: points = ');
    console.log(points);
    var dacha = points;
    var evaluation = ev;
    window_width = 2 * 60 * 1000;
    
    tensionPlot = drawTension('tension', getTensionPointsFromEvaluation(evaluation), true, 0);
    miniPlot = drawPlot('flot', dacha);
    //    plot = drawPlot('flot2', getPointsInRange(dacha, 0, window_width), true, 0);
                
    //    prevEvi = -1;
    //    $( "#slider" ).slider({
    //        value: 0,
    //        max: 1000,
    //        slide: function( event, ui ) {
    //            var f = Math.floor(getPlotStart(dacha) +  (ui.value/ 1000)*getTotalDuration(dacha));
    //
    //
    //            var t = f + window_width;
    //            
    //            //            console.log('slide: f / t');
    //            //            console.log(f);
    //            //            console.log(t);
    //            if (t > getPlotEnd(dacha)){
    //                console.log( t + ' > ' + getPlotEnd(dacha));
    //                return false;
    //            }
    //            var evi = getCurrentEvaluationIndex(evaluation, t);
    //            var inRange = getPointsInRange(dacha, f, t);
    //            if (evi != undefined && evi != prevEvi){
    //                var e = evaluation[evi];
    //                drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
    //                scatterPlot = drawScatter('scatter', getSkattergram(inRange));
    //                drawDiffHistogramm('dif', inRange);
    //                setSelection(tensionPlot, evaluation[Math.max(0,evi-1)].timestamp, evaluation[evi].timestamp);
    //                prevEvi = evi;
    //            }
    //            setSelection(miniPlot, f, t);
    //            plot = drawPlot('flot2', inRange, true, 0);
    //        }
    //    });
    //    $('.ui-slider-handle').width($('#slider').width() * window_width / getTotalDuration(dacha));
    //    setSelection(miniPlot, getPlotStart(dacha), getPlotStart(dacha) + window_width);
    var f = getPlotStart(dacha);
    var t = f + window_width;
    //    var inRange = getPointsInRange(dacha, f, t);
    //    plot = drawPlot('flot2', inRange, true, 0);

    var e = evaluation[0];
    drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
    scatterPlot = drawScatter('scatter', getSkattergram(inRange));
    //    drawDiffHistogramm('dif', inRange);
    drawHistogramm('dif', inRange);
}



function drawTrippleGraph(points, e){
    if (e != undefined){
        drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
    }
    if (points == undefined){
        return;
    }
    
    var minX = getMinInArray(points) - 10;
    var maxX = getMaxInArray(points) + 10;
    
    var range = getPointsInTheRighestWindow(points);
    //    console.log('range for triple: length = ' + range.length);
    //    console.log(range);
    scatterPlot = drawScatter('scatter', getSkattergram(range), minX, maxX);
    //    drawDiffHistogramm('dif', range);
    drawHistogramm('dif', range);

}

function drawHistoryPlots(points, ev){
    //    console.log('drawHistoryPlots: points = ');
    //    console.log(points);
    var dacha = points;
    var evaluation = ev;
    window_width = 2 * 60 * 1000;
    
    tensionPlot = drawTension('tension', getTensionPointsFromEvaluation(evaluation), true, 0);
    miniPlot = drawPlot('flot', dacha);
    plot = drawPlot('flot2', getPointsInRange(dacha, 0, window_width), true, 0);
         
    var minX = getMinInArray(points) - 50;     
    var maxX = getMaxInArray(points) + 50;     
         
    prevEvi = -1;
    $( "#slider" ).slider({
        value: 0,
        max: 1000,
        slide: function( event, ui ) {
            var f = Math.floor(getPlotStart(dacha) +  (ui.value/ 1000)*getTotalDuration(dacha));


            var t = f + window_width;
            
            //            console.log('slide: f / t');
            //            console.log(f);
            //            console.log(t);
            if (t > getPlotEnd(dacha)){
                console.log( t + ' > ' + getPlotEnd(dacha));
                return false;
            }
            var evi = getCurrentEvaluationIndex(evaluation, t);
            var inRange = getPointsInRange(dacha, f, t);
            if (evi != undefined && evi != prevEvi){
                var e = evaluation[evi];
                drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
                scatterPlot = drawScatter('scatter', getSkattergram(inRange), minX, maxX);
                //                drawDiffHistogramm('dif', inRange);
                drawHistogramm('dif', inRange);

                setSelection(tensionPlot, evaluation[Math.max(0,evi-1)].timestamp, evaluation[evi].timestamp);
                prevEvi = evi;
            }
            setSelection(miniPlot, f, t);
            plot = drawPlot('flot2', inRange, true, 0);
        }
    });
    $('.ui-slider-handle').width($('#slider').width() * window_width / getTotalDuration(dacha));
    setSelection(miniPlot, getPlotStart(dacha), getPlotStart(dacha) + window_width);
    var f = getPlotStart(dacha);
    var t = f + window_width;
    var inRange = getPointsInRange(dacha, f, t);
    plot = drawPlot('flot2', inRange, true, 0);

    var e = evaluation[0];
    drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
    scatterPlot = drawScatter('scatter', getSkattergram(inRange), minX, maxX);
    //    drawDiffHistogramm('dif', inRange);
    drawHistogramm('dif', inRange);

}

function initPlots(){
    pointsAmount = 1000;
    window_width = 2 * 60 * 1000;
    step = 20 * 1000;
                        
    dacha = generatePoints();
    evaluation = generateEvaluation(dacha);
    tensionPlot = drawTension('tension', getTensionPointsFromEvaluation(evaluation), true, 0);
                
    miniPlot = drawPlot('flot', dacha);
    plot = drawPlot('flot2', getPointsInRange(dacha, 0, window_width), true, 0);
                
    prevEvi = -1;
    $( "#slider" ).slider({
        value: 0,
        max: 1000,
        slide: function( event, ui ) {
            var f = Math.floor(getPlotStart(dacha) +  (ui.value/ 1000)*getTotalDuration(dacha));

            var t = f + window_width;
            
            //            console.log('slide: f / t');
            //            console.log(f);
            //            console.log(t);
            if (t > getPlotStart(dacha) + getTotalDuration(dacha)){
                t = getPlotStart(dacha) + getTotalDuration(dacha);
            //                return false;
            }
            var evi = getCurrentEvaluationIndex(evaluation, t);
            var inRange = getPointsInRange(dacha, f, t);
            if (evi != undefined && evi != prevEvi){
                var e = evaluation[evi];
                drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
                scatterPlot = drawScatter('scatter', getSkattergram(inRange));
                //                drawDiffHistogramm('dif', inRange);
                drawHistogramm('dif', inRange);

                setSelection(tensionPlot, evaluation[Math.max(0,evi-1)].timestamp, evaluation[evi].timestamp);
                prevEvi = evi;
            }
            setSelection(miniPlot, f, t);
            plot = drawPlot('flot2', inRange, true, 0);
        }
    });
    $('.ui-slider-handle').width($('#slider').width() * window_width / getTotalDuration(dacha));
    setSelection(miniPlot, getPlotStart(dacha), getPlotStart(dacha) + window_width);
    var f = getPlotStart(dacha);
    var t = f + window_width;
    var inRange = getPointsInRange(dacha, f, t);
    plot = drawPlot('flot2', inRange, true, 0);
    var e = evaluation[0];
    drawPie('pie', e.hf, e.lf, e.vlf, e.ulf);
    scatterPlot = drawScatter('scatter', getSkattergram(inRange));
    //    drawDiffHistogramm('dif', inRange);
    drawHistogramm('dif', inRange);

}

function loadHistoryPlots(sessionId){
    console.log('session id = ' + sessionId);
    //    getTensionPointsFromEvaluation
    $.ajax({
        url: "/BaseProjectWeb/resources/internal_sessions/session_evaluation",
        data: {
            sessionId : sessionId
        },
        type: "POST",
        success: function(da){
            de = da;
            var rates = getCardioPointsFromPullSession(da.data.rates);
            var evalu = sortEvaluation(da.data.evaluation);
            drawHistoryPlots(rates, evalu);
        }
    });
}