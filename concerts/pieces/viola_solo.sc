Piece_Viola_Solo : GuidoPieceApp {
    *new {
        arg out = 0;
        ^super.new("Viola Solo", "", "/viola_solo", [\out, out]);
    }

    initPatches {
        this.addPatch(\viola, ["viola.in", "viola.compress", "common.pan2", "common.freeverb2"]);

        onPlay = { this.playPatches };

        // temporal fix, while no pause support
        // onPause = {
        // this.stopPatches;
        // };

        onStop = { this.releasePatches(2) };
    }

    initUI {
        var control, viola_amp, viola_reverb_mix, viola_reverb_room, reverb_box, viola_pan;

        // PLAY CONTROL
        control = NodeJS_Playcontrol.new(false, false, false, 10).parent_('ui-piece-toolbar');
        control.onPlay = { this.play };
        control.onStop = { this.stop };
        // control.onPause = { this.pause };
        this.addWidget(\playControl, control);

        // VIOLA AMP
        viola_amp = NodeJS_Slider.new(1, 0, 1).label_("viola");
        this.addWidget(\violaAmp, viola_amp);
        this.bindW2P(\violaAmp, \viola, \amp);

        reverb_box = NodeJS_HBox.new.title_("Reverb").borderColor_("#AAA");
        this.addWidget(\reverbBox, reverb_box);

        viola_reverb_mix = NodeJS_Knob.new(0.5, 0, 1).size_(150).label_("mix").layout_(reverb_box);
        this.addWidget(\violaReverbMix, viola_reverb_mix);
        this.bindW2P(\violaReverbMix, \viola, \freeverb2_mix);

        viola_reverb_room = NodeJS_Knob.new(0.7, 0, 1).size_(150).label_("room").layout_(reverb_box);
        this.addWidget(\violaReverbRoom, viola_reverb_room);
        this.bindW2P(\violaReverbRoom, \viola, \freeverb2_room);

        viola_pan = NodeJS_Pan.new.size_(150).label_("VLA PAN");
        this.addWidget(\violaPan, viola_pan);
        this.bindW2P(\violaPan, \viola, \pan);

        this.addMonitorWidget(false);
    }
}
