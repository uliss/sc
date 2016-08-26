Piece_Mazzoli_Tooth_and_Nail : SP_PdfMusicPiece {
    *new {
        ^super.new("/Users/serj/work/music/sc/concerts/pieces/scores/Missy Mazzoli Tooth and Nail.pdf", "Tooth and Nail", "Missy Mazzoli", "/sc/mazzoli");
    }

    resetPatch {
        this.addPatch(\viola, ["viola.in", "viola.compress", "viola.reverb", "common.pan2"]);
        this.addPatch(\track, ["common.sfplayCh", "common.pseudoStereo"],
            (channel: 0, path: "/Users/serj/work/music/sounds/pieces/mazzoli_tooth_and_nail.wav"));
        this.addPatch(\click, ["common.sfplayCh", "mix.1<2", "route.->phones|"],
            (channel: 1, path: "/Users/serj/work/music/sounds/pieces/mazzoli_tooth_and_nail.wav"));
    }

    initPatches {
        this.resetPatch;
        onPlay = { this.resetPatch; this.playPatches };
        onStop = { this.releasePatches(2) };
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
            this.bindW2P(\trackAmp, \track, \amp);
        }.value;


        // VIOLA STAFF
        {
            var amp, pan, box, mix, room;

            // VIOLA AMP
            amp = NodeJS_Slider.new(1, 0, 2).label_("viola").labelSize_(20).hidden_(true);
            this.addWidget(\violaAmp, amp);
            this.bindW2P(\violaAmp, \viola, \amp);

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
            this.bindW2P(\violaReverbMix, \viola, \mix);

            // REVERB ROOM
            room = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(box);
            this.addWidget(\violaReverbRoom, room);
            this.bindW2P(\violaReverbRoom, \viola, \room);
        }.value;

        // CLICK WIDGETS
        {
            var amp;
            amp = NodeJS_Slider.new(1, 0, 4).label_("click").labelSize_(20).hidden_(true);
            this.addWidget(\clickAmp, amp);
            this.bindW2P(\clickAmp, \click, \amp);
        }.value;

        this.addMonitorWidget;
    }

    syncTitle {
        NodeJS.sendMsg("/node/title", "");
    }

    initPageTurns {
        this.loadPageTurns(this.class.turnsDir +/+ "mazzoli_tooth_and_nail.txt");
    }
}