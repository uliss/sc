FinalScene : SynthScene {
    var buf;
    var <>fileName;

    *new {
        arg fname = "/Users/serj/work/music/sounds/gombert.wav";
        ^super.new("Final", "/final",
            synthName: "final").initFinal;
    }

    initFinal {
        arg fname;
        fileName = fname;
    }

    set {
        arg ... args;
        args.keysValuesDo { |k, v|
            synthParam[k] = v;
        }
    }

    start {
        buf = Buffer.cueSoundFile(Server.default, fileName);
        this.synthParam[\buf] = buf;
        super.start;
    }

}