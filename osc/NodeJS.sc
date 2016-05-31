NodeJS {
    classvar <>connected;
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
    }

    *stop {
        ("rm -f >/dev/null 2>&1" + NodeJS.lockPath).unixCmd(nil, false);
        "killall node index.js >/dev/null 2>&1".unixCmd(nil, false);
        connected = false;
    }

    *restart {
        {NodeJS.stop}.defer(0);
        {NodeJS.start}.defer(1);
    }

    *open {
        ("http://localhost:" ++  NodeJS.httpPort).openOS;
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
}

NodeJS_Label {
    classvar currentTime;
    classvar timerRoutine;

    *set {
        arg text;
        NodeJS.sendMsg("/sc/vlabel/set", text);
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
        }
    }

    *css {
        arg k, v;
        NodeJS.sendMsg("/sc/vlabel/css", k, v);
    }

    *open {
        ("http://localhost:" ++  NodeJS.httpPort ++ "/vlabel").openOS;
    }
}
