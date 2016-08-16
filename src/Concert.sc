ConcertPiece {
    var <>title;
    var <>composer;
    var <>oscPath;
    var osc_play_control;

    *new {
        arg title, composer, oscPath, params = [];
        ^super.new.init(title, composer, oscPath, params);
    }

    init {
        arg title_, composer_, oscPath_, params;

        title = title_;
        composer = composer_;
        oscPath = oscPath_;

        osc_play_control = OSCFunc({ |msg|
            switch(msg[1].asString,
                "play", { this.play },
                "pause", { this.pause },
                "stop", { this.stop },
                "command", { this.command(msg[2..]) },
                {
                    "[%] unknown message: %".format(this.class.name, msg).warn;
                }
            );
        }, oscPath);

        this.initOSC(params);
        this.initMIDI(params);
        this.initUI(params);
        this.initSynths(params);
        this.initFinal(params);
    }

    initUI {
        // "[%:initUI] implement me".format(this.class.name).warn;
    }

    initOSC {
        // "[%:initOSC] implement me".format(this.class.name).warn;
    }

    initMIDI {
        // "[%:initMIDI] implement me".format(this.class.name).warn;
    }

    initSynths {
        // "[%:initSynth] implement me".format(this.class.name).warn;
    }

    initFinal {
        // "[%:initSynth] implement me".format(this.class.name).warn;
    }

    command {
        arg msg;
        "[%:command] implement me".format(this.class.name).warn;
    }

    play {
        "[%:play] implement me".format(this.class.name).warn;
    }

    pause {
        "[%:pause] implement me".format(this.class.name).warn;
    }

    stop {
        "[%:stop] implement me".format(this.class.name).warn;
    }

    free {
        osc_play_control.free;
    }
}