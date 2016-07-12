Sp_Synth {
    var <name;
    var <bus;
    var <server;
    var <group;
    var monitor_;

    *new {
        arg name, bus, server;
        ^super.new.initSynth(name, bus, server);
    }

    free {
        group.freeAll;
        group = nil;
    }

    initSynth {
        arg n, b, s;
        name = n;
        bus = b;
        server = s;
        group = Group.new;
        monitor_ = Monitor.new;
    }

    monitor {
        arg value = true, volume = 1, fadeTime = 1;
        if(value) {
            monitor_.play(fromIndex: bus,
                fromNumChannels: 1,
                toIndex:0,
                toNumChannels:2,
                volume: volume,
                fadeTime: fadeTime
            )
        }
        {
            monitor_.stop;
        };
    }

    mute {
        arg value = true;
        group.run(value.not);
    }

    run {
        "implement me! (%)".postf(this.class.name);
    }
}

Sp_SynthOut : Sp_Synth {
    var <synth_out;

    *new {
        arg bus = 0, server = Server.default;
        ^super.new("spSynthOut", bus, server).initOut;
    }

    free {
        synth_out.free;
    }

    initOut {
        monitor_.free;
        group.free;
    }

    *loadSynths {
        ">>>LOAD SYNTHS: Sp_SynthOut...".postln;

        SynthDef(\spSynthOut, {
            arg amp = 1, pan = 0, in = 0, out = 0;
            var ain = In.ar(in) * amp.lag(0.1);
            Out.ar(out, Pan2.ar(ain, pan.lag(1)));
        }).add;
    }

    *initClass {
        ServerBoot.add({Sp_SynthOut.loadSynths}, \default);
    }

    run {
        arg synth, out = 0;
        synth_out = Synth.tail(synth.group, \spSynthOut, [\in, synth.bus]);
    }

    pan {
        arg pos = 0;
        synth_out.set(\pan, pos);
    }
}

Sp_SynthViolaIn : Sp_Synth {
    var <synth_viola;
    var <synth_compress;
    var <synth_filter;
    var <synth_record;
    var <synth_play;
    var <synth_vu;
    var <synth_reverb;
    var <sample_buf;
    var bus_viola_in;

    // private

    *new {
        arg bus = 100, server = Server.default, in = 0;
        ^super.new("violaIn", bus, server).violaInInit(in);
    }

    free {
        synth_viola.free;
        synth_compress.free;
        synth_filter.free;
        synth_play.free;
        synth_record.free;
        synth_reverb.free;
        synth_vu.free;
        sample_buf.free;
    }

    violaInInit {
        arg inCh;
        bus_viola_in = inCh;
    }

    *initClass {
        ServerBoot.add({Sp_SynthViolaIn.loadSynths}, \default);
    }

    *loadSynths {
        ">>>LOAD SYNTHS: Sp_SynthViolaIn...".postln;

        SynthDef(\violaIn, {
            arg in = 0, out = 0, amp = 1;
            var snd = SoundIn.ar(in, amp);
            ReplaceOut.ar(out, snd);
        }).add;

        SynthDef(\violaInFilter, {
            arg freq = 1000, amp = 1, in = 0, out = 0,
            warmFreq = 300, warmQ = 1.41, warmDb = 6,
            sharpFreq = 1200, sharpQ = 1.41, sharpDb = -5,
            formantFreq = 3000, formantQ = 1, formantDb = 12;
            // cut low freqs, lower then C grand octave
            var snd = HPF.ar(In.ar(in), 47.midicps);
            // warm
            snd = BPeakEQ.ar(snd, warmFreq, warmQ, warmDb);
            // remove sharp
            snd = BPeakEQ.ar(snd, sharpFreq, sharpQ, sharpDb);
            // vocal formant
            snd = BPeakEQ.ar(snd, formantFreq, formantQ, formantDb);
            ReplaceOut.ar(out, snd * amp);
        }).add;

        SynthDef(\violaInPlay, {
            arg amp = 1, out = 0, playBuf = 0;
            var snd = DiskIn.ar(1, playBuf, 1);
            ReplaceOut.ar(out, snd * amp);
        }).add;

        SynthDef(\violaInReverb, {
            arg amp = 0.4, in = 0, out = 0, mix = 0.5, room = 0.5, damp = 0.5;
            var a = FreeVerb.ar(In.ar(in), mix, room, damp);
            Out.ar(out, a * amp);
        }).add;

        SynthDef(\violaCompress, {
            arg amp = 1, in = 0, out = 0, thresh = 0.5, slopeBelow = 1, slopeAbove = 1/3, clampTime = 0.002;
            var ain = In.ar(in);
            var snd = Compander.ar(ain, ain, thresh, slopeBelow, slopeAbove, clampTime);
            ReplaceOut.ar(out, snd * amp);
        }).add;

        SynthDef(\vu, {
            arg in = 0;
            var ain = In.ar(in);
            var amp = Amplitude.ar(ain, 0.1, 0.1);
            SendReply.kr(Impulse.kr(8), '/violaIn/vu', amp);
        }).add;
    }

    run {
        arg value = true;

        synth_viola = Synth.new(\violaIn, [\in, bus_viola_in, \out, bus], group);
        // synth_play = Synth.after(synth_viola, \violaInPlay, [\out, bus]);
        synth_filter = Synth.after(synth_viola, \violaInFilter, [\in, bus, \out, bus]);
        synth_compress = Synth.after(synth_filter, \violaCompress, [\in, bus, \out, bus]);
        // synth_reverb = Synth.after(synth_compress, \violaInReverb, [\in, bus, \out, bus]);
        synth_vu = Synth.after(synth_filter, \vu, [\in, bus]);

/*        {
            synth_play.run(false);
            synth_reverb.run(true);
        }.defer(0.5);*/
    }

    play {
        arg value, playPos = 0;

        if(value) {
            sample_buf = Buffer.cueSoundFile(server, "~/work/music/sounds/viola_rec1.wav".standardizePath, playPos * 44100, 1);

            synth_play.set(\playBuf, sample_buf);
            synth_play.run(true);
        }
        {
            synth_play.run(false)
        }
    }

    record {
        arg msg;
        msg.postln;
    }

    filter {
        arg value;
        synth_filter.run(value);
    }

    compress {
        arg value;
        synth_compress.run(value);
    }

    reverb {
        arg value;
        synth_reverb.run(value);
    }

    vu {
        arg value;
        synth_vu.run(value);
    }
}

Sp_SynthGameBoy : Sp_Synth {
    var inChannel;
    var bus_click;
    var synth_gameboy_in;
    var synth_vu;
    var synth_click;
    var ground_stream;

    *new {
        arg bus = 110, server = Server.default, in = 5, clickBus = 5;
        ^super.new("gameBoy", bus, server).gameboyInit(in, clickBus);
    }

    gameboyInit {
        arg in, clickBus;
        inChannel = in;
        bus_click = clickBus;

        ground_stream = Pbind(
            \instrument, \gameboySynth,
            \note, Pseq([4, 11, 18, 0, 7, 14, 2, 9, 16, -3, 4, 11, 12], inf),
            \dur, Pseq([1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1] * 0.5, inf),
            \width, 0.5,
            \octave, 3,
            \group, group,
            \out, bus
        ).asEventStreamPlayer;
    }

    *initClass {
        ServerBoot.add({Sp_SynthGameBoy.loadSynths}, \default);
    }

    *loadSynths {
        ">>>LOAD SYNTHS: Sp_SynthGameBoy...".postln;

        SynthDef(\gameboySynthFile, {
            // "/Users/serj/work/music/sounds/davidson_message_ground.wav"
            // arg freq = 440, out = 0, amp = 1, width = 0.6, detune = 1, tempo;
            // var snd = Pulse.ar([freq, freq + detune], width, amp);
            arg out = 0, clickOut = 1, buf;
            var snd, click;
            snd = DiskIn.ar(2, buf, 1);
            snd = Pan2.ar(snd[1]);

            click = snd[0];

            Out.ar(out, snd * EnvGate.new);
            Out.ar(clickOut, click * EnvGate.new);
        }).add;

        SynthDef(\gameboyIn, {
            arg in = 0, out = 0, amp = 1, clickOut = 1, clickAmp = 2;
            var inB = SoundIn.ar(in, amp);
            var inC = SoundIn.ar(in + 1, clickAmp);
            Out.ar(out, inB * EnvGate.new);
            Out.ar(clickOut, inC * EnvGate.new);
        }).add;

        SynthDef(\gameboySynth, {
            arg freq = 440, out = 0, amp = 1, width = 0.6, detune = 1, tempo;
            var snd = Pulse.ar([freq, freq + detune], width, amp);
            Out.ar(out, snd * EnvGate.new);
        }).add;

        SynthDef(\gameboyClick, {
            arg out = 0, amp = 1, tempo = 144;
            var snd = Impulse.ar(tempo / 60) * EnvGate.new;
            Out.ar(out, snd);
        }).add;


    }

    playGround {
        arg value = true, tempo = 144;
        if(value) {
            var t = TempoClock.new(tempo/60);
            ground_stream.play(t, doReset: true);
            synth_click = Synth.new(\gameboyClick, [
                \out, bus_click,
                \tempo, tempo], group)
        }
        {
            ground_stream.stop;
            synth_click.free;
        };

    }

    run {
        arg value = true;

        if(value) {
            var buf = Buffer.cueSoundFile(Server.default, "/Users/serj/work/music/sounds/davidson_message_ground.wav");

            /*synth_gameboy_in = Synth.new(\gameboyIn, [
                \in, inChannel,
                \out, bus,
                \clickOut, bus_click
            ], group);*/

            synth_gameboy_in = Synth.new(\gameboySynthFile, [
                \buf, buf,
                \out, bus,
                \clickOut, bus_click
            ], group);

            synth_vu = Synth.after(synth_gameboy_in, \vu, [\in, bus]);
        }
        {
            synth_gameboy_in.free;
            synth_vu.free;
        };


    }
}