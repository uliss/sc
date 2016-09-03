NodeJS {
    classvar <>connected;
    classvar <>outOscPort = 5001;
    classvar <inOscPort   = 5000;
    classvar <httpPort    = 3000;
    classvar <>nodeExec = "/usr/local/bin/node";
    classvar <>serverRoot;
    classvar <imageDirPrefix = "/img";
    classvar <thumbDirPrefix = "/img/thumb";
    classvar <soundDirPrefix = "/sound";
    classvar serverControl;
    classvar rehearsalUtils;
    classvar <connectionManager;
    classvar <>sendCallback;

    *initClass {
        this.connected = false;
        this.serverRoot = "~/work/music/nodejs/guidosc".standardizePath;
        ShutDown.add({
            if(this.connected == true) {
                "[GuidOSC] quit".postln;
                this.stop;
            }
        });
    }

    *htmlRootDir { ^serverRoot +/+ "build" }
    *imageDir { ^this.htmlRootDir +/+ imageDirPrefix }
    *thumbDir { ^this.htmlRootDir +/+ thumbDirPrefix }
    *soundDir { ^this.htmlRootDir +/+ soundDirPrefix }
    *testAddr { ^NetAddr("localhost", outOscPort) }

    *cmdString { |debugLevel = "verbose" |
        ^"STDOUT=1 DEBUG_LEVEL=% NOCOLOR=1 % %/index.js 2>&1 &".format(debugLevel, nodeExec, serverRoot) }

    *start {
        arg onBoot = {}, debugLevel;
        var res, cmd;

        if(connected) { ^true };

        try {
            NetAddr("localhost", httpPort).connect();
            connected = true;
            this.prInitControl;
        } {
            {
                var boot_cond = Condition.new;
                // check for root directory
                if(serverRoot.pathExists == false) {
                    Error("directory not exits: %".format(serverRoot.quote)).throw;
                };

                boot_cond.test = false;
                this.on("/guido/module/server", {
                    boot_cond.test = true;
                    boot_cond.signal;
                    onBoot.value;
                }).oneShot;
                this.cmdString(debugLevel).unixCmd;
                boot_cond.wait;
                "GuidoOSC started".postln;
                connected = true;
                this.prInitControl;
            }.fork;
        };
    }

    *prInitControl {
        if(serverControl.isNil) {
            var sendState;
            serverControl = SP_SupercolliderControl.new(NodeJS.outOscPort);
            serverControl.init(Server.default);

            sendState = {
                NodeJS.sendMsg("/guido/forward", "/guido/supercollider", "state", JSON.convert(serverControl.state));
            };

            serverControl.onMute = sendState;
            serverControl.onVolume = sendState;
            serverControl.onRecord = sendState;
            serverControl.onStateRequest = sendState;
            ServerBoot.add(sendState, \default);
            ServerQuit.add(sendState, \default);
            "[Guido] starting server control".postln;
        };

        if(rehearsalUtils.isNil) { rehearsalUtils = SP_RehearsalUtils.new };

        if(connectionManager.isNil) { connectionManager = GuidoConnectionManager.new };
    }

    *on {
        arg path, func;
        ^OSCFunc(func, path, nil, outOscPort);
    }

    *stop {
        NetAddr("localhost", NodeJS.inOscPort).sendMsg("/guido/module/server", "quit");
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
        arg onBoot = {}, debugLevel;
        {NodeJS.stop}.defer(0);
        {NodeJS.start(onBoot, debugLevel)}.defer(0.1);
    }

    *open {
        arg url = "";
        ("http://localhost:" ++  NodeJS.httpPort ++ "/" ++ url).openOS;
    }

    *sendMsg {
        arg path ... args;
        var n = NetAddr("localhost", NodeJS.inOscPort);

        if(connected) {
            n.sendMsg(path, *args);
            if(sendCallback.notNil) { sendCallback.value(path, *args) };
            ^true
        }
        { "NodeJS is not running".error; ^false; };
    }

    *send2Cli {
        arg path ... args;
        this.sendMsg("/guido/forward", path, *args);
    }

    *css {
        arg selector, key, value;
        if(key.isKindOf(Dictionary)) {
            this.sendMsg("/guido/module/client", "css", selector, JSON.convert(key));
        } {
            this.sendMsg("/guido/module/client", "css", selector, key, value);
        }
    }

    *redirect {
        arg path;
        this.sendMsg("/guido/module/client", "redirect", path);
    }

    *reload {
        this.sendMsg("/guido/module/client", "reload");
    }

    *ping {
        arg func;
        var id = 1000.rand;
        this.on("/guido/module/ping", {|m| if(id == m[2].asInteger) { func.value } }).oneShot;
        this.sendMsg("/guido/module/ping", "ping", id, ":back");
    }

    *modal {
        arg type, msg, title;
        this.sendMsg("/guido/module/client", "alert", type, title, msg);
    }

    *modalOk {
        arg msg, title = "Success";
        this.modal("ok", msg, title);
    }

    *modalError {
        arg msg, title = "Error";
        this.modal("error", msg, title);
    }

    *modalInfo {
        arg msg, title = "Information";
        this.modal("info", msg, title);
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
