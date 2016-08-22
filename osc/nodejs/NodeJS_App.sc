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
        path.postln;
        NodeJS.send2Cli(oscPath ++ path, *args);
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
        ^super.new("/vlabel", "/vlabel").text_(txt);
    }

    text_ {
        arg txt;
        text = txt;
        this.sendMsg("/set", text);
    }

    setTime {
        arg seconds, print = false;
        var txt = seconds.asTimeString.drop(-4);
        if(print) { txt.postln; };
        this.text_(txt);
    }

    color_ {
        arg c = "#000000";
        color = c;
        this.css("color", color);
    }

    backgroundColor_ {
        arg c = "#FFFFFF";
        backgroundColor = c;
        this.css("background-color", backgroundColor);
    }

    blink {
        arg ms = 100, c = "#FF0000";
        this.backgroundColor_(c);

        {
            this.backgroundColor_("transparent")
        }.defer(ms / 1000);
    }

/*    startClock {
        arg time = 0, print = false;
        currentTime = time;

        SP_App_Label.stopClock;

        timerRoutine = Routine{
            inf.do {
                SP_App_Label.setTime(currentTime, print);
                currentTime = currentTime + 1;
                1.wait;
            }
        };

        timerRoutine.play;
    }

    *stopClock {
        if(timerRoutine.notNil) {
            timerRoutine.stop;
            timerRoutine.free;
            timerRoutine = nil;
        }
    }
*/
}
