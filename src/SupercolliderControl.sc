SP_SupercolliderControl {
    classvar instance;
    var osc;
    var server;
    var <>onBoot;
    var <>onQuit;
    var <>onVolume;
    var <>onMute;
    var <>onRecord;
    var <>onRecordStop;
    var <lastRecPath;

    *new {
        arg oscPort, server = Server.default;
        if(instance.isNil) {
            instance = super.new.init(server, oscPort);
        };
        ^instance;
    }

    init {
        arg s, port;
        server = s;

        if(osc.isNil) {
            osc = OSCFunc({|m|
                {
                    var cmd = m[1].asString;
                    switch(cmd,
                        "boot", {
                            this.boot
                        },
                        "quit", {
                            this.quit
                        },
                        "reboot", {
                            this.reboot
                        },
                        "setVolume", {
                            this.volume_(m[2]);
                        },
                        "mute", {
                            var value = m[2] ? true;
                            this.mute(value.asBoolean);
                        },
                        "record", {
                            this.record
                        },
                        "stopRecord", {
                            this.stopRecord
                        },
                        {
                            "[%] unknown command: %".format(this.class, m[1..]).error;
                        }
                    );
                }.defer;
            }, "/sc/control", nil, port);
            osc.permanent = true;
        };

        ^this;
    }

    boot {
        if(server.serverRunning.not)
        {
            server.boot;
            if(onBoot.notNil) { onBoot.value }
        } {
            "[%] server already running".format(this.class).warn;
        }
    }

    reboot {
        if(server.serverRunning) {
            server.reboot;
            if(onQuit.notNil) { onQuit.value };
            if(onBoot.notNil) { onBoot.value }
        };
    }

    quit {
        if(server.serverRunning) {
            server.quit;
            if(onQuit.notNil) { onQuit.value }
        } {
            "[%] server is not running".format(this.class).warn;
        }
    }

    mute {
        arg value = true;
        if(server.serverRunning == true) {
            if(value) { server.mute } { server.unmute };
            // callback
            if(onMute.notNil) { onMute.value(value) };
            //
            "[%] mute = %".format(this.class, value).postln;
        } {
            "[%] server is not running".format(this.class).warn;
        }
    }

    volume_ {
        arg value;
        if(server.serverRunning == true) {
            var v = value ? 0;
            v = v.asInt;
            if(v.inRange(-60, 0)) {
                server.volume = v;
                // callback
                if(onVolume.notNil) { onVolume.value(v) };

                "[%] setVolume = %".format(this.class, v).postln;
            } {
                "[%] invalid volume value: %".format(this.class, v).warn;
            }
        } {
            "[%] server is not running".format(this.class).warn;
        }
    }

    findRecordPath {
        var path = NodeJS.soundDir +/+ Date.getDate.format("rec_%Y_%b_%d_[%H.%M]").toLower;
        if((path ++ ".wav").pathExists == false) {
            ^(path ++ ".wav");
        } {
            100.do {|n|
                var new_path = "%_%.wav".format(path, n.asString.padLeft(3, "0"));
                if(new_path.pathExists == false) { ^new_path };
            }
            ^(path ++ "_xxx.wav");
        };
    }

    record {
        server.waitForBoot({
            var path = this.findRecordPath;
            // check dir exists
            if(path.dirname.pathExists == false) { path.dirname.mkdir };
            server.recSampleFormat = "int16";
            server.recHeaderFormat = "WAV";
            server.recChannels = 2;
            server.prepareForRecord(path);
            server.sync;
            server.record;

            if(onRecord.notNil) { onRecord.value(path) };
            lastRecPath = path;
        });
    }

    stopRecord {
        server.stopRecording;
        if(onRecordStop.notNil) { onRecordStop.value(lastRecPath) };
    }

    stop {
        osc.free;
        osc = nil;
    }
}