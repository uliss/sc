GuidoLabelClockApp : GuidoLabelApp {
    var <time;
    var <>reverse;
    var timerRoutine;
    var timeMap;

    *new {
        arg initTime = 0, reverse = false, autoSync = false;
        ^super.new(autoSync: autoSync).initClock.time_(initTime).reverse_(reverse);
    }

    initClock {
        timeMap = Dictionary.new
    }

    schedAt {
        arg tm, func;
        timeMap[tm] = func;
        ^this;
    }

    time_ {
        arg seconds;
        time = seconds.asInteger;
        this.updateTime;
    }

    sync {
        this.updateTime;
    }

    updateTime {
        this.text_(time.asTimeString.drop(-4));

        if(timeMap.notNil && timeMap[time].notNil) {
            timeMap[time].value(time);
        }
    }

    start {
        this.stop;

        timerRoutine = Routine {
            inf.do {
                this.sync;
                if(reverse) { time = time - 1 } { time = time + 1 };
                1.wait;
            }
        };

        timerRoutine.play;
    }

    stop {
        if(timerRoutine.notNil) {
            timerRoutine.stop;
            timerRoutine.free;
            timerRoutine = nil;
        }
    }
}
