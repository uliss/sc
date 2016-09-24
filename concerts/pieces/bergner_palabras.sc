Piece_Bergner_Palabras : GuidoPdfMusicPiece {
    *new {
        arg ft = 2;
        ^super.new("/Users/serj/work/music/sc/concerts/pieces/scores/Ylva Lung Bergner Palabras.pdf", "Ylva Lung Bergner", "/palabras", params: (fadeTime: ft)).loadParams.loadTasks;
    }

    initPatches {
        arg params;
        this.addPatch(\viola, ["common.in", "common.env", "common.pan2", "common.freeverb2"]);
        this.addPatch(\track, ["common.gain", "common.env"], (
            in: SFP("/Users/serj/work/music/sounds/pieces/bergner_palabras.aif"),
            env: Env.asr(releaseTime: params[\fadeTime])
        ));

        onPlay = {
            arg params;
            var begin, fade;

            params ?? { params = Dictionary.new };
            begin = params[\begin] ? 0;
            fade = params[\fadeTime] ? 2;

            this.removePatch(\track);
            this.addPatch(\track, ["common.gain", "common.env"], (
                in: SFP("/Users/serj/work/music/sounds/pieces/bergner_palabras.aif").start_(begin),
                env: Env.asr(releaseTime: fade)
            ));

            this.playPatches;
        };

        onPause = {
            this.stopPatches;
        };

        onStop = {
            this.releasePatches(params[\fadeTime]);
        };
    }

    initUI {
        var control;

        // PLAY CONTROL
        control = NodeJS_Playcontrol.new(false, false, false, 10).parent_('ui-piece-toolbar');
        control.onPlay = { this.play };
        control.onStop = { this.stop };
        control.onPause = { this.pause };
        this.addWidget(\playControl, control);

        // TRACK STAFF
        {
            var amp;
            // TRACK AMP
            amp = NodeJS_Slider.new(1, 0, 2).label_("track").labelSize_(20).hidden_(true);
            this.addWidget(\trackAmp, amp);
            this.bindW2P(\trackAmp, \track, \gain);
        }.value;

        // VIOLA STAFF
        {
        var amp, pan, box, mix, room;

        // VIOLA AMP
        amp = NodeJS_Slider.new(1, 0, 2).label_("viola").labelSize_(20).hidden_(true);
        this.addWidget(\violaAmp, amp);
        this.bindW2P(\violaAmp, \viola, \in_amp);

        // VIOLA PAN
        amp = NodeJS_Pan.new(0, 100).label_("vla pan").hidden_(true);
        this.addWidget(\violaPan, amp);
        this.bindW2P(\violaPan, \viola, \pan);

        // REVERB BOX
        box = NodeJS_VBox.new.title_("viola reverb").hidden_(true).borderColor_("#AAA").align_("left");
        this.addWidget(\violaReverbBox, box);

        // REVERB MIX
        mix = NodeJS_Knob.new(0.5, 0, 1).size_(70).label_("mix").labelSize_(20).hidden_(true).layout_(box);
        this.addWidget(\violaReverbMix, mix);
        this.bindW2P(\violaReverbMix, \viola, \freeverb2_mix);

        // REVERB ROOM
        room = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(box);
        this.addWidget(\violaReverbRoom, room);
        this.bindW2P(\violaReverbRoom, \viola, \freeverb2_room);
        }.value;

        /*
        // CLICK WIDGETS
        {
        var amp;
        amp = NodeJS_Slider.new(1, 0, 1).label_("click").labelSize_(20).hidden_(true);
        this.addWidget(\clickAmp, amp);
        this.bindW2P(\clickAmp, \click, \amp);
        }.value;

        this.addMonitorWidget;*/

        {
            var cursor = NodeJS_Widget.new(\cursor);
            this.addWidget(\cursor, cursor);

            this.addFunction(\cursor, {
                arg ... args;
                var x = args[0].asInteger;
                var y = args[1].asInteger;
                args.postln;
                cursor.command(\rel, [x, y])
            });
        }.value;
    }

    syncTitle {
        // NodeJS.sendMsg("/node/title", "");
    }

    initPageTurns {
        // this.loadPageTurns(this.class.turnsDir +/+ "mazzoli_tooth_and_nail.txt");
    }
}
