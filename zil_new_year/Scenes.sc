Scenes {
    var soundLib;
    var oscScene0_l, oscScene0_r;
    var scene0_synth_l, scene0_synth_r;

    var oscScene1, oscScene2, oscScene3, oscScene4, oscScene5;
    var oscScene_dvoinik;
    var dvoinik_synth;

    var oscScene_xfader;
    var xfader_synth;

    *new {
        arg sound_lib = nil;
        ^super.new.init(sound_lib);
    }

    init {
        arg sound_lib, osc_port = 7000;
        soundLib = sound_lib;


        oscScene0_l = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                1, { scene0_synth_l.run(true)  },
                0, { scene0_synth_l.run(false) },
                \amp, {scene0_synth_r.set(\amp, msg[1])},
                { format("unknown message format: '%'", msg).postln });
        }, "/gadanie/0", nil, osc_port);

        oscScene0_l = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                1, { scene0_synth_r.run(true)  },
                0, { scene0_synth_r.run(false) },
                \amp, {scene0_synth_r.set(\amp, msg[1])},
                { format("unknown message format: '%'", msg).postln });
        }, "/gadanie/1", nil, osc_port);

        oscScene_dvoinik = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, { dvoinik_synth.run(true) },
                \stop,  { dvoinik_synth.run(false) },
                \pos1, { dvoinik_synth.set(\pos1, msg[2]) },
                \pos2, { dvoinik_synth.set(\pos2, msg[2]) },
                { format("unknown message: '%'", msg).postln });
        }, "/dvoinik", nil, osc_port);

        oscScene_xfader = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, { xfader_synth.run(true) },
                \stop,  { xfader_synth.run(false) },
                \pos, { xfader_synth.set(\pan, msg[2]) },
                { format("unknown message: '%'", msg).postln });

        }, "/xfader", nil, osc_port);
    }

    scene0 {
        arg synth1, synth2;
        scene0_synth_l = synth1;
        scene0_synth_r = synth2;
    }

    scene_dvoinik {
        arg synth;
        dvoinik_synth = synth;
    }

    scene_xfader {
        arg synth;
        xfader_synth = synth;
    }
}