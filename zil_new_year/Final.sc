FinalScene : SynthScene {
    var buf;

    *new {
        ^super.new("Final", "/final",
            synthName: "final").initFinal;
    }

    initFinal {
        buf = Buffer.cueSoundFile(Server.default, "/Users/serj/work/music/sounds/gombert.wav");
        this.synthParam[\buf] = buf;
    }

    set {
        arg ... args;
        args.keysValuesDo { |k, v|
            synthParam[k] = v;
        }
    }

    start {
        this.initFinal;
        super.start;
    }

}