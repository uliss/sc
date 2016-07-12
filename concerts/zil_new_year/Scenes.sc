Scenes {
    var soundLib;
    var <person1, <person2;

    var oscScene_xfader;
    var synth_xfader, synth_xfader_param, xfader_routine;

    var oscScene_glass;

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
                var head_z1 = person1.headZ;
                var head_z2 = person2.headZ;
                if(head_z1 != 0) {
                    var pos = head_z1.linlin(1.5, 4, -1, 1);
                    pos.postln;
                    synth_xfader.set(\pan, pos.lag(0.5));
                };
                0.1.wait;
            }
        };
    }
}
