AbstractScene {
    var <name;
    var <>routine;
    var <>task;
    var <>function;
    var <>oscFunction;
    var <>oscPath;
    var <>oscPort;
    var <>synth;
    var <>synthName;
    var <>synthParam;
    var <>debug;

    *new {
        arg name = "Abstract Scene", oscPath = "/ascene", oscPort = 7000;
        ^super.new.init(name, oscPath, oscPort);
    }

    init {
        arg name_, osc_path, osc_port;
        name = name_;
        oscPort = osc_port;
        oscPath = osc_path;
        debug = false;

        oscFunction = OSCFunc({|msg|
            // debug output
            if(debug) {msg.postln};

            switch(msg[1],
                \start,   { this.start(msg[2..]) },
                \stop,    { this.stop },
                \release, { this.release(msg[2]) },
                \pause,   { this.pause },
                \resume,  { this.resume },
                \reset,   { this.reset(msg[2..]) },
                \amp,     { this.amp(msg[2]) },
                \param,   { this.param(msg[2..]) },
                { format("[%] unknown message: '%'", this.class.name, msg).postln }
            );
        }, oscPath, nil, oscPort);

        oscFunction.permanent = true;
    }

    dbg { |msg|
        format("[%] %", name, msg).postln;
    }

    start {
        this.dbg("implement me");
    }

    stop {
        this.dbg("implement me");
    }

    pause {
        this.dbg("implement me");
    }

    resume {
        this.dbg("implement me");
    }

    release {
        this.dbg("implement me");
    }

    reset {
        this.dbg("implement me");
    }

    param {
        this.dbg("implement me");
    }

    amp {
        arg level;
        if(synth.notNil) {
            if(debug) { this.dbg("amp:" + level) };

            synth.set(\amp, level.asFloat);
        };
    }
}

SynthScene : AbstractScene {
    *new {
        arg name, oscPath = "/synth", oscPort = 7000, synthName = "default", synthParam = [];
        ^super.new(name, oscPath, oscPort).initSynth(synthName, synthParam);
    }

    initSynth {
        arg name, param = [];
        synthName = name;
        synthParam = param.asDict;
        synth = Synth.basicNew(synthName, Server.default);
    }

    start {
        arg ... args;
        if(debug) { this.dbg(format("start: %", args.flatten)) };
        Server.default.sendBundle(nil, synth.newMsg(nil, synthParam.asKeyValuePairs ++ args.asList.flatten));
        // synth.run(true);
        // synth.set(*args);
    }

    stop {
        if(debug) { this.dbg("stop") };
        synth.free;
    }

    pause {
        synth.run(false);
    }

    resume {
        synth.run(true);
    }

    release { |value|
        if(debug) { this.dbg("release: " + value) };
        synth.release(value);
    }

    synthSet {
        arg ... args;
        if(synth.notNil) {
            if(debug) {this.dbg(args)};

            synth.set(*args);
            args.keysValuesDo { |k, v|
                synthParam[k] = v;
            }
        }
    }
}

