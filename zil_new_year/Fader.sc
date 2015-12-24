MixerScene : SynthScene {
    var <person1, <person2;

    *new {
        arg p1, p2;
        ^super.new("Mixer", "/mixer",
            synthName: "mixer_synth",
            synthParam: [\buf1, ~l.buffer("kuranty1"), \buf2, ~l.buffer("jingle4")]).initMixer(p1, p2);
    }

    initMixer {
        arg p1, p2;
        person1 = p1;
        person2 = p2;

        routine = Routine {
            inf.do {
                var pos1 = p1.headZ.linlin(1, 4, 1, 0);
                var pos2 = p2.headZ.linlin(1, 4, 1, 0,);
                if(p1.headZ == 0) { pos1 = 0 };
                if(p2.headZ == 0) { pos2 = 0 };


                format("% - %", pos1, pos2).postln;

                this.synthSet(\amp1, pos1, \amp2, pos1);

                0.1.wait;
            }
        };
    }

    play1 {
        routine.stop;
        this.synthSet(\amp1, 0, \amp2, 1);
        this.start;
    }

    release {
        arg time = 6;
        super.release(time);
        {routine.stop;}.defer(time);
    }

    play2 {
        routine.reset;
        routine.play;
    }

    stop {
        routine.stop;
        super.stop;
    }


}