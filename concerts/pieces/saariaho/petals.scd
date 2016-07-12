SoundCard.saffire;
MIDIIn.connectAll;


~n = NdefMixer(s)

(
Spec.add(\amp, [0, 1]);
Spec.add(\pan, [-1, 1]);
Spec.add(\pan1, [-1, 1]);
Spec.add(\pan2, [-1, 1]);
Spec.add(\revtime, [2.5, 30]);
Spec.add(\roomsize, [0.5, 5]);
Spec.add(\room, [0, 1]);
Spec.add(\damping, [0, 1]);
Spec.add(\mix, [0, 1]);
Spec.add(\tresh, [0, 1]);
Spec.add(\slopeBelow, [0.5, 1.1]);
Spec.add(\slopeAbove, [0.1, 1]);

FreqScope.new;

Ndef(\src, {
    var in;
    in = HPF.ar(SoundIn.ar(0), 80);
    // in = BRF.ar(in, 256, 3);
    // in = SinOsc.ar(200);
    // in = Impulse.ar(2);
    // in = SPU_ViolaInTest.ar();
    in = Compander.ar(in, in,
        \tresh.kr(0.5),
        \slopeBelow.kr(1),
        \slopeAbove.kr(0.5));
    in;
});


Ndef(\cello).clear;
Ndef(\cello).fadeTime = 1;
Ndef(\cello, {
    var ain;
    ain = Ndef(\src).ar(1) * \amp.kr(0);
    Pan2.ar(ain, \pan.kr(0, 0.5));
});


Ndef(\harm).clear;
Ndef(\harm).fadeTime = 1;
Ndef(\harm, {
    var ain, p1, p2;
    ain = Ndef(\cello).ar(1) * \amp.kr(0);
    p1 = PitchShift.ar(ain, 0.20, 0.45.midiratio, 0, 0.01);
    p1 = DelayL.ar(p1, 0.02, 0.02);
    p1 = Pan2.ar(p1, \pan1.kr(0.3));
    p2 = PitchShift.ar(ain, 0.21, -0.5.midiratio, 0, 0.01);
    p2= DelayL.ar(p2, 0.02, 0.015);
    p2 = Pan2.ar(p2, \pan2.kr(-0.3));
    Mix.ar([p1, p2]);
});

Ndef(\toReverb).clear;
Ndef(\toReverb, {
    var cello, harm;
    cello = Ndef(\cello).ar();
    harm = Ndef(\harm).ar();
    Mono(Mix.ar([cello, harm]));
});

Ndef(\reverb).clear;
Ndef(\reverb).fadeTime = 1;
Ndef(\reverb2, {
    var ain;
    ain = Ndef(\toReverb).ar(1) * \amp.kr(0);
    GVerb.ar(ain, \roomsize.kr(15), \revtime.kr(2.5, 1), \damping.kr(0.5));
});

Ndef(\reverb, {
    var ain, out;
    ain = Ndef(\toReverb).ar(1) * \amp.kr(0);
    out = FreeVerb.ar(ain, \mix.kr(0.7), \room.kr(0.8), \damping.kr(0.5));
    Pan2.ar(out, 0);
});

Ndef(\reverb3).clear;
Ndef(\reverb3).fadeTime = 1;
Ndef(\reverb3, {
    var ain;
    ain = Ndef(\toReverb).ar(1) * \amp.kr(0);
    JPverb.ar(ain, \revtime.kr(2.5, 0.1), \damping.kr(0.45, 0.1), \roomsize.kr(2, 0.1)) * \amp.kr(0);
});



// CELLO ON
MIDIFunc.cc({ |m|
    if(m == 127) {
        "Cello ON".postln;
        Ndef(\cello).fadeTime = 1;
        Ndef(\cello).play;
    }
    {
        "Cello OFF".postln;
        Ndef(\cello).fadeTime = 0.1;
        Ndef(\cello).free;
    };
}, 32); // S[0]

// CELLO AMP
MIDIFunc.cc({ |m|
    var amp = m.linlin(0, 127, 0, 1).scurve;
    Ndef(\cello).set(\amp, amp);
    "Cello amp: %".format(amp.round(0.001)).postln;
}, 0);


// HARM ON
MIDIFunc.cc({ |m|
    if(m == 127) {
        "Harmonizer ON".postln;
        Ndef(\harm).fadeTime = 1;
        Ndef(\harm).play;
    }
    {
        "Harmonizer OFF".postln;
        Ndef(\harm).fadeTime = 0.1;
        Ndef(\harm).free;
    };
}, 33); // S[1]

// HARM AMP
MIDIFunc.cc({ |m|
    var amp = m.linlin(0, 127, 0, 1).scurve;
    Ndef(\harm).set(\amp, amp);
    "Harmonizer amp: %".format(amp.round(0.001)).postln;
}, 1);

// REVERB ON
MIDIFunc.cc({ |m|
    if(m == 127) {
        "Reverb ON".postln;
        Ndef(\reverb3).fadeTime = 1;
        Ndef(\reverb3).play;
    }
    {
        "Reverb OFF".postln;
        Ndef(\reverb3).fadeTime = 0.1;
        Ndef(\reverb3).free;
    };
}, 34); // S[2]

// REVERB AMP
MIDIFunc.cc({ |m|
    var amp = m.linlin(0, 127, 0, 1).scurve;
    Ndef(\reverb3).set(\amp, amp);
    "Reverb amp: %".format(amp.round(0.001)).postln;
}, 2);

// REVERB TIME
MIDIFunc.cc({ |m|
    var time = m.linexp(0, 127, 2.5, 30);
    Ndef(\reverb3).set(\revtime, time);
    "Reverb time: % sec".format(time.round(0.1)).postln;
}, 18);

MIDIFunc.cc({ |m|
    if(m == 127) {
        "MIDI trace ON".postln;
        MIDIFunc.trace(true);
    }
}, 44);

MIDIFunc.cc({ |m|
    if(m == 127) {
        "MIDI trace OFF".postln;
        MIDIFunc.trace(false);
    }
}, 43);

MIDIFunc.cc({ |m|
    if(m == 127) {
        {~n = NdefMixer(s)}.defer(0);
    }
}, 41);

// TEST TONE
MIDIFunc.cc({ |m|
    if(m == 127) {
        "TEST TONE".postln;
        {SinOsc.ar(440) * EnvGen.ar(Env.perc, doneAction: 2) * 0.2}.play;
    }
}, 46);
)













