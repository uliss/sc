SP_InstrumentPlayer {
    var <>instr;
    var <initArgs;
    var <>player;

    *new {
        arg instr, attackTime = 0.1, releaseTime = 0.1;
        var p = super.new.init(instr);
        p.attackTime = attackTime;
        p.releaseTime = releaseTime;
        p.outChannel = nil;
        ^p;
    }

    attackTime { ^initArgs[\attackTime] }
    attackTime_ { |v| initArgs[\attackTime] = v }

    releaseTime { ^initArgs[\fadeTime] }
    releaseTime_ { |v| initArgs[\fadeTime] = v }

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
    }

    play {
        arg ... args;
        player = Patch(instr, initArgs);
        this.set(*args);
        player.play(bus:initArgs[\outChannel]);
    }

    stop {
        player.stop;
    }

    release {
        player.release(this.releaseTime);
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
}