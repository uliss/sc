Scenes {
    var soundLib;
    var person1, person2;

    var oscScene0_l, oscScene0_r;
    var scene0_synth_l, scene0_synth_r;

    var oscScene1, oscScene2, oscScene3, oscScene4, oscScene5;
    var oscScene_dvoinik;
    var dvoinik_synth;

    var oscScene_xfader;
    var synth_xfader, synth_xfader_param;

    var oscScene_glass;

    var oscScene_seledka;
    var seledka_part1, seledka_part2, seledka_part3;

    var oscScene_drazhe;
    var drazhe_synth, drazhe_control;
    var <>drazheParam;


    var oscScene_kuranty;
    var synth_kuranty, synth_kuranty_param;

    var oscScene_wind;
    var synth_wind, synth_wind_param;

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
                \start, { synth_xfader = Synth.new(\xfader_synth,
                    [\buf1, ~l.buffer("drazhe"), \buf2, ~l.buffer("jingle4")] ++ synth_xfader_param); },
                \pause, { synth_xfader.run(false) },
                \stop,  { synth_xfader.free },
                \release, { synth_xfader.release(msg[2]) },
                \xfade, { synth_xfader.set(\pan, msg[2]) },
                { format("unknown message: '%'", msg).postln });
        }, "/xfader", nil, osc_port);

        /*        oscScene_glass = OSCFunc({|msg|
        msg.postln;
        switch(msg[1],
        \ding, { xfader_synth.run(true) },
        { format("unknown message: '%'", msg).postln });
        }, "/glass", nil, osc_port);*/

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
                \freeze, {
                    drazhe_synth.set(\run, msg[2]);
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

        oscScene_kuranty = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, { synth_kuranty = Synth.new(\mono_player,
                    [\buf, ~l.buffer("kuranty1")] ++ synth_kuranty_param) },
                \pause, { synth_kuranty.run(false) },
                \stop,  { synth_kuranty.free },
                \release, { synth_kuranty.release(msg[2]) },
                { format("unknown message: '%'", msg).postln });
        }, "/kuranty", nil, osc_port);


        oscScene_wind = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, { synth_wind = Synth.new(\wind1, synth_wind_param) },
                \pause, { synth_wind.run(false) },
                \stop,  { synth_wind.free },
                \release, { synth_wind.release(msg[2]) },
                { format("unknown message: '%'", msg).postln });
        }, "/wind", nil, osc_port);

        oscScene_seledka = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, { seledka_part1.play },
                \part1, { seledka_part1.play },
                \part2, { seledka_part2.play },
                \part3, { seledka_part1.stop; seledka_part3.play },
                \stop,  { seledka_part1.stop; seledka_part2.stop; seledka_part3.stop },
                { format("unknown message: '%'", msg).postln });
        }, "/seledka", nil, osc_port);

        this.scene_seledka_init;
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

    scene_seledka_init {
        arg tm1 = 0.3, swing = 0.01;
        var bass = Pseq(Bjorklund(4, 11), inf).asStream;
        var times = [1 + swing, 1 - swing, 1 + swing, 1 - swing]; // add swing
        var onion = Pseq(#[1, 0.2, 0.75, 0, 0.175, 0, 1, 0.5] * 1.3, inf).asStream;
        var metal1 = Pseq(#[1, 1, 0, 0.1, 0.5, 0, 0, 1, 0.2], inf).asStream;
        var metal2 = Pseq(#[1, 0.1, 0.1, 1, 0.25, 0.1, 0.75], inf).asStream;
        var microwave = Pseq(#[1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0] * 4, inf).asStream;
        var osc_addr = NetAddr("serge-android", 10000);

        // PART 1
        seledka_part1 = Routine {
            inf.do {
                var acc1 = person1.accAll;
                var acc2 = person2.accAll;
                // k.headZ.postln.lag(0.5);
                // k.headZ.postln

                if([acc1, acc2].any({|v| v > 0.07})) {
                    var ch = [1,2,3,4].choose;
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

                tm1.wait;
            }
        };

        seledka_part2 = Routine {
            var i = 0;
            var perc = 200; // 210, 50
            loop({
                var tm2 = times.wrapAt(i) * (0.1 + (0.1 * (perc/ 100.0)));
                Synth(\bass, [\amp, bass.next * 0.5, \freq, 25.rand + 42, \dur, tm2 * 4]);
                osc_addr.sendMsg("/beat1");
                tm2.wait;
                i = i + 1;
            });
        };

        seledka_part3 = Routine {
            var i = 0;
            var perc = 200; // 210, 50
            loop({
                var tm3 = times.wrapAt(i) * (0.1 + (0.1 * (perc/ 100.0)));

                if(i > 16) {
                    Synth(\sample_beat, [\amp, onion.next, \buf, ~l.buffer("onion1"), \pos: 0.2, \dur, tm3 * 10, \startPos: 1000]);
                    osc_addr.sendMsg("/beat2");
                };

                if(i > 100 && (i < 220) || (i > 260)) {
                    Synth(\sample_beat, [\amp, metal1.next, \buf, ~l.buffer("metal2"), \dur, tm3 * [2, 3, 1, 10].choose, \pos: -0.25]);
                    osc_addr.sendMsg("/beat3");
                };

                if(i > 130) {
                    Synth(\sample_beat, [\amp, metal2.next, \buf, ~l.buffer("metal1"), \dur, tm3 * [2, 3, 1].choose, \pos: -0.25]);
                    osc_addr.sendMsg("/beat4");
                };

                if(i % 28 == 0) {
                    Synth(\sample_beat, [\amp, microwave.next, \buf, ~l.buffer("microwave1"), \dur, tm3 * 16, \pos: [-0.8, 0.8].choose, \startPos, 1000]);
                    osc_addr.sendMsg("/beat5");
                };


                i.postln;


                tm3.wait;
                i = i + 1;
            });

        };
    }

    scene_drazhe {
        arg synth;
        drazhe_synth = synth;
    }

    set_xfader {
        arg ... args;
        synth_xfader_param = args.asList;
        synth_xfader_param.postln;
        if(synth_xfader.notNil) {
            synth_xfader.set(*args);
        };
    }

    set_kuranty {
        arg ... args;
        synth_kuranty_param = args.asList;
        synth_kuranty_param.postln;
        if(synth_kuranty.notNil) {
            synth_kuranty.set(*args);
        };
    }

    set_wind {
        arg ... args;
        synth_wind_param = args.asList;
        synth_wind_param.postln;
        if(synth_wind.notNil) {
            synth_wind.set(*args);
        };
    }
}
