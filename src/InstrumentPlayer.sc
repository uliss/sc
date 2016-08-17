SP_InstrumentPlayer {
    var <>instr;
    var <initArgs;
    var <>player;

    *new {
        arg instr;
        var p = super.new.init(instr);
        p.outChannel = nil;
        ^p;
    }

    outChannel { ^initArgs[\outChannel] }
    outChannel_ { |v| initArgs[\outChannel] = v }

    init {
        arg instrument;
        instr = instrument;
        initArgs = Event.new;
        player = Patch(instr, initArgs);
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
        this.set(*args);
        player.play(bus:initArgs[\outChannel]);
    }

    stop {
        player.stop;
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
        Instr(instr).gui;
    }

    playerGui {
        player.gui;
    }

    dumpInfo {
        "[%] %:".format(this.class, instr).postln;
        "init args: %".format(initArgs).padLeft(4).postln;
        "instr args: %".format(player.argNames).padLeft(4).postln;
    }
}