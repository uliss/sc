Piece_Part_Spiegel_im_Spiegel : ConcertPiece {
    var <widget_play;
    var <widget_viola_part;
    var synth_viola;

    *new {
        arg out = 0, violaIn = 0;
        ^super.new("Spigel im Spiegel", "Arvo Part", "/spigel", [\out, out, \violaIn, violaIn]);
    }

    initSynths {
        arg params;
        var param_dict = Dictionary.newFrom(params);

        SynthDef(\violaIn, {
            var snd = SPU_ViolaInCommon.ar() * \amp.kr(1, 0.1);
            snd = Pan2.ar(snd, \pos.kr(0, 0.1));
            Out.ar(param_dict[\out], snd);
        }).send;
    }

    initUI {
        // sheet music
        widget_viola_part = NodeJS_Slideshow.new(nil, [\hideButtons, true, \noSwipe, true]);
        widget_viola_part.addImages(["/Users/serj/work/music/sc/concerts/pieces/scores/Spiegel_im_Spiegel_my_version-Violin.png"], 1600@1600);

        widget_play = NodeJS_Playcontrol.new(true, true, false, 10);
        widget_play.bindSoundfile("/Users/serj/work/music/sounds/pieces/spiegel_im_spiegel_100.wav", fadeIn: 0.1, fadeOut: 1);

        this.sync;
    }

    sync {
        widget_viola_part.add;
        widget_viola_part.sync;
        widget_play.add;

        NodeJS.sendMsg("/node/title", "");
        widget_play.command("position", "absBottom")
    }

    free {
        super.free;
        widget_play.remove();
        widget_viola_part.remove();
        NodeJS.sendMsg("/node/title", "UI");
    }
}
