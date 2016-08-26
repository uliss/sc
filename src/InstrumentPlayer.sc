SP_InstrumentPlayer {
    var <>instr;
    var <initArgs;
    var <>player;
    var play_state;

    *new {
        arg instr;
        var p = super.new.init(instr);
        p.outChannel = nil;
        ^p;
    }

    outChannel { ^initArgs[\outChannel] }
    outChannel_ { |v| initArgs[\outChannel] = v }

    numChannels { ^player.numChannels }

    init {
        arg instrument;

        if(instrument.class == String) {
            instr = Instr(instrument)
        } {
            instr = instrument
        };

        initArgs = Event.new;
        player = Patch(instr, initArgs);
        player.respawnOnChange = 0.2;
        ^this;
    }

    setInitArg {
        arg key, value;
        initArgs[key] = value;
        player.argNames.do { |v, i|
            var a = initArgs[v];
            if(a.notNil) {
                player.setInput(i, a);
            };
        }
    }

    play {
        arg ... args;

        fork {
            if(play_state == 0 && player.isPlaying == true) {
                play_state = 1;
                0.5.wait;
            };

            this.set(*args);
            player.play(bus:initArgs[\outChannel]);
            play_state = 1;
        }
    }

    stop {
        fork {
            if(play_state == 1 && player.isPlaying == false) {
                play_state = 0;
                0.5.wait;
            };

            player.stop;
            play_state = 0;
        }
    }

    release {
        arg time;
        player.release(time)
    }

    set {
        arg ... args;
        var args_e = Event.newFrom(args);

        args_e.keysValuesDo { |key, value|
            try {
                if(player.argNames.includes(key.asSymbol)) {
                    player.set(key, value);
                    "set % = %".format(key, value).postln;
                } {
                    "[%] unknown player property: %".format(this.class, key).warn;
                    ^this;
                };
            } {
                "[%] no player".format(this.class, key).warn;
            }
        };
    }

    instrumentGui {
        instr.gui;
    }

    playerGui {
        player.gui;
    }

    dumpInfo {
        "[%] %:".format(this.class, instr).postln;
        "init args: %".format(initArgs).padLeft(4).postln;
        "instr args: %".format(player.argNames).padLeft(4).postln;
    }

    reload {
        Instr.load(instr.dotNotation);
        this.init(instr.dotNotation);
    }

    saveParams {
        var dir, fname, file;
        try {
            dir = Instr.dir +/+ "params";
            if(dir.pathExists === false) { dir.mkdir };
            fname = dir +/+ instr.dotNotation ++ ".params.txt";
            file = File.new(fname, "w");
            player.storeParamsOn(file);
            file.close;
            "[%] store params to file: %".format(this.class, fname.quote).postln;
            ^this;
        } { |error|
            "[%] error while saving to %".format(this.class, fname.quote).error;
            ^nil;
        }
    }
}
