var CardioMoodMath = {};
CardioMoodMath._construct = function () {
    String.prototype.hashCode = function () {
        var hash = 0, i, ch;
        if (this.length == 0) return hash;
        for (i = 0; i < this.length; i++) {
            ch = this.charCodeAt(i);
            hash = ((hash << 5) - hash) + ch;
            hash = hash & hash;
        }
        return hash;
    };

    function Training(intervals, filtrate) {
        var _intervals;
        if (filtrate) {
            var filter = new Filter(intervals);
            _intervals = filter.filtrate();
        } else {
            _intervals = intervals;
        }

        var _timestamps = [];
        var _total = 0;
        for (var i = 0; i < _intervals.length; i++) {
            _timestamps.push(_total);
            _total += _intervals[i];
        }

        this.getIndicatorValuesForWindow = function (width, step, indicatorNameArray) {
            var result = [];
            var sessions = [];
            var start = 0;
            var finish = width;
            while (finish < _total) {
                var idString = start.toString() + finish.toString();
                sessions.push(new SessionData(idString.hashCode(),
                    _getIntervalsForWindow(start, finish)));
                start += step;
                finish += step;
            }
            for (var key = 0; key < indicatorNameArray.length; key++) {
                var res = [];
                for (var i = 0; i < sessions.length; i++) {
                    var indicator = new Indicators[indicatorNameArray[key]]();
                    res[i] = sessions[i].evaluate(indicator);
                }
                result[indicatorNameArray[key]] = res;
            }
            return result;
        }

        var _getIntervalsForWindow = function (start, finish) {
            var startIndex = 0;
            var finishIndex = 0;
            for (var i = 0; i < _timestamps.length; i++) {
                if (_timestamps[i] > start && startIndex == 0) {
                    startIndex = i;
                }
                if (_timestamps[i] > finish) {
                    finishIndex = i;
                    break;
                }
            }
            var result = [];
            for (var i = startIndex; i < finishIndex; i++) {
                result.push(_intervals[i]);
            }
            return result;
        }
    }

    this.Training = Training;

    function Evaluation() {
        this.evaluate = function (session) {
        };
    }

    function SessionData(idString, intervals) {
        var _cache = new SessionDataCache();
        var _idString = idString;
        var _intervals = intervals;

        this.getIdString = function () {
            return _idString;
        }

        this.getIntervals = function () {
            return _intervals;
        };

        this.evaluate = function (evaluation) {
            if (_cache.contains(evaluation)) {
                return _cache.get(evaluation);
            } else {
                var evaluationResult = evaluation.evaluate(this);
                _cache.add(evaluation, evaluationResult);
                return evaluationResult;
            }
        };
    }

    function SessionDataCache() {
        var _cache = {};
        this.contains = function (evaluation) {
            return evaluation.constructor.name in _cache;
        };

        this.add = function (evaluation, result) {
            _cache[evaluation.constructor.name] = result;
        };

        this.get = function (evaluation) {
            return _cache[evaluation.constructor.name];
        };
    }

    var LinkedListNode = function (key) {
        return {
            key: key,
            next: null,
            prev: null
        };
    };

    var LinkedList = function () {
        var head = null;

        var insert = function (node) {
            node.next = head;
            if (head !== null) {
                head.prev = node;
            }
            head = node;
            node.prev = null;
        };

        var insertAfter = function (node, value) {
            value.next = node.next;
            value.prev = node;
            node.next = value;
        }

        var search = function (key) {
            var node = head;
            while (node !== null && node.key !== key) {
                node = node.next;
            }
            ;
            return node;
        };

        var remove = function (node) {
            if (node.prev === null) {
                head = node.next;
            }
            else {
                node.prev.next = node.next;
            }
            if (node.next !== null) {
                node.next.prev = node.prev
            }
        };

        var getHead = function () {
            return head;
        };

        return {
            insert: insert,
            insertAfter: insertAfter,
            remove: remove,
            search: search,
            getHead: getHead
        };
    };

    function Filter(intervals) {
        var _intervals = intervals;
        this.filtrate = function () {
            var intervals = new LinkedList();
            for (var i = _intervals.length - 1; i >= 0; i--) {
                intervals.insert(new LinkedListNode(_intervals[i]));
            }
            var countOfPVC = 0; //Premature Ventricular Contraction
            var current = intervals.getHead().next;
            while (current.next != null) {
                if (current.key / current.prev.key < 0.8) {
                    if (current.next.key / current.prev.key > 0.8 &&
                        current.next.key / current.prev.key < 1.2) {
                        current = current.next;
                        intervals.remove(current.prev);
                    }
                    else if (current.next.key / current.prev.key > 1.2) {
                        countOfPVC++;
                        var nextNextKey = current.next.next == null ? current.prev.key : current.next.next.key;
                        current.key = current.next.key = (current.prev.key + nextNextKey) / 2;
                    }
                }
                else {
                    var val1 = current.key / current.prev.key;
                    var val2 = Math.floor(val1);
                    if (val1 >= 1.9)
                        if (Math.abs(val1 - val2) < 0.2) {
                            for (var j = 0; j < val2 - 1; j++)
                                intervals.insertAfter(current, new LinkedListNode(current.key / val2));
                            current.key = current.key / val2;
                        }
                        else {
                            current = current.prev;
                            intervals.remove(current.next);
                        }
                }
                current = current.next;
            }
            var result = [];
            current = intervals.getHead();
            while (current.next != null) {
                result.push(current.key);
                current = current.next;
            }
            return result;
        }
    }

    var Indicators = {};
    Indicators._construct = function () {

        function AMoPercents() {
            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                var h = new Histogram();
                for (var interval in intervals) {
                    h.addRRInterval(intervals[interval]);
                }
                var maxRangeValue = h.getMaxIntervalNumber();
                var totalCount = h.getTotalCount();
                return Math.floor((maxRangeValue / totalCount) * 10000) /
                    100.0;
            };
        }

        AMoPercents.prototype = new Evaluation();
        AMoPercents.prototype.constructor = AMoPercents;
        this.AMoPercents = AMoPercents;

        function SI() {
            this.evaluate = function (session) {
                var mxDMn = session.evaluate(new MxDMn());
                var amo = session.evaluate(new AMoPercents());
                var mo = session.evaluate(new Mo());
                return Math.floor((amo * 1000 * 1000 / (2 * mxDMn * mo)) * 100) /
                    100.0;
            };
        }

        SI.prototype = new Evaluation();
        SI.prototype.constructor = SI;
        this.SI = SI;

        function CV() {
            this.evaluate = function (session) {
                var sdnn = session.evaluate(new SDNN());
                var average = session.evaluate(new Average());
                return sdnn / average * 100;
            };
        }

        CV.prototype = new Evaluation();
        CV.prototype.constructor = CV;
        this.CV = CV;

        function MxDMn() {
            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                return (Math.max.apply(Math, intervals) -
                    Math.min.apply(Math, intervals));
            };
        }

        MxDMn.prototype = new Evaluation();
        MxDMn.prototype.constructor = MxDMn;
        this.MxDMn = MxDMn;

        function Mo() {
            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                var h = new Histogram();
                for (var interval in intervals) {
                    h.addRRInterval(intervals[interval]);
                }
                return h.getMaxIntervalStart();
            };
        }

        Mo.prototype = new Evaluation();
        Mo.prototype.constructor = Mo;
        this.Mo = Mo;

        function Histogram() {
            var _intervals = [];
            var _lowBorder = 400;
            var _highBorder = 1300;
            var _step = 50;

            for (var i = _lowBorder; i < _highBorder; i += _step) {
                if (i + _step < _highBorder) {
                    _intervals.push({start: i,
                        end: i + _step,
                        values: []});
                }
                else {
                    _intervals.push({start: i,
                        end: i + _highBorder,
                        values: []});
                }
            }

            this.addRRInterval = function (length) {
                var that = this;
                if (length >= _lowBorder &&
                    length <= _highBorder) {
                    _getIntervalForRR(length).values.push(length);
                }
            };
            this.getMaxIntervalNumber = function () {
                var maxValue = 0;
                for (var interval in _intervals) {
                    if (_intervals[interval].values.length > maxValue) {
                        maxValue = _intervals[interval].values.length;
                    }
                }
                return maxValue;
            };
            this.getTotalCount = function () {
                var total = 0;
                for (var interval in _intervals) {
                    total += _intervals[interval].values.length;
                }
                return total;
            };
            this.getMaxIntervalStart = function () {
                var maxValue = this.getMaxIntervalNumber();
                var intervalStart = 0;
                for (var interval in _intervals) {
                    if (_intervals[interval].values.length == maxValue) {
                        intervalStart = _intervals[interval].start;
                        break;
                    }
                }
                return intervalStart;
            };
            var _getIntervalForRR = function (length) {
                for (var interval in _intervals) {
                    if (_intervals[interval].start <= length &&
                        _intervals[interval].end > length) {
                        return _intervals[interval];
                    }
                }
                return null;
            };
        }

        function PNN50() {
            this.evaluate = function (session) {
                var pnn = 0;
                var intervals = session.getIntervals();
                for (var i = 1; i < intervals.length; i++) {
                    var now = intervals[i];
                    var before = intervals[i - 1];
                    if (Math.abs(now - before) >= 50) {
                        pnn++;
                    }
                }
                return Math.floor(pnn / (intervals.length - 1) * 10000) /
                    100.0;
            };
        }

        PNN50.prototype = new Evaluation();
        PNN50.prototype.constructor = PNN50;
        this.PNN50 = PNN50;

        function Average() {
            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                var sum = 0;
                for (var i = 0; i < intervals.length; i++) {
                    sum += intervals[i];
                }
                return sum / intervals.length;
            };
        }

        Average.prototype = new Evaluation();
        Average.prototype.constructor = Average;
        this.Average = Average;

        function RMSSD() {
            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                var total = 0;

                for (var i = 1; i < intervals.length; i++) {
                    var now = intervals[i];
                    var before = intervals[i - 1];

                    total += Math.pow(now - before, 2);
                }
                return Math.sqrt(total / intervals.length);
            };
        }

        RMSSD.prototype = new Evaluation();
        RMSSD.prototype.constructor = RMSSD;
        this.RMSSD = RMSSD;

        function SDNN() {
            this.evaluate = function (session) {
                var average = session.evaluate(new Average());
                var total = 0;
                var intervals = session.getIntervals();
                for (var i in intervals) {
                    total += Math.pow(average - intervals[i], 2);
                }
                return Math.sqrt(total / intervals.length);
            };
        }

        SDNN.prototype = new Evaluation();
        SDNN.prototype.constructor = SDNN;
        this.SDNN = SDNN;

        function Lomb() {

            this.evaluate = function (session) {
                var intervals = session.getIntervals();
                var size = intervals.length;
                var freqValues = [];
                var intervalValues = [];
                var timeValues = [];
                var time = 0;
                for (var i = 0; i < size; i++) {
                    time += intervals[i] / 1000;
                    timeValues.push(time);
                    intervalValues.push(intervals[i] / 1000);
                }

                var maxFreq = 0.5 / Math.min.apply(Math, intervalValues);

                var freqSize = 0;
                var freqTotal = 0;

                while (freqTotal <= maxFreq) {
                    freqValues.push(freqSize / 1024);
                    freqSize++;
                    freqTotal += 1 / 1024;
                }

                var sum = 0;
                for (var i = 0; i < intervalValues.length; i++) {
                    sum += intervalValues[i];
                }
                var mean = sum / intervalValues.length;
                var stSum = 0;
                for (var i = intervalValues.length - 1; i >= 0; i--) {
                    stSum += Math.pow((intervalValues[i] - mean), 2);
                }
                var std = Math.sqrt(stSum / (intervalValues.length - 1));
                for (var i = 0; i < size; i++) {
                    intervalValues[i] -= mean;
                }

                var periodogram = [];

                for (var i = 0; i < freqSize; i++) {
                    var w = 2 * Math.PI * freqValues[i];

                    if (w > 0) {
                        var sinSum = 0;
                        var cosSum = 0;

                        for (var j = 0; j < size; j++) {
                            sinSum += Math.sin(2 * w * timeValues[j]);
                            cosSum += Math.cos(2 * w * timeValues[j]);
                        }

                        var tau = Math.atan(sinSum / cosSum) / 2 / w;

                        var sinHigh = 0;
                        var sinLow = 0;
                        var cosHigh = 0;
                        var cosLow = 0;

                        for (var j = 0; j < size; j++) {
                            sinHigh += intervalValues[j] *
                                Math.sin(w * (timeValues[j] - tau));
                            cosHigh += intervalValues[j] *
                                Math.cos(w * (timeValues[j] - tau));
                            cosLow += Math.pow(Math.cos(w * (timeValues[j] - tau)), 2);
                            sinLow += Math.pow(Math.sin(w * (timeValues[j] - tau)), 2);
                        }
                        periodogram.push({frequency: freqValues[i],
                            value: 1 / (2 * std * std) *
                                (sinHigh * sinHigh / sinLow +
                                    cosHigh * cosHigh / cosLow)});
                    }
                }

                return periodogram;
            };
        }

        Lomb.prototype = new Evaluation();
        Lomb.prototype.constructor = Lomb;

        function Square() {
        }

        Square.calculate = function (periodogram, left, right) {
            var square = 0;
            for (var i = 0, size = periodogram.length; i < size; i++) {
                if (periodogram[i].frequency >= left &&
                    periodogram[i].frequency <= right) {
                    square += periodogram[i].value;
                }
            }
            return square;
        };

        function HF() {
            this.evaluate = function (session) {
                var periodogram = session.evaluate(new Lomb());
                return Square.calculate(periodogram, 0.1, 1);
            };
        }

        HF.prototype = new Evaluation();
        HF.prototype.constructor = HF;
        this.HF = HF;

        function LF() {
            this.evaluate = function (session) {
                var periodogram = session.evaluate(new Lomb());
                return Square.calculate(periodogram, 0.0333, 0.1);
            };
        }

        LF.prototype = new Evaluation();
        LF.prototype.constructor = LF;
        this.LF = LF;

        function VLF() {
            this.evaluate = function (session) {
                var periodogram = session.evaluate(new Lomb());
                return Square.calculate(periodogram, 0.0033, 0.0333);
            };
        }

        VLF.prototype = new Evaluation();
        VLF.prototype.constructor = VLF;
        this.VLF = VLF;

        function ULF() {
            this.evaluate = function (session) {
                var periodogram = session.evaluate(new Lomb());
                return Square.calculate(periodogram, 0, 0.0033);
            };
        }

        ULF.prototype = new Evaluation();
        ULF.prototype.constructor = ULF;
        this.ULF = ULF;

        function HFPercents() {
            this.evaluate = function (session) {
                var hf = session.evaluate(new HF());
                var tp = session.evaluate(new TP());
                return (hf / tp) * 100;
            };
        }

        HFPercents.prototype = new Evaluation();
        HFPercents.prototype.constructor = HFPercents;
        this.HFPercents = HFPercents;

        function LFPercents() {
            this.evaluate = function (session) {
                var lf = session.evaluate(new LF());
                var tp = session.evaluate(new TP());
                return (lf / tp) * 100;
            };
        }

        LFPercents.prototype = new Evaluation();
        LFPercents.prototype.constructor = LFPercents;
        this.LFPercents = LFPercents;

        function VLFPercents() {
            this.evaluate = function (session) {
                var vlf = session.evaluate(new VLF());
                var tp = session.evaluate(new TP());
                return (vlf / tp) * 100;
            };
        }

        VLFPercents.prototype = new Evaluation();
        VLFPercents.prototype.constructor = VLFPercents;
        this.VLFPercents = VLFPercents;

        function ULFPercents() {
            this.evaluate = function (session) {
                var ulf = session.evaluate(new ULF());
                var tp = session.evaluate(new TP());
                return (ulf / tp) * 100;
            };
        }

        ULFPercents.prototype = new Evaluation();
        ULFPercents.prototype.constructor = ULFPercents;
        this.ULFPercents = ULFPercents;

        function TP() {
            this.evaluate = function (session) {
                var periodogram = session.evaluate(new Lomb());
                return Square.calculate(periodogram, 0, 1);
            };
        }

        TP.prototype = new Evaluation();
        TP.prototype.constructor = TP;
        this.TP = TP;

        function IC() {
            this.evaluate = function (session) {
                var hf = session.evaluate(new HF());
                var lf = session.evaluate(new LF());
                var vlf = session.evaluate(new VLF());

                return (lf + vlf) / hf;
            };
        }

        IC.prototype = new Evaluation();
        IC.prototype.constructor = IC;
        this.IC = IC;

        function RSAI() {
            this.evaluate = function (session) {
                var average = session.evaluate(new Average());
                var sdnn = session.evaluate(new SDNN());
                var cv = session.evaluate(new CV());
                var amo = session.evaluate(new AMoPercents());
                var mxdmn = session.evaluate(new MxDMn());
                var si = session.evaluate(new SI());
                var lfPercents = session.evaluate(new LFPercents());
                var hfPercents = session.evaluate(new HFPercents());
                var vlfPercents = session.evaluate(new VLFPercents()) +
                    session.evaluate(new ULFPercents());
                var totalFPercents = lfPercents + vlfPercents + hfPercents;

                var h = [0, 0, 0, 0, 0];
                //now we need to calculate 5 indexes
                //RSAI consists of these 5 indexes

                //h[0]
                //Cumulative effect of regulation
                if (average <= 660)
                    h[0] = 2;
                if (average <= 800)
                    h[0] = 1;

                if (average >= 800 && average <= 1000)
                    h[0] = 0;

                if (average >= 1000)
                    h[0] = -1;

                if (average >= 1200)
                    h[0] = -2;

                //h[1]
                //function of automatism
                if (sdnn <= 20 && mxdmn <= 0.1 && cv <= 2.0)
                    h[1] = 2;

                if (sdnn >= 100 && mxdmn >= 0.3 && cv >= 8.0)
                    h[1] = 1;

                if (mxdmn >= 0.45)
                    h[1] = -1;

                if (sdnn <= 100 && mxdmn >= 0.6 && cv <= 8.0)
                    h[1] = -2;

                //h[2]
                //vegetative homeostasis
                if (mxdmn <= 0.06 && amo >= 80 && si >= 500)
                    h[2] = 2;

                if (mxdmn <= 0.15 && amo >= 50 && si >= 200)
                    h[2] = 1;

                if (mxdmn >= 0.30 && amo <= 30 && si <= 50)
                    h[2] = -1;

                if (mxdmn >= 0.50 && amo <= 15 && si <= 25)
                    h[2] = -2;

                //h[3]
                //regulation stability
                if (cv <= 3 || cv >= 6)
                    h[3] = 2;

                //h[4]
                //Basal activity of the nervous centers
                if (lfPercents / totalFPercents >= 0.70 &&
                    vlfPercents / totalFPercents >= 0.25 &&
                    hfPercents <= 0.05)
                    h[4] = 2;

                if (lfPercents / totalFPercents >= 0.60 &&
                    hfPercents <= 0.20)
                    h[4] = 1;

                if (lfPercents / totalFPercents <= 0.20 &&
                    hfPercents >= 0.40)
                    h[4] = -2;

                if (lfPercents / totalFPercents <= 0.40 &&
                    hfPercents >= 0.30)
                    h[4] = -1;

                var rsai = Math.abs(h[0]) +
                    Math.abs(h[1]) +
                    Math.abs(h[2]) +
                    Math.abs(h[3]) +
                    Math.abs(h[4]);
                var negatives = 0;
                for (var i = 0; i < 5; i++)
                    if (h[i] < 0)
                        negatives += Math.abs(h[i]);
                var negPercents = Math.floor(negatives / rsai * 100);
                return [rsai, negPercents];
            };
        }

        RSAI.prototype = new Evaluation();
        RSAI.prototype.constructor = RSAI;
        this.RSAI = RSAI;
    }
    Indicators._construct();
}
CardioMoodMath._construct();
