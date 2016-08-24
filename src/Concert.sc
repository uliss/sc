SP_PieceApp : SP_AbstractApp {
    classvar <>dir;
    classvar <app_pieces;
    var <>title;
    var <>composer;
    var osc_play_control;
    var <playState;
    var <>onPlay;
    var <>onPause;
    var <>onStop;
    var <patches;
    var <widgets;
    var widget_name_list;
    var <bindings;

    *initClass {
        dir = "~/.config/sc".standardizePath;
        app_pieces = Dictionary.new;
    }

    *new {
        arg title, composer, oscPath, params = [];
        var key = title + composer;
        var piece;
        if(app_pieces[key].notNil) {
            ^app_pieces[key]
        } {
            var p = super.new(oscPath, "/piece", true).title_(title).composer_(composer).initPiece(params);
            app_pieces[key] = p;
            ^p;
        };
    }

    initPiece {
        arg params;

        playState = 0;
        patches = Dictionary.new;
        widgets = Dictionary.new;
        bindings = Dictionary.new;
        widget_name_list = List.new;

        osc_play_control = OSCFunc({ |msg|
            switch(msg[1].asString,
                "play", { this.play },
                "pause", { this.pause },
                "stop", { this.stop },
                "command", { this.command(msg[2..]) },
                "load", {this.loadParams },
                "save", {this.saveParams },
                {
                    "[%] unknown message: %".format(this.class.name, msg).warn;
                }
            );
        }, oscPath, nil, NodeJS.outOscPort);

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
    freePatches { patches.do { |p| p.free }; patches = nil; }
    releasePatches { |t = 0.5| patches.do { |p| p.release(t) } }

    addWidget {
        arg name, widget;
        widgets[name] = widget;
        widget_name_list.add(name);
    }

    widget { |name| ^widgets[name] }
    createWidgets {
        widget_name_list.do { |name|
            widgets[name].add;
        }
    }
    syncWidgets {
        widget_name_list.do { |name|
            widgets[name].sync;
        }
    }
    removeWidgets {
        widget_name_list = [];
        widgets.do { |w| w.remove };
        widgets = nil;
    }
    freeWidgets {
        widget_name_list = [];
        widgets.do { |w| w.free };
        widgets = nil;
    }

    syncTitle {
        NodeJS.sendMsg("/node/title", title);
    }

    add {
        NodeJS.send2Cli("/app/piece/set_osc_path", oscPath);
        this.syncTitle;
        this.createWidgets;
    }

    initUI {
        // "[%:initUI] implement me".format(this.class.name).warn;
    }

    initOSC {
        onConnect = { this.add };
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

    bindW2P { // bind widget to patch
        arg wName, pName, controlName;
        var w, p, idx, keyName;
        w = widgets[wName];
        p = patches[pName];

        if(p.isNil || w.isNil) {
            "[%:bindWidgetToPatch] invalid object names: %, %".format(this.class, wName, pName).warn;
            ^nil;
        };

        w.onValue = { |v| p.set(controlName.asSymbol, v) };

        // set initial values from patch values
        idx = p.argNames.indexOf(controlName.asSymbol);
        if(p.args[idx].class == KrNumberEditor) {
            w.value = p.args[idx].value;
        };

        keyName = pName.asString + controlName.asString;
        bindings[keyName] = wName.asSymbol;
    }

    findBindedWidget {
        arg patch, param;
        var keyName = patch.asString + param.asString;
        ^bindings[keyName];
    }

    findBindedPatch {
        arg widget;
        bindings.keysValuesDo { |k, v|
            if(widget.asSymbol == v) { ^k.split($ ).asAssociations.first };
        };

        ^nil;
    }

    params {
        var params = Dictionary.new;
        patches.keysValuesDo { |n, p|
            var args = p.storeArgs[1];
            var dict = Dictionary.new;
            args.do { |control, idx|
                var name = p.argNames[idx];
                var value;

                if(name.isNil) { ^nil };

                switch(control.class,
                    String, { dict[name] = control },
                    KrNumberEditor, { dict[name] = control.value },
                    IntegerEditor, { dict[name] = control.value },
                    { "[%] unsupported control type: %".format(this.class, control.class).postln }
                );
            };
            params[n] = dict;
        };
        ^params;
    }

    printParams {
        "% synth params:".format(composer + title).postln;

        this.params.keysValuesDo { |instr, p|
            "    %:".format(instr.asString.quote).postln;
            p.keysValuesDo {|k, v|
                "        % = %".format(k.asString.padRight(14), v).postln;
            }
        }
    }

    saveParams {
        arg version = nil;
        var file, fname, dir;
        var params = this.params;

        File.mkdir(SP_PieceApp.dir);
        fname = SP_PieceApp.dir +/+ (composer + title).replaceSpaces;
        if(version.notNil) { fname = fname ++ "_" ++ version};
        fname = fname ++ ".params";

        if(File.exists(fname)) {
            "[%] file exists: %".format(this.class, fname.quote).postln;
            "overwriting...".postln;
        };

        params.writeArchive(fname);
        ^params;
    }

    set {
        arg patchName, name, value;
        var p = patches[patchName];

        if(p.notNil) {
            if(p.argNamesForSynth.includes(name.asSymbol)) {
                var widgetName = this.findBindedWidget(patchName, name);
                p.set(name.asSymbol, value);
                // widget sync
                if(widgetName.notNil) {
                    var w = widgets[widgetName];
                    if(w.isNil) {
                        "[%] invalid widget name in binding: %".format(this.class, widgetName).warn;
                        ^nil;
                    };

                    w.value = value;
                };

            }
            {
                "[%] unknown arg for patch: % -> %".format(this.class, patchName, name).warn;
            }
        } {
            "[%] no patch with name: %".format(this.class, patchName).warn;
        }
    }

    loadParamsDict {
        arg version = nil;
        var fname;

        fname = SP_PieceApp.dir +/+ (composer + title).replaceSpaces;
        if(version.notNil) {
            fname = fname ++ "_" ++ version ++ ".params";
            if(File.exists(fname).not) {
                "[%] params not found: %".format(this.class, fname.quote).warn;
                ^Dictionary.new;
            }
        } {
            fname = fname ++ "*.params";
            if(fname.pathMatch.first.isNil) {
                "[%] params not found: %".format(this.class, fname.quote).warn;
                ^Dictionary.new;
            }
        };

        "[%] reading params from: %".format(this.class, fname.quote).postln;
        ^Object.readArchive(fname);
    }

    loadParams {
        arg version = nil;
        var dict = this.loadParamsDict(version);
        dict.keysValuesDo { |name, args|
            var p = patches[name];
            if(p.notNil) {
                args.keysValuesDo { |k, v|
                    this.set(name, k, v);
                }
            } {
                "[%] no patch with name: %".format(this.class, name).warn;
            }
        }
    }

    free {
        super.free;
        this.stop;
        this.freePatches;
        this.freeWidgets;
        bindings = nil;
        osc_play_control.free;
    }

    sync {
        this.syncTitle;
        this.syncWidgets;
    }
}

SP_SheetMusicPiece : SP_PieceApp {
    *new {
        arg title, composer, oscPath, params = [];
        ^super.new;
    }
}
