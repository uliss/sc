SP_RehearsalUtils {
    var tone_gen;
    var latency_test;
    var instr_manager;

    *new {
        ^super.new.init;
    }

    init {
        tone_gen = NodeUtility_SinOsc.new;
        latency_test = NodeUtility_LatencyTest.new;
        instr_manager = NodeUtility_PlayerManager.new;
    }

    stop {
        tone_gen.unbindOscAll;
        latency_test.unbindOscAll;
        instr_manager.unbindOscAll;
    }
}

NodeUtility {
    var <osc;
    var <oscPath;
    var is_osc_unique;

    *new {
        arg path;
        ^super.new.init(path);
    }

    init {
        arg path;
        oscPath = path;
        if(path.notNil) { this.bindOsc };
    }

    processOsc {
        arg msg;
        "[%] osc message: %".format(this.class, msg).postln;
    }

    bindOsc {
        if(osc.notNil) { osc.free; };

        try {
            osc = OSCFunc({
                arg msg;
                this.processOsc(msg);
            }, oscPath, nil, NodeJS.outOscPort);
            osc.permanent = true;
            this.addDependant(osc);
        } { |err|
            err.what.postln;
        }
    }

    unbindOsc {
        osc.free;
        osc = nil;
    }

    unbindOscAll {
        AbstractResponderFunc.allEnabled.do { |f|
            f.do{|osc_f|
                if(osc_f.class == OSCFunc) {
                    if(osc_f.path.asString == oscPath) {
                        osc_f.disable;
                        osc_f.permanent = false;
                        osc_f.free;
                    }
                }
            }
        }
    }
}

NodeUtility_LatencyTest : NodeUtility {
    *new {
        ^super.new("/sc/utils/latency");
    }

    processOsc {
        arg msg;
        NodeJS.send2Cli("/cli/utils/latency", msg[1]);
    }
}

NodeUtility_Synth : NodeUtility {
    var synth_func;
    var <>synth;

    *new {
        arg func, oscPath;
        ^super.new(oscPath).initSynth(func);
    }

    initSynth {
        arg func;
        synth_func = func;
    }

    play {
        arg ... params;

        if(synth.notNil && synth.isPlaying) {
            synth.set(*params);
            ^this;
        };

        synth = synth_func.play(args: params);
        NodeWatcher.register(synth, true);
    }

    processOsc {
        arg msg;
        switch(msg[1].asString,
            "play", { this.play(*msg[2..]) },
            "stop", { this.stop(*msg[2..]) },
            "set",  { this.set(*msg[2..]) },
            { "[%] unknown message format: %".format(this.class, msg).postln }
        )
    }

    set {
        arg ... params;
        synth.set(*params);
    }

    stop {
        synth.free;
        synth = nil;
    }
}

NodeUtility_Player : NodeUtility {
    var <>instr;
    var <>attackTime;
    var <>releaseTime;
    var <>player;
    var osc;
    var init_params;

    *new {
        arg instr, oscPath, attackTime = 0.1, releaseTime = 0.1;
        var p = super.new(oscPath);
        p.attackTime = attackTime;
        p.releaseTime = releaseTime;
        p.initPlayer(instr);
        ^p;
    }

    initPlayer {
        arg instrument ... params;
        var params_e = ();
        instr = instrument;

        if(params.size > 0) { params_e = Event.newFrom(params).collect({|v| v.asFloat }); };

        if(params_e.attackTime.isNil) { params_e.attackTime = attackTime; };

        init_params = params_e;
        init_params.postln;
    }

    play {
        arg ... args;
        player = Patch(instr, init_params);
        this.set(\fadeTime, releaseTime);
        this.set(*args);
        player.play;
    }

    stop {
        player.stop;
    }

    release {
        player.release(releaseTime);
    }

    set {
        arg ... args;
        var args_e = Event.newFrom(args);
        // args_e.postln;

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

    gui {
        player.gui;
    }
}

NodeUtility_PlayerManager : NodeUtility {
    var players;

    *new {
        arg oscPath = "/sc/utils/instr";
        ^super.new(oscPath).initManager;
    }

    initManager {
        players = Dictionary.new;
    }

    processOsc {
        arg msg;
        var instr_name = msg[1].asString;
        var action = msg[2].asString;
        var player = players[instr_name];

        if(player.isNil) { // add new elements
            var instr = Instr(instr_name);
            if(instr.isNil) {
                "[%] invalid instrument name: %".format(this.class, instr_name).warn;
                ^nil;
            };

            players[instr_name] = NodeUtility_Player.new(instr);
            player = players[instr_name];
        };

        switch(action,
            "init", { player.initPlayer(instr_name, *msg[3..]) },
            "play", { player.play(*msg[3..]) },
            "stop", { player.stop },
            "release", { player.release },
            "set", {  player.set(*msg[3..]) },
            "gui", {  player.gui },
            { "[%] unknown message format: %".format(this.class, msg).postln }
        )
    }

    player { |name| ^players[name] }

    playerNames { ^players.keys }

    stopAll {
        players.do { |p| p.stop }
    }

    releaseAll {
        players.do { |p| p.release }
    }
}

NodeUtility_SinOsc : NodeUtility_Synth {
    *new {
        arg oscPath = "/sc/utils/osc";
        var f = { |freq = 440, amp = 1| SinOsc.ar(freq, 0, amp)};
        ^super.new(f, oscPath);
    }

    play {
        arg freq;
        this.superPerform(\play, \freq, freq.asFloat);
    }

    freq_ {
        arg freq;
        this.set(\freq, freq);
    }
}

