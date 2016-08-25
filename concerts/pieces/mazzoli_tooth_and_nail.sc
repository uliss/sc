Piece_Mazzoli_Tooth_and_Nail : SP_PdfMusicPiece {
    *new {
        ^super.new("/Users/serj/work/music/sc/concerts/pieces/scores/Missy Mazzoli Tooth and Nail.pdf", "Tooth and Nail", "Missy Mazzoli", "/sc/spiegel");
    }

    resetPatch {
        this.addPatch(\viola, ["viola.in", "viola.compress", "viola.reverb", "common.pan2"]);
        this.addPatch(\track, ["common.sfplay"], (path: "/Users/serj/work/music/sounds/pieces/mazzoli_tooth_and_nail.wav"));
    }

    initPatches {
        this.resetPatch;
        onPlay = { this.resetPatch; this.playPatches };
        onStop = { this.releasePatches(2) };
    }

    initUI {
        var w2, w3, w4, w5, w6, w7, w8;

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

        // TRACK AMP
        w4 = NodeJS_Slider.new(1, 0, 2).label_("track").labelSize_(20).hidden_(true);
        this.addWidget(\trackAmp, w4);
        this.bindW2P(\trackAmp, \track, \amp);

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
    }

    syncTitle {
        NodeJS.sendMsg("/node/title", "");
    }
}
