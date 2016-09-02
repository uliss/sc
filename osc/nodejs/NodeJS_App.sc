SP_AbstractApp {
    var <oscPath;
    var <httpPath;
    var <>onConnect;

    *new {
        arg oscPath, httpPath, syncOnConnect = false;
        ^super.newCopyArgs(oscPath, httpPath).init(syncOnConnect);
    }

    *hasSync {
        arg url;
        ^Library.at(\guido, \app, \sync, url.asSymbol).notNil;
    }

    *registerSync {
        arg name, func;
        NodeJS.sendMsg("/guido/module/server", "sync_add", name);

        Library.at(\guido, \app, \sync, name.asSymbol) !? ( _.free );
        Library.put(\guido, \app, \sync, name.asSymbol, func);
    }

    *unregisterSync {
        arg name;
        NodeJS.sendMsg("/guido/module/server", "sync_remove", name);
        Library.at(\guido, \app, \sync, name.asSymbol) !? ( _.free );
        Library.global.removeAt(\guido, \app, \sync, name.asSymbol);
    }

    name {
        ^oscPath.basename;
    }

    init {
        arg syncOnConnect;

        if(syncOnConnect) {
            var fn = OSCFunc({|msg|
                {
                    "[%:%] sync on connection".format(this.class, this.identityHash).postln;
                    if(onConnect.notNil) { onConnect.value };
                }.defer(2);
            }, "/sc/app/sync" +/+ httpPath, nil, NodeJS.outOscPort);

            SP_AbstractApp.registerSync(httpPath, fn);
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
        SP_AbstractApp.unregisterSync(httpPath);
    }

    title_ {
        arg pageTitle, windowTitle;
        if(windowTitle.notNil) { // set both
            NodeJS.send2Cli("/guido/module/client/broadcast", "title", pageTitle.urlEncode, windowTitle.urlEncode);
        } {
            NodeJS.send2Cli("/guido/module/client/broadcast", "title", pageTitle.urlEncode);
        };
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
        text = txt.urlEncode;
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
