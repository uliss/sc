NodeJS {
    classvar <>connected;
    classvar osc_funcs;
    classvar <>outOscPort = 5001;
    classvar <>sendCallback = nil;
    classvar <serverRootDir = "/Users/serj/work/music/nodejs/supercollider_ui";
    classvar <imageDirPrefix = "/img";
    classvar <thumbDirPrefix = "/img/thumb";
    classvar <soundDirPrefix = "/sound";
    classvar serverControl;
    classvar rehearsalUtils;

    *inOscPort { ^5000 }
    *httpPort { ^3000 }
    *lockPath { ^"/var/tmp/sc-node.lock" }
    *htmlRootDir { ^serverRootDir +/+ "build" }
    *imageDir { ^NodeJS.htmlRootDir +/+ imageDirPrefix }
    *thumbDir { ^NodeJS.htmlRootDir +/+ thumbDirPrefix }
    *soundDir { ^NodeJS.htmlRootDir +/+ soundDirPrefix }
    *testAddr { ^NetAddr("localhost", outOscPort) }

    *start {
        var dir, res, pid, cmd, node, withScControl = true;
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

        if(serverControl.isNil && withScControl == true) {
            var sendState;
            serverControl = SP_SupercolliderControl.new(NodeJS.outOscPort);
            serverControl.init(Server.default);

            sendState = {
                NodeJS.sendMsg("/node/supercollider", "state", JSON.convert(serverControl.state));
            };

            serverControl.onMute = sendState;
            serverControl.onVolume = sendState;
            serverControl.onRecord = sendState;
            serverControl.onStateRequest = sendState;
            ServerBoot.add(sendState, \default);
            ServerQuit.add(sendState, \default);
            "[NodeJS] starting server control".postln;
        };

        if(rehearsalUtils.isNil) { rehearsalUtils = SP_RehearsalUtils.new };

        ^true;
    }

    *stop {
        ("rm -f >/dev/null 2>&1" + NodeJS.lockPath).unixCmd(nil, false);
        "killall node index.js >/dev/null 2>&1".unixCmd(nil, false);
        connected = false;
        if(serverControl.notNil) {
            serverControl.stop;
        };
        serverControl = nil;
        rehearsalUtils.stop();
        rehearsalUtils = nil;
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
        arg path ... args;
        var n = NetAddr("localhost", NodeJS.inOscPort);

        if(connected.notNil && connected)
        {
            n.sendMsg(path, *args);
            if(sendCallback.notNil) {
                sendCallback.value(path, *args);
            };
            ^true
        }
        { "NodeJS is not running".error; ^false; };
    }

    *send2Cli {
        arg path ... args;
        NodeJS.sendMsg("/node/forward", "/cli" +/+ path, *args);
    }

    *css {
        arg selector, key, value;
        NodeJS.sendMsg("/node/css", selector, key, value);
    }

    *redirect {
        arg path;
        NodeJS.sendMsg("/node/redirect", path);
    }

    *reload {
        NodeJS.sendMsg("/node/reload");
    }

    *set {
        arg key, val;
        NodeJS.sendMsg("/node/set", key, val);
    }

    *get {
        arg key, func;
        NodeJS.sendMsg("/node/get", key);
        OSCFunc({|msg|
            func.value(msg[2]);
        }, "/sc/get", nil, NodeJS.outOscPort).oneShot;
    }

    *ping {
        NodeJS.sendMsg("/node/echo", "ping -> pong");
    }

    *verbose {
        arg v = true;
        var on = 0;
        if(v) {on = 1};
        NodeJS.sendMsg("/node/set", "verbose", on);
    }

    *modalOk {
        arg msg, title = "Success";
        NodeJS.sendMsg("/node/alert", "ok", title, msg);
    }

    *modalError {
        arg msg, title = "Error";
        NodeJS.sendMsg("/node/alert", "error", title, msg);
    }

    *modalInfo {
        arg msg, title = "Information";
        NodeJS.sendMsg("/node/alert", "info", title, msg);
    }

    *isRunning {
        ^ connected ? false;
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
