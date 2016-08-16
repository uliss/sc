NodeJS_Widget {
    classvar idx_count = 1;
    var <idx;
    var <>type;
    var <>params;
    var <>action;
    var <>widgetAction;
    var onRemove;
    var added;
    var osc;
    var auto_add;

    *toUI {
        NodeJS.sendMsg("/sc/redirect", "/ui");
    }

    *new {
        arg type, params = [];
        ^super.new.init(type, params);
    }

    init {
        arg t, p = [];

        type = t;
        idx = idx_count;
        added = false;
        params = Dictionary.new;
        params[\idx] = type ++ idx;
        params[\parent] = "ui-elements";
        params = params ++ Dictionary.newFrom(p);
        params[\type] = type;
        params[\oscPath] = "/ui";

        idx_count = idx_count + 1;

        try {
            osc = OSCFunc({|m|
                params[\value] = m[1];
                if(action.notNil) {
                    action.value(m);
                };
                if(widgetAction.notNil) {
                    widgetAction.value(m);
                }
            }, "/sc/ui/" ++ this.id, nil, NodeJS.outOscPort);
        } { |error|
            error.what.error;
            "Seems that you should restart NodeJS: NodeJS.restart;".error;
        };
    }

    colors_ {
        arg border = "#FAA", fill = "#0F0", accent = "#F00";
        params[\colors] = [border, fill, accent];
    }

    add {
        added = true;
        this.sendMsg("/widget/add", this.asJSON);
    }

    remove {
        added = false;
        this.sendMsg("/widget/remove", this.id);
        if(onRemove.notNil) { onRemove.value };
    }

    update {
        this.sendMsg("/widget/update", this.asJSON);
        ^this;
    }

    commandGroup {
        arg msg;
        this.sendMsg("/widget/command", JSON.toJSON(msg));
    }

    command {
        arg name, value;
        var cmd = (idx: this.id);
        cmd[name.asSymbol] = value;
        this.sendMsg("/widget/command", JSON.toJSON(cmd));
    }

    sendMsg {
        arg path, msg;
        NodeJS.sendMsg("/node" ++ path, msg);
        ^this;
    }

    id {
        ^params[\idx];
    }

    value {
        ^params[\value];
    }

    value_ { |v|
        params[\value] = v;
        if(added) { this.update }
    }

    asJSON {
        ^JSON.toJSON(params);
    }

    label {
        ^params[\label];
    }

    label_ { |txt|
        params[\label] = txt;
    }

    css {
        arg k, v;
        NodeJS.css("#" ++ this.id, k, v);
    }

    autoAdd {
        arg value = true, interval = 10;
        if(value) {
            if(auto_add.isNil) {
                auto_add = SkipJack.new({this.add}, interval, false, "auto_add");
            }
            {
                auto_add.start;
            }
        }
        {
            if(auto_add.notNil) { auto_add.stop }
        }
    }

    startDemo {
        "[%] startDemo not implemented".format(this.class).warn;
    }

    stopDemo {
        "[%] stopDemo not implemented".format(this.class).warn;
    }
}

NodeJS_SuperCollider : NodeJS_Widget {
    var server;

    *new {
        arg server = Server.default;
        ^super.new("sc_button").initSC(server);
    }

    id {
        ^"sc_button";
    }

    initSC {
        arg s;
        server = s;

        params[\idx] = "sc_button";
        widgetAction = { |e|
            if(e[1].asInt == 1) {
                if(server.serverRunning.not) {
                    server.boot;
                };
                this.command(\state, 1);
            }
            {
                if(server.serverRunning) {
                    server.quit;
                };
                this.command(\state, 0);
            };
        };

        if(server.serverRunning) {
            this.command(\state, 1);
        }
    }
}

NodeJS_ValueWidget : NodeJS_Widget {
    var <>onValue;

    *new {
        arg type, value = 0.0, min = 0.0, max = 1.0, size = 100, label = "", params = [];
        ^super.new(type, [
            \size, size,
            \label, label,
            \min, min,
            \max, max,
            \value, value
        ] ++ params).initValue;
    }

    initValue {
        widgetAction = { |e|
            params[\value] = e[1].asFloat;
            if(onValue.notNil) {
                onValue.value(params[\value]);
            }
        }
    }
}

NodeJS_RotationSensor : NodeJS_Widget {
    var <x, <y, <z;
    var <>onX, <>onY, <>onZ, <>onRotate;

    *new {
        arg size = 100, label = "", params = [];
        ^super.new("tilt", [
            \size, size,
            \label, label,
        ] ++ params).initRotationSensor;
    }

    initRotationSensor {
        widgetAction = {
            arg msg;
            x = msg[1].asFloat;
            y = msg[2].asFloat;
            z = msg[3].asFloat;

            if(onX.notNil) { onX.value(x); };
            if(onY.notNil) { onY.value(y); };
            if(onZ.notNil) { onZ.value(z); };
            if(onRotate.notNil) { onRotate.value(x, y, z); };
        };
    }
}

NodeJS_MotionSensor : NodeJS_Widget {
    var <x, <y, <z;
    var <>onX, <>onY, <>onZ, <>onRotate;

    *new {
        arg size = 100, label = "", params = [];
        ^super.new("motion", [
            \size, size,
            \label, label,
        ] ++ params).initMotionSensor;
    }

    initMotionSensor {
        widgetAction = {
            arg msg;
            x = msg[1].asFloat;
            y = msg[2].asFloat;
            z = msg[3].asFloat;

            if(onX.notNil) { onX.value(x); };
            if(onY.notNil) { onY.value(y); };
            if(onZ.notNil) { onZ.value(z); };
            if(onRotate.notNil) { onRotate.value(x, y, z); };
        };
    }
}

NodeJS_Number : NodeJS_ValueWidget {
    *new {
        arg value = 0.0, min = 0, max = 1000, size = 120, step = 1, sensivity = 0.25, digits = 2, label = "", params = [];
        ^super.new("number", value, min, max, size, label, [
            \step, step,
            \rate, sensivity,
            \digits, digits
        ] ++ params);
    }
}

NodeJS_NumberFreq : NodeJS_Number {
    *new {
        arg freq = 1000, min = 20, max = 20000, size = 120, label = "freq";
        ^super.new(freq, min, max, size, 1, 2, 0, label);
    }
}

NodeJS_NumberAmp : NodeJS_Number {
    *new {
        arg amp = 0, min = 0, max = 1, size = 120, label = "amp";
        ^super.new(amp, min, max, size, 0.01, 0.15, 2, label);
    }
}

NodeJS_NumberMidi : NodeJS_Number {
    *new {
        arg note = 60, min = 36, max = 108, size = 120, label = "note";
        ^super.new(note, min, max, size, 1, 0.2, 0, label);
    }
}

NodeJS_Knob : NodeJS_ValueWidget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 100, label = "", params = [];
        ^super.new("knob", value, min, max, size, label, params);
    }
}

NodeJS_Pan : NodeJS_ValueWidget {
    *new {
        arg value = 0, size = 50, params = [];
        var p = super.new("pan", value, size: size, params: params);
        p.label = p.id;
        ^p;
    }
}

NodeJS_Slider : NodeJS_ValueWidget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 180, label = "", horizontal = 0, relative = 0, params = [];
        var p = super.new("slider", value, min, max, size, label, [
            \horizontal, horizontal,
            \relative, relative] ++ params);
        ^p;
    }
}

NodeJS_Toggle : NodeJS_ValueWidget {
    *new {
        arg value = 0, size = 100, label = "", params = [];
        ^super.new("toggle", value, size: size, label: label, params: params);
    }
}

NodeJS_Button : NodeJS_Widget {
    var <>onClick;
    var <>onPress;
    var <>onRelease;

    *new {
        arg size = 100, label = "", params = [];
        ^super.new("button", [\size, size, \label, label] ++ params).initButton;
    }

    initButton {
        widgetAction = { |msg|
            var state = msg[1].asInteger;

            if(onClick.notNil) { onClick.value(state) };
            if(state == 1 && onPress.notNil) { onPress.value };
            if(state == 0 && onRelease.notNil) { onRelease.value };
        };
    }

    startDemo {
        SynthDef(\NodeJS_Button_demo, {
            var snd = LPF.ar(Impulse.ar(0), 1000) ! 2;
            Out.ar(0, snd * EnvGate.new(fadeTime: 0));
        }).send;

        onPress = {
            (\instrument: \NodeJS_Button_demo, \note: 1, \sustain: 0.1).play;
        };
    }

    stopDemo {
        onPress = nil;
    }
}

NodeJS_TouchButton : NodeJS_Widget {
    var <x, <y;
    var <>onClick;
    var <>onPress;
    var <>onRelease;
    var <>onTouch;
    var demo_synth;

    *new {
        arg size = 100, label = "", params = [];
        ^super.new("touchbutton", [\size, size, \label, label] ++ params).initTouchButton;
    }

    initTouchButton {
        widgetAction = { |msg|
            var data_type = msg[1].asString;

            if(data_type == "pos") {
                x = msg[2].asFloat;
                y = msg[3].asFloat;

                if(onTouch.notNil) { onTouch.value(x, y) };
            };

            if(data_type == "press") {
                var state = msg[2].asInteger;
                if(onClick.notNil) { onClick.value(state) };
                if(state == 1 && onPress.notNil) { onPress.value };
                if(state == 0 && onRelease.notNil) { onRelease.value };
            };
        };
    }

    startDemo {
        {
            var name = \NodeJS_TouchButton_demo;
            SynthDef(name, {
                var snd = Pulse.ar(Vibrato.kr(440, \rate.kr(6, 0.1), \depth.kr(0.02, 0.1)));
                Out.ar(0, snd * EnvGate.new(fadeTime: 0.5));
            }).send;

            Server.default.sync;

            onPress = {
                demo_synth = Synth(name);
            };

            onRelease = {
                demo_synth.release;
            };

            onTouch = { |x, y|
                var rate = x.clip(0, 1).linlin(0, 1, 1, 20);
                var depth = y.clip(0, 1).linlin(0, 1, 0.001, 0.1);
                demo_synth.set(\rate, rate);
                demo_synth.set(\depth, depth);
            };
        }.fork;
    }

    stopDemo {
        onPress = nil;
        onTouch = nil;
    }
}

NodeJS_Pianoroll : NodeJS_Widget {
    var <>onNote;

    *new {
        arg size = 600, octaves = 3, midibase = 48, params = [];
        var p = super.new("pianoroll", [
            \size, size,
            \octaves, octaves,
            \midibase, midibase] ++ params).initPianoroll;
        ^p;
    }

    initPianoroll {
        widgetAction = { |msg|
            if(onNote.notNil) {
                onNote.value(msg[1].asInteger, msg[2].asInteger);
            }
        };
    }

    testSetup {
        onNote = {
            arg note, state;
            if(state != 0) {
                (\instrument: \default, \type: \on, \midinote: note).play;
            } {
                (\instrument: \default, \type: \off, \midinote: note).play;
            };
        };
    }
}

NodeJS_XFade : NodeJS_ValueWidget {
    *new {
        arg value = 0.0, min = -1.0, max = 1.0, size = 200, label = "", params = [];
        var p = super.new("crossfade", value, min, max, size, label, params);
        ^p;
    }
}

NodeJS_Matrix : NodeJS_Widget {
    var <row, <col;
    var <matrix;
    var <>onCell;

    *new {
        arg size = 200, row = 4, col = 4, label = "", params = [];
        var p = super.new("matrix", [
            \size, size,
            \row, row,
            \col, col,
            \label, label] ++ params).initMatrix(row, col);
        ^p;
    }

    initMatrix {
        arg r = 4, c = 4;
        row = r;
        col = c;
        matrix = Array.fill2D(row, col, 0);
        params[\matrix] = matrix;

        widgetAction = {
            arg msg;
            var row = msg[1].asInteger;
            var col = msg[2].asInteger;
            var state = msg[3].asInteger;
            if(onCell.notNil) {
                onCell.value(row, col, state);
            };

            matrix[row][col] = state;
            params[\matrix] = matrix;
        };
    }

    setCell {
        arg row, col, state = 1;
        matrix[row][col] = state;
        params[\matrix] = matrix;
        if(added) { this.update };
    }

    fill {
        arg v = 1;
        matrix = Array.fill2D(row, col, v);
        params[\matrix] = matrix;
        if(added) { this.update };
    }
}

NodeJS_Touchpad : NodeJS_Widget {
    var <x;
    var <y;
    var <>onMove;
    var <>onClick;
    var <>onRelease;

    *new {
        arg size = 200, label = "", params = [];
        var p = super.new("position", [
            \size, size,
            \label, label] ++ params);
        p.initTouchPad;
        ^p;
    }

    initTouchPad {
        widgetAction = {|msg|
            var state = msg[3].asString;
            x = msg[1].asFloat;
            y = msg[2].asFloat;

            if(state == "click" && onClick.notNil) { onClick.value(x, y) };
            if(state == "move" && onMove.notNil) { onMove.value(x, y) };
            if(state == "release" && onRelease.notNil) { onRelease.value(x, y) };
        };
    }
}

NodeJS_Multitouch : NodeJS_Widget {
    var <event;
    var <>onTouch0;
    var <>onTouch1;
    var <>onTouch2;
    var <>onTouch3;
    var <>onTouch4;

    *new {
        arg size = 400, label = "", params = [];
        var p = super.new("multitouch", [
            \size, size,
            \label, label] ++ params);
        p.initMultitouch;
        ^p;
    }

    initMultitouch {
        widgetAction = { |e|
            var t0, t1, t2, t3, t4;
            event = e[1].asString.parseYAML;
            t0 = event["touch0"];
            t1 = event["touch1"];
            t2 = event["touch2"];
            t3 = event["touch3"];
            t4 = event["touch4"];

            if(t0.notNil && onTouch0.notNil) { onTouch0.value(t0["x"], t0["y"]) };
            if(t1.notNil && onTouch1.notNil) { onTouch1.value(t1["x"], t1["y"]) };
            if(t2.notNil && onTouch2.notNil) { onTouch2.value(t2["x"], t2["y"]) };
            if(t3.notNil && onTouch3.notNil) { onTouch3.value(t3["x"], t3["y"]) };
            if(t4.notNil && onTouch4.notNil) { onTouch4.value(t4["x"], t4["y"]) };
        };
    }
}

NodeJS_MultitouchGrid : NodeJS_Multitouch {
    *new {
        arg size = 400, rows = 10, cols = 10, labels = (1..10), params = [];
        var p = super.new(size, params: [
            \size, size,
            \mode, "matrix",
            \rows, rows,
            \cols, cols,
            \matrixLabels, labels] ++ params);
        ^p;
    }
}

NodeJS_Playcontrol : NodeJS_Widget {
    var timerRoutine;
    var currentTime;
    var maxTime;
    var isPaused;
    var syncTime;
    var <>onPlay;
    var <>onStop;
    var <>onPause;
    var <>onPrev;
    var <>onNext;
    var <>onFirst;
    var <>onLast;
    var sections;
    var sectionTimes;
    var currentSection;
    var sound_file;
    var cue_params;
    var play_event;

    *new {
        arg showBack = true, showForward = true, showDisplay = true, syncTime = 10, params = [];
        var p = super.new("playcontrol", [
            \back, showBack,
            \forward, showForward,
            \display, showDisplay
        ] ++ params);
        ^p.initPlaycontrol(syncTime);
    }

    initPlaycontrol {
        arg sync_time = 10;

        onRemove = { this.stop };
        syncTime = sync_time;
        currentTime = 0;
        maxTime = 100000;
        isPaused = false;

        timerRoutine = Task {
            inf.do {
                if(currentTime.mod(syncTime) == 0) {
                    this.sync;
                };
                1.wait;
                currentTime = currentTime + 1;

                if(currentTime >= maxTime) { this.stop };
            }
        };

        widgetAction = { |msg|
            switch(msg[1].asString,
                "play", { this.play },
                "stop", { this.stop },
                "pause", { this.pause },
                "prev", { this.prevSection },
                "next", { this.nextSection },
                "first", { this.firstSection },
                "last", { this.lastSection },
                { "[%] unknown command: %".format(this.class, msg[1].asString).postln }
            );
        };

        sections = SortedList.new;
        sectionTimes = Dictionary.new;

        ^this;
    }

    firstSection {
        var first_section = sections.first;
        if(first_section.notNil) {
            currentSection = first_section;
            currentTime = sectionTimes[currentSection];
            this.part(currentSection);
            this.sync;
        };

        if(onFirst.notNil) { onFirst.value(currentSection) };
    }

    lastSection {
        var last_section = sections.last;
        if(last_section.notNil) {
            currentSection = last_section;
            currentTime = sectionTimes[currentSection];
            this.part(currentSection);
            this.sync;
        };

        if(onLast.notNil) { onLast.value(currentSection) };
    }

    nextSection {
        var idx = sections.indexOf(currentSection);
        if(idx.notNil) {
            var next_section = sections[idx + 1];
            if(next_section.notNil) {
                currentSection = next_section;
                currentTime = sectionTimes[currentSection];
                this.part(currentSection);
                this.sync;
            };
        };

        if(onNext.notNil) { onNext.value(currentSection) };
    }

    prevSection {
        var idx = sections.indexOf(currentSection);
        if(idx.notNil) {
            var prev_section = sections[idx - 1];
            if(prev_section.notNil) {
                currentSection = prev_section;
                currentTime = sectionTimes[currentSection];
                this.part(currentSection);
                this.sync;
            };
        };

        if(onPrev.notNil) { onPrev.value(currentSection) };
    }

    section {
        arg name;
        this.command(\part, name.asString);
    }

    sync {
        this.command(\sync, currentTime);
    }

    bindSoundfile {
        arg path, start = 0, end = nil, fadeIn = 0, fadeOut = 0, out = 0;

        if(sound_file.notNil) {sound_file.free};
        sound_file = SoundFile.new;
        sound_file.openRead(path);
        cue_params = (out: out,
            ar: fadeIn,
            dr: fadeOut,
            begin: start * sound_file.sampleRate
        );

        if(end.notNil) {
            var pos = end * sound_file.sampleRate;
            cue_params.add(\lastFrame -> pos);
            maxTime = pos;
        } {
            maxTime = sound_file.duration;
        };

        maxTime.postln;

        cue_params.postln;
    }

    play {
        if(isPaused) {
            timerRoutine.resume;
        }
        {
            timerRoutine.play(doReset: true);
        };
        isPaused = false;
        this.command(\state, "play");
        this.sync;

        if(onPlay.notNil) { onPlay.value(currentTime) };

        if(sound_file.notNil) {
            // play after stop
            if(play_event.isNil) {
                var pos = (currentTime * sound_file.sampleRate) + cue_params[\begin];
                cue_params[\firstFrame] = pos;
                play_event = sound_file.cue(cue_params, true, true);
                play_event.synth.postln;
            };

            // resume
            if(play_event.notNil) {
                play_event.resume
            };
        }
    }

    stop {
        isPaused = false;
        currentTime = 0;
        timerRoutine.stop;
        this.command(\state, "stop");
        this.sync;

        if(onStop.notNil) { onStop.value };

        if(sound_file.notNil && play_event.notNil) {
            var delay = play_event[\dr] + 0.1;

            { play_event = nil }.defer(delay);
            play_event.stop;
        }
    }

    pause {
        isPaused = true;
        timerRoutine.pause;
        this.command(\state, "pause");

        if(onPause.notNil) { onPause.value };

        if(sound_file.notNil && play_event.notNil) {
            play_event.pause;
        }
    }

    setSections {
        arg ... values;
        sections.clear;
        sectionTimes.clear;

        Dictionary.newFrom(values).keysValuesDo { |k, v|
            sections.add(k);
            sectionTimes[k] = v;
        };

        currentSection = sections.first;
        this.section(currentSection);
    }

    sections {
        ^sectionTimes;
    }
}

NodeJS_Image : NodeJS_Widget {
    var <>path;
    var <>url;
    var <width;
    var <height;

    *new {
        arg url, params = [];
        var p = super.new("image", [] ++ params);
        ^p.initImage(url);
    }

    initImage {
        arg url_;
        url = url_;
        params[\url] = NodeJS.imageDirPrefix +/+ url;
    }

    // uploads to Node image directory
    upload {
        arg path, size = nil;
        var res, ext, dest_path, info, cmd, img_size;
        var gm = "/usr/local/bin/gm";
        res = path.pathExists;
        ext = PathName(path).extension.toLower;

        if(res == false) {
            "[%] path not exists '%'".format(this.class, path).error;
            ^false;
        };

        if(res.asString == "folder") {
            "[%] path is directory '%'".format(this.class, path).error;
            ^false;
        };

        if(["jpg", "jpeg", "png", "gif"].includesEqual(ext).not) {
            "[%] not image file '%'".format(this.class, path).error;
            ^false;
        };

        // ensure dir exists
        NodeJS.imageDir.mkdir;

        // convert
        dest_path = NodeJS.imageDir +/+ url.basename;
        cmd = gm + "convert";
        if(size.notNil) {
            cmd = cmd + "-resize" + size.asPoint.asArray.join("x")
        };
        cmd = cmd + path.escapeChar($").quote + dest_path.escapeChar($").quote;
        cmd.systemCmd;

        // get size information
        cmd = gm + "identify" + dest_path.escapeChar($").quote;
        // format:
        // build/img/003.jpg JPEG 683x1024+0+0 DirectClass 8-bit 105.7Ki 0.000u 0:01
        img_size = (cmd.unixCmdGetStdOut.split($ ) @@ -6).drop(-4).split($x).asInt;
        width = img_size[0];
        height = img_size[1];
        this.path = dest_path;

        ^true;
    }

    setAsPageBackground {
        this.command("url", NodeJS.imageDirPrefix +/+ url);
    }

    clearBackground {
        NodeJS.css("html", "background", "#60646D");
    }
}

NodeJS_SoundImage : NodeJS_Image {
    var snd_path;

    *new {
        arg path;
        ^super.new.initSoundImage(path);
    }

    initSoundImage {
        arg path;
        var image_path = "/tmp/%_tmp.png".format(this.class);
        this.initImage("snd_" ++ PathName(path).fileNameWithoutExtension ++ ".png");
        snd_path = path;

        SP_Wav2Png.new(path, image_path).convert;
        this.upload(image_path);
    }
}

NodeJS_ImageSequence {
    var <>urls;

    *new {
        ^super.new.init();
    }

    init {
        urls = List.new;
    }

    clear {
        urls.clear;
    }

    addUrl {
        arg url;
        urls.add(url);
    }

    addUrls {
        arg urls_;
        urls = (urls ++ urls_).asList;
    }

    addUrlPattern {
        arg pattern = "*.jpg";
        var files;

        pattern = NodeJS.imageDir +/+ pattern.basename;
        files = pathMatch(pattern);
        urls = (urls ++ files.collect({|p| p.basename})).asList;
    }

    addImage {
        arg path, size = nil;
        var img = NodeJS_Image.new(path.basename);
        img.upload(path, size);
        urls.add(img.url);
        "[%] image uploaded: %".format(this.class, NodeJS.imageDirPrefix +/+ img.url).postln;
    }

    addImages {
        arg paths, size = nil;
        paths.do { |p|
            this.addImage(p, size);
        }
    }

    addImagePattern {
        arg pattern, size = nil;
        this.addImages(pathMatch(pattern), size);
    }

    makeThumbs {
        arg size = nil;
        size = size ? 100@100;

        urls.do { |u|
            var path_name = PathName(u);
            var new_url = path_name.fileNameWithoutExtension ++ "_thumb." ++ path_name.extension;
            var img = NodeJS_Image.new(new_url);
            img.upload(NodeJS.imageDir +/+ path_name.fileName, size);
            img.path.moveToDir(NodeJS.thumbDir, true, true);
            "[%] image thumbs created: %".format(this.class, NodeJS.thumbDirPrefix +/+ new_url).postln;
        }
    }
}


NodeJS_Slideshow : NodeJS_Widget {
    var <seq;
    var currentImage;

    *new {
        arg urls = [], params = [];
        var p = super.new("slideshow", params);
        ^p.initSlideshow(urls);
    }

    initSlideshow {
        arg urls;
        currentImage = 0;
        seq = NodeJS_ImageSequence.new;
        seq.urls = urls.asList;

        widgetAction = { |msg|
            switch(msg[1].asString,
                "prev", { this.prev },
                "next", { this.next },
                "first", { this.first },
                "last", { this.last },
                { "[%] unknown command: %".format(this.class, msg[1].asString).postln }
            );
        };
    }

    addImages {
        arg paths, size = nil;
        seq.addImages(paths, size);
        this.sync;
    }

    addUrlPattern {
        arg pattern = "*.jpg";
        seq.addUrlPattern(pattern);
    }

    addImagePattern {
        arg pattern;
        seq.addImagePattern(pattern);
    }

    imageUrls {
        ^seq.urls;
    }

    imageCount {
        ^seq.urls.size;
    }

    currentUrl {
        ^seq.urls[currentImage];
    }

    sync {
        if(this.currentUrl.notNil) {
            this.command("url", NodeJS.imageDirPrefix +/+ this.currentUrl);
        }
    }

    next {
        if(currentImage < (seq.urls.size - 1)) {
            currentImage = currentImage + 1;
            this.sync;
        }
    }

    prev {
        if(currentImage > 0) {
            currentImage = currentImage - 1;
            this.sync;
        }
    }

    first {
        if(this.imageCount > 0) {
            currentImage = 0;
            this.sync;
        }
    }

    last {
        if(this.imageCount > 0) {
            currentImage = this.imageCount - 1;
            this.sync;
        }
    }

    toImage {
        arg n;
        if(n >= 0 && n < this.imageCount) {
            currentImage = n;
            this.sync;
        }
        {
            "[%] invalid page number: %".format(this.class, n);
        }
    }

    clear {
        seq.clear;
        currentImage = 0;
    }
}


NodeJS_UI1 {
    var <knob;
    var <toggle;
    var <button;
    var <slider;
    var lines;

    *new {
        arg num = 6;
        ^super.new.init(num);
    }

    init { |n|
        knob = Array.new(n);
        toggle = Array.new(n);
        button = Array.new(n);
        slider = Array.new(n);
        lines = List.new;

        n.do { |i|
            var k, t, b, s;
            var l;

            l = "knob" ++ i;
            k = NodeJS_Knob.new(params: [\idx, l]);
            knob.add(k);
            k.label = l;

            l = "toggle" ++ i;
            t = NodeJS_Toggle.new(size: 100, params: [\idx, l]);
            toggle.add(t);
            t.label = l;

            l = "button" ++ i;
            b = NodeJS_Button.new(100, params: [\idx, l]);
            button.add(b);
            b.label = l;

            l = "slider" ++ i;
            s = NodeJS_Slider.new(0, params:[\idx, l]);
            slider.add(s);
        }
    }

    add {
        knob.do { |k| k.add };
        this.addNewline;
        toggle.do { |t| t.add };
        this.addNewline;
        button.do { |b| b.add };
        this.addNewline;
        slider.do { |s|
            s.add;
            s.css("margin", "5px 33px");
        };
    }

    addNewline {
        var nl = NodeJS_Widget.new("newline");
        nl.add;
    }

    remove {
        knob.do { |k| k.remove };
        toggle.do { |t| t.remove };
        button.do { |b| b.remove };
        slider.do { |s| s.remove };
    }

    labels_ {
        arg type, values = [];
        var elems, dict;

        switch(type.toString,
            "knob",   {elems = `knob},
            "toggle", {elems = `toggle},
            "button", {elems = `button},
            "slider", {elems = `slider}
        );

        dict = Dictionary.newFrom(values);
        dict.postln;
        dict.keysValuesDo{ |k,v|
            if(elems[k].notNil) {
                elems[k].label = v;
                elems[k].update;
            };
        };
    }
}

