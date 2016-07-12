Sp_SynthMessageGround : Sp_Synth {
    var <viola_in;
    var <viola_delay;
    var <viola_fx;
    var <gameboy;
    var <out_mix;
    var fx_mix;
    var fx_room;
    var fx_amp;
    var tempo;

    *new {
        arg bus = 80, server = Server.default, gameboyIn = 2, violaIn = 0, clickBus = 5, fx_mix = 0.4, fx_room = 0.6, fx_amp = 1, tempo = 144;
        ^super.new("MessageGround", bus, server).msgroundInit(gameboyIn, violaIn, clickBus, fx_mix, fx_room, fx_amp, tempo);
    }

    free {
        super.free;
        viola_in.free;
        gameboy.free;

        viola_in = nil;
        gameboy = nil;
    }

    msgroundInit {
        arg gameboyIn, violaIn, clickBus, fxMix, fxRoom, fxAmp, t;

        viola_in = Sp_SynthViolaIn.new(100, in: violaIn);
        viola_in.run;
        // viola_in.reverb(false);

        fx_mix = fxMix;
        fx_room = fxRoom;
        fx_amp = fxAmp;
        tempo = t;

        viola_delay = Synth.new(\violaGround, [\in, 100, \out, 70, \tempo, tempo], group, addAction: \addToTail);
        viola_fx = Synth.after(viola_delay, \violaGroundEff, [\in, 70, \out, 70, \amp, fx_amp, \mix, fx_mix, \room, fx_room]);
        gameboy = Sp_SynthGameBoy.new(bus: 110, in: gameboyIn, clickBus: clickBus);
        out_mix = Synth.new(\groundMix, [\violaIn, 70, \gameboyIn, 110], group, addAction: \addToTail);
    }


    *initClass {
        ServerBoot.add({Sp_SynthMessageGround.loadSynths}, \default);
    }

    *loadSynths {
        ">>>LOAD SYNTHS: Sp_SynthMessageGround...".postln;

        SynthDef(\violaGround, {
            arg in = 0, out = 0, tempo = 144, beats = 8, v1_amp = 1, v2_amp = 1;
            var time = (60.0 / tempo) * 8;
            var ain = In.ar(in);
            var v1 = Pan2.ar(ain, 0.4) * v1_amp;
            var v2 = Pan2.ar(DelayC.ar(ain, 6, time), -0.4) * v2_amp;
            var asig = v1 + v2;
            ReplaceOut.ar(out, asig);
        }).add;

        SynthDef(\violaGroundEff, {
            arg in = 0, out = 0, mix = 0.45, room = 0.75, amp = 1;
            var ain = In.ar(in, 2);
            var arev = FreeVerb.ar(ain, mix: mix, room: room, mul: amp);
            ReplaceOut.ar(out, Pan2.ar(arev) * EnvGate.new);
        }).add;

        SynthDef(\groundMix, {
            arg out = 0, violaIn = 70, gameboyIn = 71, violaAmp = 1, gameboyAmp = 1;
            var asig = Mix.new([In.ar(violaIn) * violaAmp, Pan2.ar(In.ar(gameboyIn) * gameboyAmp)]);
            Out.ar(out, asig);
        }).add;
    }

    playTestViola {
        arg value = true, time = 30;
        viola_in.play(value, time);
    }

    run {
        arg value = true, off_amp = 2, off_room = 0.8;

        if(value) {
            viola_fx.set(\room, fx_room);
            viola_fx.set(\amp, fx_amp);
        }
        {
            viola_fx.set(\room, off_room);
            viola_fx.set(\amp, off_amp);
        };

        gameboy.run(value);
        gameboy.mute(value.not);
        viola_delay.run(value);
    }
}