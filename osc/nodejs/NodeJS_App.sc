SP_AbstractApp {
    var <oscPath;
    var <httpPath;

    *new {
        arg oscPath, httpPath;
        ^super.new.init(oscPath, httpPath);
    }

    init {
        arg osc_path, http_path;
        oscPath = osc_path;
        httpPath = http_path;
    }

    open {
        ("http://localhost:" ++  NodeJS.httpPort ++ httpPath).openOS;
    }

    sendMsg {
        arg path ... args;
        NodeJS.send2Cli(oscPath ++ path, *args);
    }

    sync {

    }

    css {
        arg k, v;
        this.sendMsg("/css", k, v);
    }
}

SP_AppLabel : SP_AbstractApp {
    var <color;
    var <backgroundColor;
    var <text;

    *new {
        arg txt = "DEFAULT";
        ^super.new("/vlabel", "/vlabel").text_(txt).color_("black").backgroundColor_("transparent")
    }

    text_ {
        arg txt;
        text = txt;
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

    *new {
        arg initTime = 0, reverse = false;
        ^super.new().time_(initTime).reverse_(reverse);
    }

    time_ {
        arg seconds;
        time = seconds.asInteger;
        this.sync;
    }

    sync {
        this.text_(time.asTimeString.drop(-4));
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
