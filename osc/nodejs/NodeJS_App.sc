SP_AbstractApp {
    var <oscPath;
    var <httpPath;
    var <>onConnect;
    var osc_connect_sync;

    *new {
        arg oscPath, httpPath, syncOnConnect = false;
        ^super.new.init(oscPath, httpPath, syncOnConnect);
    }

    *registerSync {
        arg name;
        NodeJS.sendMsg("/node/app/sync/add", name);
    }

    *unregisterSync {
        arg name;
        NodeJS.sendMsg("/node/app/sync/remove", name);
    }

    name {
        ^oscPath.basename;
    }

    init {
        arg osc_path, http_path, syncOnConnect;
        oscPath = osc_path;
        httpPath = http_path;

        if(syncOnConnect) {
            SP_AbstractApp.registerSync(httpPath);

            osc_connect_sync = OSCFunc({|msg|
                {
                    if(onConnect.notNil) { onConnect.value };
                }.defer(1);
            }, "/sc/app/sync" +/+ httpPath, nil, NodeJS.outOscPort)
        }
    }

    open {
        ("http://localhost:" ++  NodeJS.httpPort ++ httpPath).openOS;
    }

    sendMsg {
        arg path ... args;
        NodeJS.send2Cli(oscPath ++ path, *args);
    }

    sync {}

    css {
        arg k, v;
        this.sendMsg("/css", k, v);
    }

    free {
        SP_AbstractApp.unregisterSync(httpPath)
    }
}

SP_AppLabel : SP_AbstractApp {
    var <color;
    var <backgroundColor;
    var <text;

    *new {
        arg txt = "DEFAULT", autoSync = true;
        ^super.new("/vlabel", "/vlabel", autoSync).text_(txt).color_("black").backgroundColor_("transparent")
    }

    text_ {
        arg txt;
        text = txt;
        this.sendMsg("/set", text);
    }

    sync {
        this.sendMsg("/set", text);
    }

    color_ {
        arg c = "#000000";
        color = c;
        this.css("color", color);
    }

    backgroundColor_ {
        arg c = "#FFFFFF";
        if(c.isNil) { c = "transparent" };
        backgroundColor = c;
        this.css("background-color", backgroundColor);
    }

    blink {
        arg ms = 100, c = "#FF0000";
        var prev_color = backgroundColor;

        this.backgroundColor_(c);

        {
            this.backgroundColor_(prev_color)
        }.defer(ms / 1000);
    }

    invert {
        var bgcolor = backgroundColor;
        this.backgroundColor_(color);
        this.color_(bgcolor);
    }
}

SP_AppLabelClock : SP_AppLabel {
    var <time;
    var <>reverse;
    var timerRoutine;
    var timeMap;

    *new {
        arg initTime = 0, reverse = false;
        ^super.new(autoSync: false).initClock.time_(initTime).reverse_(reverse);
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
