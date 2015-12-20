Scenes {
    var soundLib;
    var person1, person2;

    var oscScene0_l, oscScene0_r;
    var scene0_synth_l, scene0_synth_r;

    var oscScene1, oscScene2, oscScene3, oscScene4, oscScene5;
    var oscScene_dvoinik;
    var dvoinik_synth;

    var oscScene_xfader;
    var xfader_synth;

    var oscScene_glass;

    var seledka_move, seledka_bass, seledka_beat;

    var oscScene_drazhe;
    var drazhe_synth, drazhe_control;
    var <>drazheParam;

    *new {
        arg sound_lib = nil;
        ^super.new.init(sound_lib);
    }

    init {
        arg sound_lib, osc_port = 7000;
        soundLib = sound_lib;

        person1 = Kinect.new(1);
        person2 = Kinect.new(2);

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

        oscScene_glass = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \ding, { xfader_synth.run(true) },
                { format("unknown message: '%'", msg).postln });
        }, "/glass", nil, osc_port);

        oscScene_drazhe = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, {
                    drazhe_control.reset;
                    drazhe_control.play;
                    drazhe_synth.run(true);
                },
                \stop,  {
                    drazhe_control.stop;
                    drazhe_synth.run(false);
                },
                { format("unknown message: '%'", msg).postln });
        }, "/drazhe", nil, osc_port);

        drazheParam = Dictionary.new;
        drazheParam[\timeout] = 0.2;
        drazheParam[\acc_threshold] = 0.07;

        drazhe_control = Routine {
            inf.do {
                var acc1 = person1.accAll;
                acc1.postln;

                if((acc1 > drazheParam[\acc_threshold] || person1.noHands), {
                    drazhe_synth.set(\freq, 20);
                    drazhe_synth.set(\dur, 0.1);
                    drazhe_synth.set(\run, 1);

                }, {
                    drazhe_synth.set(\freq, 10);
                    drazhe_synth.set(\dur, 0.04);
                    drazhe_synth.set(\run, 0);
                });
                drazheParam[\timeout].wait;
            };
        };
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

    scene_seledka {
        arg synth_move, synth_bass, synth_beat;

    }

    scene_drazhe {
        arg synth;
        drazhe_synth = synth;
    }
}

SeledkaScene {
    var person1, person2;
    var dest_addr;
    var <part1, <part2, <>part3;
    var <>pBass, <>pOnion, <>pMetal1, <>pMetal2;
    var tempo;
    var <swing_amount, <swing;

    *new {
        ^super.new.init;
    }

    init {
        arg time = 200;
        person1 = Kinect.new(1);
        person2 = Kinect.new(2);
        tempo = time;
        swing_amount = 0.01;
        swing = [1 + swing_amount, 1 - swing_amount, 1 + swing_amount, 1 - swing_amount]; // add swing

        dest_addr = NetAddr("alex", 10000);

        part1 = Routine {
            inf.do {
                var acc1 = person1.accAll;
                var acc2 = person2.accAll;

                if([acc1, acc2].any({|v| v > 0.07})) {
                    var ch = [1, 2, 3, 4].choose;

                    switch(ch,
                        1, {
                            Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                        },
                        2, {
                            Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("onion1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                        },
                        3, {
                            Synth(\sample_beat, [\amp, 5, \buf, ~l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, 1]);
                        },
                        4, {
                            Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal2"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                        },
                    );
                };

                0.3.wait;
            }
        };
    }

    play1 {
        part1.play;
    }

    play2 {
        part2.play;
    }

    play3 {
        part1.stop;
        part3.play
    }


}