Piece_Shannon_Bachiana : GuidoPieceApp {
    var <>fadeTime;
    *new {
        arg time = 2;
        ^super.new("Tableau alla Bachiana", "William R. Shannon", "/shannon", params: (fadeTime: time));
    }

    initPatches {
        arg params;

        this.addPatch(\cello, ["common.in", "viola.reverb", "common.pan2"]);
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
        // this.addMonitorWidget(false);
    }
}