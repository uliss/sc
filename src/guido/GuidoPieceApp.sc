GuidoPieceApp : GuidoAbstractApp {
    classvar <>dir;
    var <>title;
    var <>composer;
    var <playState;
    var <>onPlay;
    var <>onPause;
    var <>onStop;
    var <patches;
    var <widgets;
    var <widgetsOrderList;
    var <bindings;
    var <monitor;
    var <>phonesChannel;
    var <>taskRunner;

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
        arg hidden = true;
        var box, toggle, slider;
        box = NodeJS_VBox.new.title_("monitor").hidden_(hidden).borderColor_("#AAA").align_("center").titleIcon_("headphones");
        this.addWidget(\monitorBox, box);

        toggle = NodeJS_Toggle.new(0).hidden_(hidden).label_("on").labelSize_(16).size_(40).layout_(box);
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

        slider = NodeJS_Slider.new(0, 0, 1, 150).label_("amp").labelSize_(20).hidden_(hidden).layout_(box);
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
        widgetsOrderList = List.new;
        bindings = Dictionary.new;
        monitor = Monitor.new;
        phonesChannel = 4;
        taskRunner = SP_TaskRunner.new;

        this.initOSC(params);
        this.initMIDI(params);
        this.initPatches(params);
        this.initUI(params);
        this.initFinal(params);

        this.addFunction(\play, { this.play });
        this.addFunction(\pause, { this.pause });
        this.addFunction(\stop, { this.stop });
        this.addFunction(\command, { arg ... args; this.command(args) });
        this.addFunction(\load, { this.loadParams });
        this.addFunction(\save, { this.saveParams });

        ^this;
    }

    isPlaying { ^ taskRunner.isPlaying }
    isStopped { ^ taskRunner.isStopped }
    isPaused  { ^ taskRunner.isPaused }

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
        this.removeAllPatchBindings(name);
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

    widget { |name| ^widgets[name.asSymbol] }

    addWidget {
        arg name, widget;
        name = name.asSymbol;
        widgets[name] = widget;
        widgetsOrderList.add(name);
    }

    removeWidget {
        arg name;
        name = name.asSymbol;
        widgets[name].remove;
        widgets[name].free;
        widgets[name] = nil;
        this.removeWidgetBinding(name);
        widgetsOrderList.removeEvery([name]);
    }

    showWidgets { widgetsOrderList.do { |name| widgets[name].add } }
    hideWidgets { widgetsOrderList.do { |name| widgets[name].remove } }

    syncWidgets { widgetsOrderList.do { |name| widgets[name].sync } }

    removeWidgets {
        widgets.keys.do { |name| this.removeWidget(name) };
        widgets = nil;
        widgetsOrderList.clear;
    }

    syncTitle {
        NodeJS.sendMsg("/guido/module/client", "title", title);
    }

    add {
        NodeJS.send2Cli("/app/piece/set_osc_path", oscPath);
        this.syncTitle;
        this.showWidgets;
    }

    initUI {
        // "[%:initUI] implement me".format(this.class.name).warn;
    }

    initOSC {
        onConnect = { this.add }
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
        if(this.isPlaying) {
            "[%:play] already playing".format(this.class).warn;
            ^nil;
        };

        if(onPlay.notNil) { onPlay.value };
        taskRunner.play;
    }

    pause {
        if(this.isPaused) {
            "[%:pause] already paused".format(this.class).warn;
            ^nil;
        };

        if(onPause.notNil) { onPause.value };
        taskRunner.pause;
    }

    stop {
        if(this.isStopped) {
            "[%:stop] already stopped".format(this.class).warn;
            ^nil;
        };

        if(onStop.notNil) { onStop.value };
        taskRunner.stop;
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

    removeAllPatchBindings {
        arg patchName;
        bindings.keysValuesDo { |k, v|
            var pname = k.split($ ).first;
            if(pname == patchName.asString) {
                bindings[k] = nil;
            }
        }
    }

    removePatchBinding {
        arg patchName, controlName;
        var keyName = patchName.asString + controlName.asString;
        bindings[keyName] = nil;
    }

    removeWidgetBinding {
        arg widgetName;
        var p = this.findBindedPatch(widgetName);
        p !? { |name| this.removePatchBinding(name.key, name.value)};
    }

    hasPatchBinding {
        arg patchName, controlName;
        ^this.findBindedWidget(patchName, controlName).notNil;
    }

    hasWidgetBinding {
        arg wName;
        ^this.findBindedPatch(wName).notNil;
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

    appName { ^ (composer + title).replaceSpaces.toLower }

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
        fname = GuidoPieceApp.dir +/+ this.appName;
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

        fname = GuidoPieceApp.dir +/+ this.appName;
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
        if(this.isStopped.not) { this.stop };
        this.freePatches;
        this.removeWidgets;
        this.removeAllTasks;
        Library.put(\piece, composer.asSymbol, title.asSymbol, nil);
        bindings = nil;
    }

    *tasksDir {
        ^this.filenameSymbol.asString.dirname +/+ "tasks";
    }

    tasksFilename {
        arg version;
        var tr = { |str| str.tr($ , $_).toLower };

        if(version.isNil) {
            ^this.class.tasksDir +/+ this.appName ++ "_tasks.txt";
        } {
            ^this.class.tasksDir +/+ this.appName ++ "_tasks_%.txt".format(version);
        }
    }

    loadTasks {
        var res;
        var path = this.tasksFilename;
        "loading tasks from %".format(path).postln;
        res = taskRunner.load(path, { |name| this.function(name) } );
        if(res.isNil) {
            "loading failed: %".format(path).error;
            ^nil;
        }
    }

    saveTasks {
        arg force = false;

        var path = this.tasksFilename;

        if(path.pathExists !== false) {
            if(force) {
                taskRunner.save(path);
            } {
                "file already exists: %. use force=true, to overwrite".format(path).error;
            };
        } {
            taskRunner.save(path);
        }
    }

    addTask {
        arg time, func, name = \default ... args;
        taskRunner.addTask(time, func, name, args);
    }

    addFunctionTask {
        arg time, name ... args;
        if(this.hasFunction(name)) {
            taskRunner.addTask(time, this.function(name), name, args);
        };
    }

    hasTask {
        arg time;
        ^ taskRunner.hasTaskAt(time);
    }

    removeTask {
        arg time;
        taskRunner.removeTask(time);
    }

    currentTime_ {
        arg time;
        taskRunner.currentTime = time;
    }

    currentTime { ^taskRunner.currentTime }
    removeAllTasks { taskRunner.removeAllTasks }
}
