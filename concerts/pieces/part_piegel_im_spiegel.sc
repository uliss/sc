Piece_Part_Spiegel_im_Spiegel : SP_PieceApp {
    var <widget_play;
    var <widget_viola_part;
    var <widget_viola_amp_slider;
    var <widget_piano_amp_slider;
    var <widget_viola_pan_knob;

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
        onPlay = { this.playPatches };
        onStop = { this.releasePatches(2) };
    }

    initUI {
        var w1, w2, w3, w4, w5;
        // sheet music
        w1 = NodeJS_Slideshow.new(nil, [\hideButtons, true, \noSwipe, true])
        .addImages(["/Users/serj/work/music/sc/concerts/pieces/scores/Spiegel_im_Spiegel_my_version-Violin.png"], 1600@1600);
        this.addWidget(\sheetMusic, w1);

        // PLAY CONTROL
        w2 = NodeJS_Playcontrol.new(false, false, false, 10, params: [\parent, 'ui-piece-toolbar']);
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
        w5 = NodeJS_Pan.new(0, 100, params: [\collapse, 1]).label_("vla pan");
        this.addWidget(\violaPan, w5);
        this.bindW2P(\violaPan, \viola, \pan);

        this.createWidgets;
        this.sync;
    }

    sync {
        super.sync;
        this.widget(\sheetMusic).sync;
        NodeJS.sendMsg("/node/title", "");
    }

    free {
        super.free;
    }
}
