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
}

Sp_SynthViolaIn : Sp_Synth {
    var <synth_viola;
    var <synth_compress;
    var <synth_filter;
    var <synth_record;
    var <synth_play;
    var <synth_vu;
    var <synth_reverb;

    // private
    classvar sample_buf;

    *new {
        arg bus = 100, server = Server.default;
        ^super.new("violaIn", bus, server);
    }

    *loadSynths {
        sample_buf = Buffer.cueSoundFile(Server.default, "~/work/music/sounds/viola_rec1.wav".standardizePath, 0, 1);

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
            arg amp = 1, out = 0;
            var snd = DiskIn.ar(1, sample_buf, 1);
            ReplaceOut.ar(out, snd * amp);
        }).add;

        SynthDef(\violaInReverb, {
            arg amp = 0.4, in = 0, out = 0, mix = 0.5, room = 0.5, damp = 0.5;
            var a = FreeVerb.ar(In.ar(in), mix, room, damp);
            Out.ar(out, a * amp);
        }).add;

        SynthDef(\violaCompress, {
            arg amp = 1, in = 0, out = 0, thresh = 0.5;
            var ain = In.ar(in);
            var snd = Compander.ar(ain, ain, thresh, 1, 1/3, 0.002);
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

        synth_viola = Synth.new(\violaIn, [\in, 0, \out, bus], group);
        synth_play = Synth.after(synth_viola, \violaInPlay, [\out, bus]);
        synth_filter = Synth.after(synth_play, \violaInFilter, [\in, bus, \out, bus]);
        synth_compress = Synth.after(synth_filter, \violaCompress, [\in, bus, \out, bus]);
        synth_reverb = Synth.after(synth_compress, \violaInReverb, [\in, bus, \out, bus]);
        synth_vu = Synth.after(synth_reverb, \vu, [\in, bus]);

        synth_vu.run(false);
/*        synth_reverb.run(false);*/
        synth_play.run(false);
        synth_compress.run(true);
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