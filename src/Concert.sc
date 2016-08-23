SP_PieceApp : SP_AbstractApp {
    var <>title;
    var <>composer;
    var osc_play_control;
    var <playState;
    var <>onPlay;
    var <>onPause;
    var <>onStop;
    var <patches;
    var <widgets;

    *new {
        arg title, composer, oscPath, params = [];
        ^super.new(oscPath, "/piece", true).title_(title).composer_(composer).initPiece(params);
    }

    initPiece {
        arg params;

        playState = 0;
        patches = Dictionary.new;
        widgets = Dictionary.new;

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
        this.initPatches(params);
        this.initUI(params);
        this.initFinal(params);
        ^this;
    }

    isPlaying { ^ playState == 1 }
    isStopped { ^ playState == 0 }
    isPaused  { ^ playState == 2 }

    addPatch {
        arg name, instrumentList, params = ();
        var instr = instrumentList.collect({|i| Instr(i)}).reduce({|a,b| a <>> b});
        var patch = Patch(instr, params);
        patches[name] = patch;
    }

    removePatch {
        arg name;
        patches[name] = nil;
    }

    patch { |name| ^patches[name] }

    playPatches { patches.do { |p| p.play } }
    stopPatches { patches.do { |p| p.stop } }
    freePatches { patches.do { |p| p.free } }
    releasePatches { |t = 0.5| patches.do { |p| p.release(t) } }

    addWidget {
        arg name, widget;
        widgets[name] = widget;
    }

    widget { |name| ^widgets[name] }
    createWidgets { widgets.do { |w| w.add } }
    syncWidgets { widgets.do { |w| w.sync } }
    removeWidgets { widgets.do { |w| w.remove } }

    initUI {
        // "[%:initUI] implement me".format(this.class.name).warn;
    }

    initOSC {
        // "[%:initOSC] implement me".format(this.class.name).warn;
    }

    initMIDI {
        // "[%:initMIDI] implement me".format(this.class.name).warn;
    }

    initPatches {
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
        if(playState == 1) {
            "[%:play] already playing".format(this.class).warn;
            ^nil;
        };

        if(onPlay.notNil) { onPlay.value };
        playState = 1;
    }

    pause {
        if(playState == 2) {
            "[%:pause] already paused".format(this.class).warn;
            ^nil;
        };

        if(onPause.notNil) { onPause.value };
        playState = 2;
    }

    stop {
        if(playState == 0) {
            "[%:stop] already stopped".format(this.class).warn;
            ^nil;
        };

        if(onStop.notNil) { onStop.value };
        playState = 0;
    }

    free {
        this.stop;
        this.freePatches;
        this.patches = nil;
        this.removeWidgets;
        this.widgets = nil;
        osc_play_control.free;
    }

    sync {
        NodeJS.sendMsg("/node/title", title);
        this.syncWidgets;
    }
}
