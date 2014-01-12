var ScaleEnum = {
    OVERALL: {
        name: 'overall'  
    },
    YEAR: {
        name: 'year'
    },
    MONTH: {
        name: 'month'
    },
    DAY: {
        name: 'day'
    },
    HOUR: {
        name: 'hour'
    },
    SESSION:{
        name:'session'
    }
}

function getNextScale(scale){
    if (scale === ScaleEnum.OVERALL){
        console.log('currentScale = ' + scale.name + ' ; so next scale is ' + ScaleEnum.YEAR.name);
        return ScaleEnum.YEAR;
    }
    if (scale === ScaleEnum.YEAR){
        console.log('currentScale = ' + scale.name + ' ; so next scale is ' + ScaleEnum.MONTH.name);
        return ScaleEnum.MONTH;
    }
    if (scale === ScaleEnum.MONTH){
        console.log('currentScale = ' + scale.name + ' ; so next scale is ' + ScaleEnum.DAY.name);
        return ScaleEnum.DAY;
    }
    if (scale === ScaleEnum.DAY){
        console.log('currentScale = ' + scale.name + ' ; so next scale is ' + ScaleEnum.SESSION.name);
        return ScaleEnum.SESSION;
    }
    
    if (scale === ScaleEnum.SESSION){
        console.log('currentScale = ' + scale.name + ' ; so next scale is ' + ScaleEnum.SESSION.name);
        return ScaleEnum.SESSION;
    }
    
    return ScaleEnum.OVERALL;
}

function getPrevScale(scale){
    //    if (scale === ScaleEnum.YEAR){
    //        console.log('currentScale = ' + scale.name + ' ; so previous scale is ' + ScaleEnum.OVERALL.name);
    //        return ScaleEnum.OVERALL;
    //    }
    if (scale === ScaleEnum.MONTH){
        console.log('currentScale = ' + scale.name + ' ; so previous scale is ' + ScaleEnum.YEAR.name);
        return ScaleEnum.YEAR;
    }
    if (scale === ScaleEnum.DAY){
        console.log('currentScale = ' + scale.name + ' ; so previous scale is ' + ScaleEnum.MONTH.name);
        return ScaleEnum.MONTH;
    }
    if (scale === ScaleEnum.SESSION){
        console.log('currentScale = ' + scale.name + ' ; so previous scale is ' + ScaleEnum.DAY.name);
        return ScaleEnum.DAY;
    }
    //    return ScaleEnum.OVERALL;
    return ScaleEnum.YEAR;
    
}

function isSessionInside(session, left, right){
    if ( ((session.start > left) && (session.start < right )) ){
        return true;
    }
    return false;
}

function generateRandomSessions(){
    var sArray = new Array();
    var ep = (new Date()).getTime() - 1000*3600*24*30*13;
    var sDur = 1000 * 3600 * 5;
    for (var i = 0; i<= 1800; i++){
        var ss = new SimpleSession(ep, ep, ep +  Math.floor(Math.random()*sDur), 70 + Math.floor(Math.random()*30), Math.floor(Math.random()*20) + 20, 60 + Math.floor(Math.random()*20) );
        sArray.push(ss);
        ep+= sDur
    }
    return sArray;
}




function SessionsPool(sessions){
    this.sessions = sessions;
        
    this.loadSessions = function(callback){
        //        this.sessions = generateRandomSessions();
        $.ajax({
            type: "GET",
            url: "/BaseProjectWeb/resources/internal_sessions/all",
            success: function(data){
                console.log(data);
                this.sessions = data.data;
                callback(data.data);
            }
        });
    };
    
    this.getSessionsInRange = function(start, end){
        var array = new Array();
        for (i in this.sessions){
            if (isSessionInside(this.sessions[i], start, end)){
                array.push(this.sessions[i]);
            }
        }
        return array;
    };
    
    this.getMaxTensionInSessionArray = function(sess){
        var w = 0.0;
        var sum = 0.0;
        for (i in sess){
            sum += (sess[i].end - sess[i].start);
            w += sess[i].maxTension * (sess[i].end - sess[i].start);
        }
        return Math.floor(w / sum) ;
    };
    
    this.getMinTensionInSessionArray = function(sess){
        var w = 0.0;
        var sum = 0.0;
        for (i in sess){
            sum += (sess[i].end - sess[i].start);
            w += sess[i].minTension * (sess[i].end - sess[i].start);
        }
        return Math.floor(w / sum) ;
    };
    
    this.getAvrTensionInSessionArray = function(sess){
        var w = 0.0;
        var sum = 0.0;
        for (i in sess){
            sum += (sess[i].end - sess[i].start);
            w += sess[i].avrTension * (sess[i].end - sess[i].start);
        }
        return Math.floor(w / sum) ;
    };
    
    this.getTensionTimePercentsInSessionArray = function(sess){
        var w = 0.0;
        var totalTime = 0.0;
        var tensionTime = 0.0;
        for (i in sess){
            totalTime += (sess[i].end - sess[i].start);
            tensionTime += (sess[i].end - sess[i].start) * sess[i].stressTimePercents;
            w += sess[i].avrTension * (sess[i].end - sess[i].start);
        }
        console.log('tension time = ' + tensionTime);
        console.log('total time = ' + totalTime);
        
        return Math.floor( 100.0 * tensionTime / totalTime) / 100.0;
    };
    
    
    this.getSessionBar = function(start, end){
        var bar = new MySessionBar();

        var sess = this.getSessionsInRange(start, end);
        bar.max = this.getMaxTensionInSessionArray(sess);
        bar.min = this.getMinTensionInSessionArray(sess);
        bar.avr = this.getAvrTensionInSessionArray(sess);
        bar.stressTimePercents = this.getTensionTimePercentsInSessionArray(sess);
        

        bar.start = start; 
        bar.end = end;
        return bar;
    };
    
    this.getPrettySessionBar = function(start, end){
        var bar = new MySessionBar(); 

        var sess = this.getSessionsInRange(start, end);
        bar.max = this.getMaxTensionInSessionArray(sess);
        bar.min = this.getMinTensionInSessionArray(sess);
        bar.avr = this.getAvrTensionInSessionArray(sess);
        
        if (sess != undefined && sess[0] != undefined){
            bar.start = sess[0].start;
        }else{
            bar.start = start; 
        }
        
        if (sess != undefined && sess[sess.length - 1] != undefined){
            bar.end = sess[sess.length - 1].end;
        }else{
            bar.end = end; 
        }
        

        bar.start = start; 
        bar.end = end;
        return bar;
    };
    

    
    this.getYearMyInterval = function(start){
        //        var yStart = moment(start).startOf("year").startOf('second');
        //        var yEnd = moment(start).endOf("year");
        var yStart = moment(this.sessions[0].start).startOf("month").startOf('second');
        var yEnd = moment(this.sessions[this.sessions.length - 1].end).endOf("month");
        var monthsBetween = Math.floor(moment.duration(yEnd - yStart).asMonths()) + 1;
        //    alert(monthsBetween);
    
    
        var myInterval = new MyTimeInterval();
        myInterval.start = yStart.valueOf();
        myInterval.end = yEnd.valueOf();
        myInterval.scale = ScaleEnum.YEAR;
        myInterval.name = yEnd.format("YYYY");

        var barsArray = new Array();
        for (var i = 0; i < monthsBetween; i++){
            var m = yStart.clone();
            m.add('months', i).startOf("second");
            var bar = this.getSessionBar(m.valueOf(), m.endOf('month').valueOf());
            bar.name = m.format("MMMM");
            bar.scale = ScaleEnum.MONTH;
            barsArray.push(bar);
        }

        myInterval.bars = barsArray;
        return myInterval;
    };
    
    this.getMonthMyInterval = function(start){
        var mStart = moment(start).startOf("month").startOf('second');
        var mEnd = moment(start).endOf("month");
        
        console.log('month: start='+mStart + ' ; end = '+ mEnd);
        
        var myInterval = new MyTimeInterval();
        myInterval.start = mStart.valueOf();
        myInterval.end = mEnd.valueOf();
        myInterval.scale = ScaleEnum.MONTH;
        myInterval.name = mEnd.format("MMMM");
        
        var barsArray = new Array();
        for (var i = 0; i < mStart.daysInMonth(); i++){
            var m = mStart.clone();
            m.add('days', i).startOf("second");
            var bar = this.getSessionBar(m.valueOf(), m.endOf('day').valueOf());
            bar.name = m.format("D");
            bar.scale = ScaleEnum.DAY;
            barsArray.push(bar);
        }
        
        myInterval.bars = barsArray;
        
        return myInterval;
    };
    
    this.getDayMyInterval = function(start){
        var dStart = moment(start).startOf("day").startOf('second');
        var dEnd = moment(start).endOf("day");
        
        console.log('day: start='+dStart + ' ; end = '+ dEnd);
        
        var sess = this.getSessionsInRange(dStart.valueOf(), dEnd.valueOf());
        
        var myInterval = new MyTimeInterval();
        
        myInterval.start = dStart.valueOf();
        myInterval.end = dEnd.valueOf();
        myInterval.scale = ScaleEnum.DAY;
        myInterval.name = dEnd.format("DDDD");
        
        var barsArray = new Array();
        for (var i = 0; i < sess.length; i++){
            var bar = new MySessionBar();
            bar.start = sess[i].start;
            bar.end = sess[i].end;
            bar.max = sess[i].maxTension;
            bar.min = sess[i].minTension;
            bar.avr = sess[i].avrTension;
            bar.stressTimePercents = sess[i].stressTimePercents;
            bar.sessionId = sess[i].id;
            
            bar.scale = ScaleEnum.SESSION;
            barsArray.push(bar);
        }
        
        myInterval.bars = barsArray;
        
        return myInterval;
    };
    
        
    this.getOverallMyInterval = function(){
        console.log('getOverallMyInterval');
        var sess = this.sessions;
        var myInterval = new MyTimeInterval();
        if (sess == undefined){
            return null;
        }
        
        myInterval.start = sess[0].start;
        myInterval.end = sess[sess.length - 1].end;
        myInterval.scale = ScaleEnum.OVERALL;
        myInterval.name = "OVERALL";
        
        var barsArray = new Array();
        var currentDate = myInterval.start;
        
        var yStart = moment(myInterval.start).startOf("month").startOf('second');
        var i = 0;
        while (currentDate < myInterval.end){
            var m = yStart.clone();
            m.add('months', i).startOf("second");
            currentDate = m.valueOf();
            if (currentDate >= myInterval.end){
                break;
            }
            
            var bar = this.getSessionBar(m.valueOf(), m.endOf('month').valueOf());
            bar.name = m.format("MMMM");
            bar.scale = ScaleEnum.MONTH;
            barsArray.push(bar);
            i++;
        }
        
        myInterval.bars = barsArray;
        return myInterval;
    }
    
    
    this.getMyInterval = function(start, scale){
        console.log('scale = ' + scale.name);
        console.log('getMyInterval for scale='+scale.name);
        if (scale === ScaleEnum.OVERALL){
            return this.getOverallMyInterval();
        }
        if (scale === ScaleEnum.YEAR){
            return this.getYearMyInterval(start);
        }
        if (scale === ScaleEnum.MONTH){
            return this.getMonthMyInterval(start);
        }
        if(scale == ScaleEnum.DAY){
            return this.getDayMyInterval(start);
        }
        if (scale === ScaleEnum.SESSION){
            console.log('selected session');
        //            alert('redirecting session');
        }
    }
}

function SessionPlot(session){
    this.session = session;
    
    this.generateRandomSession = function(){
        this.session.rates = generateRandomRates();
        var t = this.session.start;
        for (var i in this.session.rates){
            t+=this.session.rates[i];
        }
        this.session.end = t;
    };
    
    //    this.obtainSessionRates = function(){
    //        $.ajax({
    //            type: 'GET',
    //            url: '/BaseProjectWeb/resources/internal_sessions/tension?sessionId='+session.id,
    //            success: function(data){
    //                ddd = data;
    //                this.session.tensionPoints = data.data;
    //                drawSessionPlot();
    //            }
    //        });
    //    }
    
    this.drawSessionPlot = function(divId){
        console.log('draw session plot occured');
        //        var title = moment(this.session.start).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(this.session.end).format("MMMM Do YYYY, HH:mm:ss");
        $('#plotsPanel').show();
        loadHistoryPlots(session.id);
        //        $('.infAbout').text(moment(data.data[0][0] + 0).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(data.data[data.data.length - 1][0] + 0).format("MMMM Do YYYY, HH:mm:ss"));
        //        drawSessionInfo();

        $.ajax({
            type: 'GET',
            url: '/BaseProjectWeb/resources/internal_sessions/tension_array?sessionId='+session.id,
            success: function(data){
                ddd = data;
                console.log('disabling preloader...');
                disablePreloader();
                //                this.session.tensionPoints = data.data;
                //                drawTensionPlot(divId, data.data, 'Tension plot');
                    
                //                $('#plotsPanel').show();
                //                loadHistoryPlots(session.id);
                    
                $('.infAbout').text(moment(data.data[0][0] + 0).format("MMMM Do YYYY, HH:mm:ss") + ' - ' + moment(data.data[data.data.length - 1][0] + 0).format("MMMM Do YYYY, HH:mm:ss"));
                drawSessionInfo();
            },
            error: function(){
                disablePreloader();

            }
        });
    }
}


function SimpleSession(id, start, end, maxTension, minTension, avrTension){
    this.id = id;
    this.start = start;
    this.end = end;
    this.maxTension = maxTension;
    this.minTension = minTension;
    this.avrTension = avrTension;
    this.rates = undefined;
}

function MySessionBar(scale, start, end, name, max, min, avr){
    this.scale = scale;
    this.start = start;
    this.end = end;
    this.name = name;
    this.max = max;
    this.min = min;
    this.avr = avr;
    this.sessionId = undefined;
}

function MyTimeInterval(start, end, scale, name){
    this.scale = scale;
    this.start = start;
    this.end = end;
    this.name = name;
    this.bars = null;
}

