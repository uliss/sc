Piece_King_Revelations : GuidoPieceApp {
    var <>fadeTime;
    *new {
        arg time = 1;
        ^super.new("Revelations of Sain John the Divine", "Larry King", "/king", params: (fadeTime: time)).loadParams;
    }

    initPatches {
        arg params;

        this.addPatch(\track, ["common.gain", "common.env"], (
            in: SFP("/Users/serj/work/music/sounds/pieces/larry_king_revelations.wav"),
            env: Env.asr(releaseTime: params[\fadeTime]),
        ));

        this.addPatch(\click, ["common.gain", "common.env", "route.split", "route.->phones|"], (
            in: SFP("/Users/serj/work/music/sounds/pieces/larry_king_revelations_practise.wav"),
            env: Env.asr(releaseTime: params[\fadeTime]),
            split_bus: 2,
            channel: 0
        ));

        onPlay = {
            // this.patch(\cello).play;
            // this.playPatches;
        };

        onPause = {
            this.stopPatches;
            this.widget(\trackPlay).value = 0;
        };

        onStop = {
            this.releasePatches(params[\fadeTime]);
            this.widget(\trackPlay).value = 0;
        };
    }

    initUI {
        arg params;

        var track_box;
        var track_gain;
        var track_play;
        var click_gain;
        var hbox1;

        track_box = this.addVBox("track").align_(\left);

        {
            track_play = NodeJS_Toggle.new.label_("play").size_(150).layout_(track_box).labelSize_(40);
            track_play.onValue = {|v|
                if(v == 1) {
                    this.patch(\track).play;
                    this.patch(\click).play;

                } {
                    this.patch(\track).release(params[\fadeTime]);
                    this.patch(\click).release(params[\fadeTime]);
                };
            };
            this.addWidget(\trackPlay, track_play);

            hbox1 = this.addHBox("gain").layout_(track_box).align_(\left);

            track_gain = NodeJS_Slider.new(1).vertical_(true).label_("amp").layout_(hbox1).cssStyle_((margin: "20px"));
            this.addWidget(\trackGain, track_gain);
            this.bindW2P(\trackGain, \track, \gain);

            click_gain = NodeJS_Slider.new(1).vertical_(true).label_("click").layout_(hbox1).cssStyle_((margin: "20px"));
            this.addWidget(\clickGain, click_gain);
            this.bindW2P(\clickGain, \click, \gain);
        }.value;

        this.addParams;
    }
}