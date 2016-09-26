Instr("filonenko.note_repeat", {
    arg in = 0, threshold = 5.0, interval = 0.2;
    var onsets, input, ain, out, trigOff, trigOn;
    input = SoundIn.ar(in);
    onsets = SLOnset.kr(input, 20, 5, 5, threshold);
    trigOff = SetResetFF.kr(Impulse.kr(0), onsets);
    trigOn = SetResetFF.kr(onsets);

    ain = input * EnvGen.kr(Env.asr(0, 1, 2), trigOff);
    out = AllpassC.ar(ain, 1, interval, 600) * Linen.kr(trigOn, 2, 10);
    out;
}, [
    \bus,
    [0, 10, \lin, 0, 5],
    [0.1, 1, \lin, 0, 0.23]
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

  