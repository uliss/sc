Piece_Shannon_Bachiana : GuidoPieceApp {
    var <>fadeTime;
    *new {
        arg time = 2;
        ^super.new("Tableau alla Bachiana", "William R. Shannon", "/shannon", params: (fadeTime: time));
    }

    initPatches {
        arg params;

        this.addPatch(\cello, ["common.in", "common.pan2", "common.freeverb2"]);
        this.addPatch(\track, ["common.gain", "common.env"], (
            in: SFP("/Users/serj/work/music/sounds/pieces/shannon_tableau_bachiana.wav"),
            env: Env.asr(releaseTime: params[\fadeTime])
        ));

        onPlay = {
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
        var playback;
        var track_box;
        var track_gain;
        var cello_box;
        var cello_amp;
        var cello_reverb_box;
        var cello_reverb_mix;
        var cello_reverb_room;
        var cello_pan;

        playback = NodeJS_Playcontrol.new(false, false, true);
        playback.onPlay = { this.play };
        playback.onPause = { this.pause; playback.currentTime = 0.001 };
        playback.onStop = { this.stop };
        this.addWidget(\playback, playback);

        cello_box = this.addHBox("cello");
        cello_amp = NodeJS_Slider.new(1).label_("amp").layout_(cello_box);
        this.addWidget(\celloAmp, cello_amp);
        this.bindW2P(\celloAmp, \cello, \in_amp);

        cello_reverb_box = NodeJS_VBox.new.layout_(cello_box);
        this.addWidget(\celloReverbBox, cello_reverb_box);

        cello_reverb_mix = NodeJS_Knob.new(0.5).label_("reverb mix").size_(85).labelSize_(20).layout_(cello_reverb_box);
        this.addWidget(\celloReverbMix, cello_reverb_mix);
        this.bindW2P(\celloReverbMix, \cello, \freeverb2_mix);
        cello_reverb_room = NodeJS_Knob.new(0.7).label_("reverb room").size_(85).labelSize_(20).layout_(cello_reverb_box);
        this.addWidget(\celloReverbRoom, cello_reverb_room);
        this.bindW2P(\celloReverbRoom, \cello, \freeverb2_room);


        track_box = this.addVBox("track");

        track_gain = NodeJS_Slider.new(1).vertical_(true).label_("amp").layout_(track_box);
        this.addWidget(\trackGain, track_gain);
        this.bindW2P(\trackGain, \track, \gain);

        this.addParams;
    }
}