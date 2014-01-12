function drawRRPlot(divId, start, rates, title){
    var rrWrapper = new Highcharts.Chart({
        chart: {
            type: 'spline',
            lineWidth: 6,
            states: {
                hover: {
                    lineWidth: 7
                }
            },
            renderTo: divId,
            defaultSeriesType: 'line',
            marginRight: 10,
            marginBottom: 30,
            backgroundColor:'rgba(255, 255, 255, 0.1)',
            events: {
                load: function() {
                    //                    alert('chart loaded');
                    var series = this.series[0];
                    console.log(series);
                    var x = start;// start time
                    var y = rates[0];
                    series.addPoint([x, y], true, true);
                    
                    for (var i = 1; i<rates.length; i++){
                        x+=rates[i];
                        y = rates[i];
                        console.log('adding: x/y = ' + x +'/'+y);
                        series.addPoint([x, y], true, true);
                    }
                }
            }
        },
        title: {
            text: title
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
                this.x +': '+ this.y +'';
            }
        },
        series:  [{
            name: 'Random data',
            data: (function() {
                // generate an array of random data
                var data = [], time = (new Date()).getTime(), i;
                
                
                var x = start;// start time
                var y = rates[0];
                data.push({
                    x: x,
                    y: y
                });
                
                //                series.addPoint([x, y], true, true);
                    
                for (var i = 1; i<rates.length; i++){
                    x+=rates[i];
                    y = rates[i];
                    console.log('adding: x/y = ' + x +'/'+y);
                    data.push({
                        x: x,
                        y: y
                    });
                    
                //                    series.addPoint([x, y], true, true);
                }
                return data;
            })()
        }],
                            
        legend: {
            enabled: false
        },
                        
        plotOptions: {
            line: {
                lineWidth: 7,
                marker: {
                    enabled: false
                }
            },
            series: {
                marker: {
                    enabled: false
                }
            }
        }
    });
}


function drawTensionPlot(divId, points, title){
    var options = {
        series: {
            shadowSize: 0, 
            color : '#CF3300'
        },
        yaxis: {
            color: '#92d5ea',
            tickLentgth: 0
        },
        xaxis: {
            mode: "time",
            color: '#92d5ea'
        },
        grid: {
            borderWidth: 0
        }
        
    };
    

    plot2 = $.plot($("#"+divId),
        [   {
            data: points, 
            lines: {
                show:true
            }
        } ], options);
}

function generateRandomRates(){
    var rates = new Array();
    var n = Math.floor( 20 + Math.random() * 5);
    for (var i = 0; i<n; i++){
        rates.push(Math.floor(700 + Math.random()*300));
    }
    //    alert('generated');
    return rates;
}

function getStressComment(tension){
    if (tension > 300) return 'very high';
    if (tension > 150) return 'high';
    return 'normal';
}

function getStressClass(tension){
    if (tension > 300) return 'very_high_stress';
    if (tension > 150) return 'high_stress';
    return 'normal_stress';
}

function drawSessionInfo(){
    var max = $('.selectedBar .maxBar span').text();
    var min = $('.selectedBar .minBar span').text();
    var avr = $('.selectedBar .avrBar span').text();
    var tensTP = $('.selectedBar .tensTP').text();

    //    alert(tensTP);
    tensTP = (tensTP == undefined) ? 0 : tensTP;

    $('.history_page_info').hide();
    $('#info_stress_time_percents').text((Math.floor(100.0 * tensTP) / 100.0) + '%');

    $('#info_max_stress').text(max);
    $('#info_min_stress').text(min);
    $('#info_avr_stress').text(avr);
    
    $('#info_max_stress').next().text(getStressComment(max));
    $('#info_min_stress').next().text(getStressComment(min));
    $('#info_avr_stress').next().text(getStressComment(avr));
    
    $('#info_max_stress').next().attr('class',(getStressClass(max)));
    $('#info_min_stress').next().attr('class',getStressClass(min));
    $('#info_avr_stress').next().attr('class',getStressClass(avr));
}

//function drawSessionInfo(bar){
//    //    alert('bar = ' + bar);
//    var max = $('.selectedBar .maxBar').text();
//    var min = $('.selectedBar .minBar').text();
//    var avr = $('.selectedBar .avrBar').text()
//    $('.history_page_info').hide();
//    
//    $('#info_stress_time_percents').text(bar.stressTimePercents*100 + '%');
//    
//    $('#info_max_stress').text(bar.max);
//    $('#info_min_stress').text(bar.min);
//    $('#info_avr_stress').text(bar.avr);
//    
//    $('#info_max_stress').next().text(getStressComment(bar.max));
//    $('#info_min_stress').next().text(getStressComment(bar.min));
//    $('#info_avr_stress').next().text(getStressComment(bar.avr));
//    
//    $('#info_max_stress').next().attr('class',(getStressClass(bar.max)));
//    $('#info_min_stress').next().attr('class',getStressClass(bar.min));
//    $('#info_avr_stress').next().attr('class',getStressClass(bar.avr));
//}