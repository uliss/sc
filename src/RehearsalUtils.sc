SP_RehearsalUtils {
    var tone_gen;
    var latency_test;

    *new {
        ^super.new.init;
    }

    init {
        tone_gen = NodeUtility_SinOsc.new;
        latency_test = NodeUtility_LatencyTest.new;
    }

    stop {
        tone_gen.unbindOsc;
        latency_test.unbindOsc;
    }
}

NodeUtility {
    var osc;
    var <oscPath;
    var is_osc_unique;

    *new {
        arg path;
        ^super.new.init(path);
    }

    init {
        arg path;
        oscPath = path;
        this.bindOsc;
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

