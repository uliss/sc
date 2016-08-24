Piece_Part_Spiegel_im_Spiegel : SP_PieceApp {
    *new {
        arg out = 0, violaIn = 0;
        ^super.new("Spigel im Spiegel", "Arvo Part", "/sc/spiegel", [\out, out]);
    }

    resetPatch {
        this.addPatch(\viola, ["viola.in", "viola.compress", "viola.reverb", "common.pan2"]);
        this.addPatch(\piano, ["common.sfplay", "common.reverb"], (path: "/Users/serj/work/music/sounds/pieces/spiegel_im_spiegel_100.wav"));
    }

    initPatches {
        this.resetPatch;
        onPlay = { this.resetPatch; this.playPatches };
        onStop = { this.releasePatches(2) };
    }

    initUI {
        var w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11;
        // sheet music
        w1 = NodeJS_Slideshow.new(nil, [\hideButtons, true, \noSwipe, true])
        .addImages(["/Users/serj/work/music/sc/concerts/pieces/scores/Spiegel_im_Spiegel_my_version-Violin.png"], 1600@1600);
        this.addWidget(\sheetMusic, w1);

        // PLAY CONTROL
        w2 = NodeJS_Playcontrol.new(false, false, false, 10).parent_('ui-piece-toolbar');
        w2.onPlay = { this.play };
        w2.onStop = { this.stop };
        w2.onPause = { this.pause };
        this.addWidget(\playControl, w2);

        // VIOLA AMP
        w3 = NodeJS_Slider.new(1, 0, 2).label_("viola").labelSize_(20).hidden_(true);
        this.addWidget(\violaAmp, w3);
        this.bindW2P(\violaAmp, \viola, \amp);

        // PIANO AMP
        w4 = NodeJS_Slider.new(1, 0, 2).label_("piano").labelSize_(20).hidden_(true);
        this.addWidget(\pianoAmp, w4);
        this.bindW2P(\pianoAmp, \piano, \amp);

        // VIOLA PAN
        w5 = NodeJS_Pan.new(0, 100).label_("vla pan").hidden_(true);
        this.addWidget(\violaPan, w5);
        this.bindW2P(\violaPan, \viola, \pan);

        // VIOLA REVERB
        w8 = NodeJS_VBox.new.title_("viola reverb").hidden_(true).borderColor_("#AAA");
        this.addWidget(\violaReverbBox, w8);

        w6 = NodeJS_Knob.new(0.5, 0, 1).size_(70).label_("mix").labelSize_(20).hidden_(true).layout_(w8);
        this.addWidget(\violaReverbMix, w6);
        this.bindW2P(\violaReverbMix, \viola, \mix);

        w7 = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(w8);
        this.addWidget(\violaReverbRoom, w7);
        this.bindW2P(\violaReverbRoom, \viola, \room);

        // PIANO REVERB
        w9 = NodeJS_VBox.new.title_("piano reverb").hidden_(true).borderColor_("#AAA");
        this.addWidget(\pianoReverbBox, w9);

        w10 = NodeJS_Knob.new(0.5, 0, 1).size_(70).label_("mix").labelSize_(20).hidden_(true).layout_(w9);
        this.addWidget(\pianoReverbMix, w10);
        this.bindW2P(\pianoReverbMix, \piano, \mix);

        w11 = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(w9);
        this.addWidget(\pianoReverbRoom, w11);
        this.bindW2P(\pianoReverbRoom, \piano, \room);

        this.createWidgets;
        NodeJS.sendMsg("/node/title", "");
    }

    sync {
        super.sync;
        NodeJS.sendMsg("/node/title", "");
    }

    free {
        super.free;
    }
}
