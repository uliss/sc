GuidoPieceApp : GuidoAbstractApp {
    classvar <>dir;
    var <>title;
    var <>composer;
    var osc_play_control;
    var <playState;
    var <>onPlay;
    var <>onPause;
    var <>onStop;
    var <patches;
    var <widgets;
    var <bindings;
    var <monitor;
    var <>phonesChannel;
    var <>onTimer;
    var timerTask;
    var currentTime;
    var <taskDict;

    *initClass { dir = "~/.config/sc".standardizePath }

    *new {
        arg title, composer, oscPath, params = [];
        var instance = Library.at(\piece, composer.asSymbol, title.asSymbol);

        if(instance.notNil) { ^instance };

        instance = super.new(oscPath, "/piece", true).title_(title).composer_(composer).initPiece(params);
        Library.put(\piece, composer.asSymbol, title.asSymbol, instance);
        ^instance;
    }

    addMonitorWidget {
        var box, toggle, slider;
        box = NodeJS_VBox.new.title_("monitor").hidden_(true).borderColor_("#AAA").align_("center").titleIcon_("headphones");
        this.addWidget(\monitorBox, box);

        toggle = NodeJS_Toggle.new(0).hidden_(true).label_("on").labelSize_(16).size_(40).layout_(box);
        toggle.onValue = { |v|
            if(v > 0) {
                this.startMonitor(widgets[\monitorAmp].value);
                "[%] monitor ON".format(this.class).postln;
            } {
                this.stopMonitor;
                "[%] monitor OFF".format(this.class).postln;
            }
        };
        this.addWidget(\monitorToggle, toggle);

        slider = NodeJS_Slider.new(0, 0, 1, 150).label_("amp").labelSize_(20).hidden_(true).layout_(box);
        slider.onValue = { |v| monitor.vol = v };
        this.addWidget(\monitorAmp, slider);
    }

    startMonitor {
        arg amp = 1;
        monitor.play(0, 2, phonesChannel, 2, volume: amp, fadeTime: 0.5);
        widgets[\monitorToggle].value = 1;
        widgets[\monitorAmp].value = amp;
    }

    stopMonitor {
        monitor.stop;
        widgets[\monitorToggle].value = 0;
    }

    initPiece {
        arg params;

        playState = 0;
        patches = Dictionary.new;
        widgets = Dictionary.new;
        bindings = Dictionary.new;
        monitor = Monitor.new;
        phonesChannel = 4;
        currentTime = 0;
        timerTask = Task {
            loop {
                if(onTimer.notNil) { onTimer.value(currentTime) };
                this.runTasks;
                1.wait;
                currentTime = currentTime + 1;
            }
        };
        taskDict = Dictionary.new;

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
        var instr, patch;

        if(name.isNil) {
            "[%] invalid patch name: %".format(thisMethod, name).error;
            ^nil
        };

        name = name.asSymbol;

        if(instrumentList.isNil || (instrumentList !? (_.isEmpty))) {
            "[%] instrument list is empty".format(thisMethod).error;
            ^nil
        };

        instr = instrumentList.collect({|i| Instr(i)}).reduce({|a,b| a <>> b});
        patch = Patch(instr, params);
        patches[name] = patch;
    }

    removePatch {
        arg name;
        patches[name.asSymbol] = nil;
    }

    patch { |name| ^patches[name.asSymbol] }

    syncPatchesParams {
        widgets.keysValuesDo { |name, widget|
            var patchBind = this.findBindedPatch(name.asSymbol);
            if(patchBind.notNil) {
                this.set(patchBind.key.asSymbol, patchBind.value.asSymbol, widget.value);
            }
        }
    }

    playPatches { patches.do { |p| p.play } }
    stopPatches { patches.do { |p| p.stop } }
    freePatches { patches.do { |p| p.free }; patches = nil; }
    releasePatches { |t = 0.5| patches.do { |p| p.release(t) } }

    addWidget {
        arg name, widget;
        widgets[name.asSymbol] = widget;
    }

    widget { |name| ^widgets[name.asSymbol] }

    removeWidget {
        arg name;
        name = name.asSymbol;
        widgets[name].remove;
        widgets[name].free;
        widgets[name] = nil;
    }

    createWidgets { widgets.do { |w| w.add } }

    syncWidgets { widgets.do { |w| w.sync } }

    removeWidgets {
        widgets.do { |w| w.remove; w.free };
        widgets = nil;
    }

    syncTitle {
        NodeJS.sendMsg("/guido/module/client", "title", title);
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

        if(this.isStopped) { timerTask.start };
        if(this.isPaused)  { timerTask.resume };

        if(onPlay.notNil) { onPlay.value };
        playState = 1;
    }

    pause {
        if(playState == 2) {
            "[%:pause] already paused".format(this.class).warn;
            ^nil;
        };

        if(this.isPlaying) { timerTask.pause };

        if(onPause.notNil) { onPause.value };
        playState = 2;
    }

    stop {
        if(playState == 0) {
            "[%:stop] already stopped".format(this.class).warn;
            ^nil;
        };

        timerTask.stop;
        timerTask.reset;
        currentTime = 0;

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

        w.onValue = { |v| patches[pName].set(controlName.asSymbol, v) };

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

        File.mkdir(GuidoPieceApp.dir);
        fname = GuidoPieceApp.dir +/+ (composer + title).replaceSpaces;
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

        fname = GuidoPieceApp.dir +/+ (composer + title).replaceSpaces;
        if(version.notNil) {
            fname = fname ++ "_" ++ version ++ ".params";
            if(File.exists(fname).not) {
                "[%] params not found: %".format(this.class, fname.quote).warn;
                ^Dictionary.new;
            }
        } {
            var pattern = fname ++ "*.params";
            fname = pattern.pathMatch.first;
            if(fname.isNil) {
                "[%] params not found: %".format(this.class, pattern.quote).warn;
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
        this.removeWidgets;
        Library.put(\piece, composer.asSymbol, title.asSymbol, nil);
        timerTask.free;
        bindings = nil;
        osc_play_control.free;
    }

    sync {
        super.sync;
        this.syncTitle;
        this.syncWidgets;
    }

    addTask {
        arg time, func;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        if(taskDict[time].isNil) { taskDict[time] = List.new(2) };
        taskDict[time].add(func);
    }

    addReplaceTask {
        arg time, func;
        this.removeTask(time);
        this.addTask(time, func);
    }

    removeTask {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        taskDict[time] = nil;
    }

    hasTask {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        ^taskDict[time].notNil;
    }

    runTasks {
        var task_list = taskDict[currentTime];
        if(task_list.notNil) {
            task_list.do { |f| f.value(currentTime) }
        }
    }

    clearAllTasks {
        taskDict = Dictionary.new;
    }
}
