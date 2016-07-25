NodeJS_Widget {
    classvar idx_count = 1;
    var <idx;
    var <>type;
    var <>params;
    var <>action;
    var <>widgetAction;
    var added;
    var osc;

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
        osc = OSCFunc({|m|
            params[\value] = m[1];
            if(action.notNil) {
                action.value(m);
            };
            if(widgetAction.notNil) {
                widgetAction.value(m);
            }
        }, "/sc/ui/" ++ this.id, nil, NodeJS.outOscPort);
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
}

NodeJS_Knob : NodeJS_Widget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 100, label = "", params = [];
        ^super.new("knob", [
            \size, size,
            \label, label,
            \min, min,
            \max, max,
            \value, value
        ] ++ params);
    }
}

NodeJS_Pan : NodeJS_Widget {
    *new {
        arg value = 0, size = 50, params = [];
        var p = super.new("pan", [
            \size, size,
            \value, value
        ] ++ params);
        p.label = p.id;
        ^p;
    }
}

NodeJS_Slider : NodeJS_Widget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 180, label = "", horizontal = 0, relative = 0, params = [];
        var p = super.new("slider", [
            \horizontal, horizontal,
            \min, min,
            \max, max,
            \value, value,
            \size, size,
            \label, label,
            \relative, relative] ++ params);
        ^p;
    }
}

NodeJS_Toggle : NodeJS_Widget {
    *new {
        arg value = 0, size = 100, label = "", params = [];
        var p = super.new("toggle", [
            \value, value,
            \label, label,
            \size, size] ++ params);
        ^p;
    }
}

NodeJS_Button : NodeJS_Widget {
    *new {
        arg size = 100, label = "", params = [];
        var p = super.new("button", [\size, size, \label, label] ++ params);
        ^p;
    }
}

NodeJS_Pianoroll : NodeJS_Widget {
    *new {
        arg size = 600, octaves = 3, midibase = 48, params = [];
        var p = super.new("pianoroll", [
            \size, size,
            \octaves, octaves,
            \midibase, midibase] ++ params);
        ^p;
    }
}

NodeJS_XFade : NodeJS_Widget {
    *new {
        arg size = 200, label = "", params = [];
        var p = super.new("crossfade", [
            \size, size,
            \label, label] ++ params);
        ^p;
    }
}

NodeJS_Matrix : NodeJS_Widget {
    *new {
        arg size = 200, row = 4, col = 4, label = "", params = [];
        var p = super.new("matrix", [
            \size, size,
            \row, row,
            \col, col,
            \label, label] ++ params);
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
        arg back = true, forward = true, display = true, syncTime = 10, params = [];
        var p = super.new("playcontrol", [
            \back, back,
            \forward, forward,
            \display, display
        ] ++ params);
        ^p.initPlaycontrol(syncTime);
    }

    initPlaycontrol {
        arg sync_time = 10;

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

    part {
        arg txt;
        this.command(\part, txt.asString);
    }

    sync {
        this.command(\sync, currentTime);
    }

    bindSoundfile {
        arg path, begin = 0, end = nil, fadeIn = 0, fadeOut = 0, out = 0;

        if(sound_file.notNil) {sound_file.free};
        sound_file = SoundFile.new;
        sound_file.openRead(path);
        cue_params = (out: out,
            ar: fadeIn,
            dr: fadeOut,
            begin: begin * sound_file.sampleRate
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

        Dictionary.newFrom(*values).keysValuesDo { |k, v|
            sections.add(k);
            sectionTimes[k] = v;
        };

        currentSection = sections.first;
        this.part(currentSection);
    }
}

NodeJS_Image : NodeJS_Widget {
    var <>path;
    var <url;
    var <width;
    var <height;

     *new {
        arg path, url = nil, params = [];
        var p = super.new("image", [] ++ params);
        ^p.initImage(path, url);
    }

    initImage {
        arg path_, url_;
        var res = path_.pathExists;
        path = path_;
        url = url_;

        if(res == false) {
            "[%] ERROR: path not exists '%'".format(this.class, path).postln;
            ^nil;
        };

        if(res.asString == "folder") {
            "[%] ERROR: path is directory '%'".format(this.class, path).postln;
            ^nil;
        };

        if(["jpg", "jpeg", "png", "gif"].includesEqual(PathName(path).extension.toLower).not) {
            "[%] ERROR: not image file '%'".format(this.class, path).postln;
            ^nil;
        }
    }

    // uploads to Node image directory
    upload {
        var img_params = ();
        this.sendMsg("/image/upload", path, JSON.toJSON(img_params));
        OSCFunc({ |m|
            url = m[1];
            params[\url] = url.asString;
        }, "/sc/image/upload/url").oneShot;

        OSCFunc({ |m|
            width = m[1];
            height = m[2];
        }, "/sc/image/upload/size").oneShot;
    }

    setAsPageBackground {
        arg bgcolor = "#60646D";
        NodeJS.css("html", "background", bgcolor + "url('" ++ url ++ "') no-repeat center fixed");
        NodeJS.css("html", "background-size", "cover");
        NodeJS.css("body", "background-color", "transparent");
        NodeJS.css("h1",   "background-color", "transparent");
    }

    clearBackground {
        NodeJS.css("html", "background", "#60646D");
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

