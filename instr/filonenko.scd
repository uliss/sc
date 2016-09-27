Instr("filonenko.note_repeat", {
    arg in = 0, threshold = 5.0, interval = 0.2, amp = 1, rev_mix = 0.33, rev_room = 0.6, rev_damp = 0.5, pan_freq = 0.5;
    var onsets, input, ain, sig, trigOff, trigOn;
    input = SoundIn.ar(in);
    onsets = SLOnset.kr(input, 20, 5, 5, threshold);
    trigOff = SetResetFF.kr(Impulse.kr(0), onsets);
    trigOn = SetResetFF.kr(onsets);

    ain = input * EnvGen.kr(Env.asr(0, 1, 2), trigOff);
    sig = AllpassC.ar(ain, 1, interval, 600) * Linen.kr(trigOn, 2, 10);
    sig = PitchShift.ar(sig, 0.3, 0.99);
    sig = FreeVerb.ar(sig, rev_mix, rev_room, rev_damp);
    sig = Pan2.ar(sig, LFNoise0.ar(pan_freq, 0.5));
    sig;
}, [
    \bus,
    [0, 10, \lin, 0, 5],
    [0.1, 1, \lin, 0, 0.23],
    [0, 1, \amp, 0, 1],
    [0, 1, \lin, 0, 0.13],
    [0, 1, \lin, 0, 0.6],
    [0, 1, \lin, 0, 0.7],
    [0.1, 10, \lin, 0, 1]
]);


Instr("filonenko.final", {
    arg in = 0;
    var p1, p2;
    var sig = SwitchDelay.ar(in, 0, 0.3, [0.2, 0.5, 0.7, 1.3, 0.35, 0.56, 0.41], [0.7, 0.8, 0.8]);
    p1 = PitchShift.ar(sig[3], 0.2, 0.5);
    p2 = PitchShift.ar(sig[5], 0.2, 0.35);
    sig = Splay.ar(sig + [p1, p2]);
    sig = FreeVerb.ar(sig, 0.6, 0.7);
    Pan2.ar(sig);
}, [
    AudioSpec(1)
],
outSpec: AudioSpec(2)
);

Instr("filonenko.gain", {
    arg in, gain = 1;
    in * gain;
}, [
    AudioSpec(),
    [0, 20, \amp, 0, 1]
],
outSpec: AudioSpec());


Instr("filonenko.lownoise", {
    arg amp = 1;
    var a;
    a = LPF.ar(BrownNoise.ar, 3000);
    a = Resonz.ar(a, [25, 30, 37, 50, 58, 60, 100, 134]);
    a = Splay.ar(a);
    a = LeakDC.ar(a, 0.99);
    AllpassL.ar(a) * amp;
}, [
    \amp
],
outSpec: AudioSpec(2));


Instr("filonenko.gul", {
    arg amp = 1;
    var a;
    a = Klank.ar(`[[2000, 356, 671, 1153] * 0.15, nil, [1, 1, 1, 1]], BrownNoise.ar([0.05, 0.05])) * 0.05;
    a = Splay.ar(a);
    a = LeakDC.ar(a, 0.99);
    AllpassL.ar(a) * amp;
}, [
    \amp
],
outSpec: AudioSpec(2));



  