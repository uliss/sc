Scenes {
    var soundLib;
    var <person1, <person2;

    var oscScene0_l, oscScene0_r;
    var scene0_synth_l, scene0_synth_r;

    var synth_dvoinik, synth_dvoinik_param, dvoinik_routine;

    var oscScene_xfader;
    var synth_xfader, synth_xfader_param, xfader_routine;

    var oscScene_glass;

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

        oscScene_xfader = OSCFunc({|msg|
            msg.postln;
            switch(msg[1],
                \start, {
                    synth_xfader = Synth.new(\xfader_synth,
                        [\buf1, ~l.buffer("kuranty1"), \buf2, ~l.buffer("jingle4")] ++ synth_xfader_param);
                    xfader_routine.reset;
                    xfader_routine.play;
                },
                \pause, { synth_xfader.run(false) },
                \stop,  {
                    synth_xfader.free;
                    xfader_routine.stop;
                },
                \release, {
                    xfader_routine.stop;
                    synth_xfader.release(msg[2]);
                },
                \amp,   { synth_xfader.set(\amp, msg[2].asFloat) },
                \xfade, { synth_xfader.set(\pan, msg[2]) },
                \set,   { synth_xfader.set(msg[2].asString, msg[3].asFloat) },
                { format("unknown message: '%'", msg).postln });
        }, "/xfader", nil, osc_port);

        this.scene_xfader_init;

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
        drazheParam[\acc_threshold_up] = 0.07;// 02
        drazheParam[\acc_threshold_down] = 0.07;// 02

        drazhe_control = Routine {
            var n = NetAddr("10.1.1.96", 10000);
            inf.do {
                var acc1 = person1.accAll;
                acc1.postln;

                if((acc1 > drazheParam[\acc_threshold_up] || person1.noHands), {
                    drazhe_synth.set(\freq, 20);
                    drazhe_synth.set(\dur, 0.1);
                    drazhe_synth.set(\run, 1);
                    n.sendMsg("/freeze", 0);
                }, {
                    if(acc1 < drazheParam[\acc_threshold_down]) {
                        drazhe_synth.set(\freq, 10);
                        drazhe_synth.set(\dur, 0.04);
                        drazhe_synth.set(\run, 0);
                        n.sendMsg("/freeze", 1);
                    };
                }
                );

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
                \amp,   { synth_wind.set(\amp, msg[2].asFloat) },
                \release, { synth_wind.release(msg[2]) },
                \set,   { synth_wind.set(msg[2].asString, msg[3].asFloat) },
                { format("unknown message: '%'", msg).postln });
        }, "/wind", nil, osc_port);
    }

    scene_xfader_init {
        xfader_routine = Routine {
            inf.do {
                var head_z = person1.headZ;
                if(head_z != 0) {
                    var pos = head_z.linlin(1.5, 4, -1, 1);
                    // pos.postln
                    synth_xfader.set(\pan, pos.lag(0.5));
                };
                0.1.wait;
            }
        };
    }
}
