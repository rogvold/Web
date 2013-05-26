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

function generateRandomRates(){
    var rates = new Array();
    var n = Math.floor( 20 + Math.random() * 5);
    for (var i = 0; i<n; i++){
        rates.push(Math.floor(700 + Math.random()*300));
    }
    //    alert('generated');
    return rates;
}