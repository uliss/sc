NodeJS {
    classvar <>connected;
    classvar osc_funcs;

    *inOscPort { ^5000 }
    *outOscPort { ^5001 }
    *httpPort { ^3000 }
    *lockPath { ^"/var/tmp/sc-node.lock" }

    *start {
        var dir, res, pid, cmd, node;
        node = "/usr/local/bin/node";
        // check lock file
        res = ("lockfile -r 0" + NodeJS.lockPath).systemCmd;
        if(res != 0) {
            "nodejs server already running".error;
            ^nil;
        };

        dir = "~/work/music/nodejs/supercollider_ui".standardizePath;
        if(dir.pathExists == false) {
            "directory not exits: %".format(dir.quote).error;
            ^nil;
        };

        cmd = "% %/index.js &".format(node, dir);
        cmd.unixCmd;
        connected = true;
        ^true;
    }

    *stop {
        ("rm -f >/dev/null 2>&1" + NodeJS.lockPath).unixCmd(nil, false);
        "killall node index.js >/dev/null 2>&1".unixCmd(nil, false);
        connected = false;
        ^true;
    }

    *restart {
        {NodeJS.stop}.defer(0);
        {NodeJS.start}.defer(1);
    }

    *open {
        arg url = "";
        ("http://localhost:" ++  NodeJS.httpPort ++ "/" ++ url).openOS;
    }

    *sendMsg {
        arg addr ... args;
        var n = NetAddr("localhost", NodeJS.inOscPort);

        if(connected.notNil && connected) {
            n.sendMsg(addr, *args);
        }
        {
            var v;
            var osc_f = NodeJS.subscribe({|m| v = m[1]; NodeJS.connected = true; });
            n.sendMsg("/server/set", "ping", 1);
            {n.sendMsg("/server/get", "ping");}.defer(0.1);
            {
                if(connected.notNil && connected) {
                    n.sendMsg(addr, *args);
                }{
                    "NodeJS is not running".error;
                };
                osc_f.free;
            }.defer(1);
        };
    }

    *css {
        arg selector, key, value;
        NodeJS.sendMsg("/sc/css", selector, key, value);
    }

    *redirect {
        arg path;
        NodeJS.sendMsg("/sc/redirect", path);
    }

     *reload {
        NodeJS.sendMsg("/sc/reload");
    }

    *set {
        arg key, val;
        NodeJS.sendMsg("/server/set", key, val);
    }

    *get {
        arg key;
        NodeJS.sendMsg("/server/get", key);
    }

    *subscribe {
        arg func;
        if(func.notNil) {
            ^OSCFunc({|msg|
                func.value(msg.drop(1));
            }, "/server/get", nil, NodeJS.outOscPort);
        };
        ^nil;
    }

    *addWidget {
        arg type ... args;
        var widget = NodeJS_Widget.new(type, *args);
        NodeJS.sendMsg("/sc/addWidget", widget.asJSON);
        ^widget;
    }

    *verbose {
        arg v = true;
        var on = 0;
        if(v) {on = 1};
        NodeJS.sendMsg("/server/set", "verbose", on);
    }
}

NodeJS_Widget {
    classvar idx_count = 1;
    var <idx;
    var <>type;
    var <>params;
    var <>action;
    var osc;

    *new {
        arg type, params = [];
        ^super.new.init(type, params);
    }

    init {
        arg t, p = [];
        type = t;
        idx = idx_count;
        params = p;
        idx_count = idx_count + 1;
        osc = OSCFunc({|m|
            if(action.notNil) {
                var dict = m[1].asString.parseYAML;
                if(dict["idx"].asInteger == idx) {
                    action.value(dict);
                }
            }
        }, "/sc/button", nil, NodeJS.outOscPort);
    }

    set {
        arg k, v;
        params[k] = v;
    }

    asDict {
        var d = Dictionary.newFrom(params);
        d[\idx] = idx;
        d[\type] = type;
        ^d;
    }

    asJSON {
        ^JSON.toJSON(this.asDict);
    }
}

NodeJS_Label {
    classvar currentTime;
    classvar timerRoutine;
    classvar blinkMap;

    *path { ^"/sc/vlabel" }

    *set {
        arg text;
        NodeJS.sendMsg(NodeJS_Label.path ++ "/set", text);
    }

    *setTime {
        arg seconds, print = false;
        var txt = seconds.asTimeString.drop(-4);
        if(print) { txt.postln; };
        NodeJS_Label.set(txt);
    }

    *startClock {
        arg time = 0, print = false;
        currentTime = time;

        NodeJS_Label.stopClock;

        timerRoutine = Routine{
            inf.do {
                NodeJS_Label.setTime(currentTime, print);
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

    *css {
        arg k, v;
        NodeJS.sendMsg(NodeJS_Label.path ++ "/css", k, v);
    }

    *color {
        arg color = "#000000";
        NodeJS_Label.css("color", color);
    }

    *backgroundColor {
        arg color = "#FFFFFF";
        NodeJS_Label.css("background-color", color);
    }

    *blink {
        arg ms = 100, color = "#FF0000";
        NodeJS_Label.backgroundColor(color);
        {NodeJS_Label.backgroundColor}.defer(ms / 1000);
    }

    *open {
        ("http://localhost:" ++  NodeJS.httpPort ++ "/vlabel").openOS;
    }
}

NodeJS_Metronome {
    *path { ^"/sc/vmetro" }

    *backgroundColor {
        arg color = "#FFFFFF";
        NodeJS.css(".vmetro-area", "background-color", color);
    }

    *bar { |n|
        NodeJS.sendMsg(NodeJS_Metronome.path ++ "/bar", n);
    }

    *beat { |n, flash = 0|
        NodeJS.sendMsg(NodeJS_Metronome.path ++ "/beat", n, flash);
    }

    *css {
        arg k, v;
        NodeJS.sendMsg(NodeJS_Metronome.path ++ "/css", k, v);
    }

    *mark { |m|
        NodeJS.sendMsg(NodeJS_Metronome.path ++ "/mark", m);
    }

    *numBeats { |n|
        NodeJS.sendMsg(NodeJS_Metronome.path ++ "/numBeats", n);
    }
}

NodeJS_Tone {
    *new {
        ^super.new.init;
    }

    init {
        var s1, s2, s3, s4;

        NodeJS.redirect("/vlabel");
        SynthDef(\NodeJS_Tone, {
            arg freq, amp = 1;
            Out.ar([0,1], SinOsc.ar(freq, 0, amp) * EnvGate.new);
        }).send;


        {
            var w_a415, w_a430, w_a440, w_a442;
            NodeJS_Label.set("tone");
            w_a415 = NodeJS.addWidget(\button, [\x, "100px", \y, "100px", \label, "415hz", \style, "success", \act, "toggle"]);
            w_a430 = NodeJS.addWidget(\button, [\x, "200px", \y, "100px", \label, "430hz", \style, "success", \act, "toggle"]);
            w_a440 = NodeJS.addWidget(\button, [\x, "300px", \y, "100px", \label, "440hz", \style, "success", \act, "toggle"]);
            w_a442 = NodeJS.addWidget(\button, [\x, "400px", \y, "100px", \label, "442hz", \style, "success", \act, "toggle"]);


            w_a415.action = {|m| if(m["on"] == "true") {
                s1 = Synth(\NodeJS_Tone, [\freq, 415]);
            } {
                s1.release;
            }};

            w_a430.action = {|m| if(m["on"] == "true") {
                s2 = Synth(\NodeJS_Tone, [\freq, 430]);
            } {
                s2.release;
            }};

            w_a440.action = {|m| if(m["on"] == "true") {
                s3 = Synth(\NodeJS_Tone, [\freq, 440]);
            } {
                s3.release;
            }};

            w_a442.action = {|m| if(m["on"] == "true") {
                s4 = Synth(\NodeJS_Tone, [\freq, 442]);
            } {
                s4.release;
            }};
        }.defer(5);
    }
}
