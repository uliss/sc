Instr("viola.in", {
    arg in = 0, amp = 1;
    SoundIn.ar(in) * amp;
}, [
    ScalarSpec(0, 8, \lin, 1),
    [0, 2, \amp, 0]
],
outSpec: AudioSpec(1)
);

Instr("viola.test", {
    arg pos = 2, loop = 1, amp = 1, attackTime = 0.1, fadeTime = 0.1;
    DiskIn.ar(1,
        Buffer.cueSoundFile(Server.default,
            "~/work/music/sounds/viola_rec1.wav".standardizePath,
            pos * Server.default.sampleRate,
            1),
        1) * amp * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
}, [
    StaticIntegerSpec(0, 3600, 2, "sec");
    \loop,
    [0, 4, \amp, 0],
    StaticIntegerSpec(0, 20, 0.1, "sec");
    StaticIntegerSpec(0, 20, 0.1, "sec");
],
outSpec: AudioSpec(1)
);

Instr("viola.compress", {
    arg in, thresh = 0.5, slopeBelow = 1, slopeAbove = 0.33, clampTime = 0.002;
    Compander.ar(in, in, thresh, slopeBelow, slopeAbove, clampTime);
}, [
    \monoEffect,
    [0, 1, \lin, 0, 0.5], // thresh
    [0.5, 2, \lin, 0, 1], // slopeBelow
    [0.1, 1, \lin, 0, 0.33], // slopeAbove
    [0.002, 0.010, \lin, 0, 0.002] // clampTime
],
outSpec: AudioSpec(1)
);

Instr("viola.filter", {
    arg in, warmFreq = 300, warmQ = 1.41,
    warmDb = 6, sharpFreq = 1200, sharpQ = 1.41, sharpDb = -5,
    formantFreq = 3000, formantQ = 1, formantDb = 6;

    BPeakEQ.ar( // vocal formant
        BPeakEQ.ar( // remove sharp
            BPeakEQ.ar( // warm
                HPF.ar(in, 47.midicps), // cut low freqs, lower then C grand octave
                warmFreq, warmQ, warmDb),
            sharpFreq, sharpQ, sharpDb),
        formantFreq, formantQ, formantDb);
}, [
    \monoEffect,
    [100, 500, \exp, 0, 300, 'Hz'], // warmFreq
    [1, 2, \lin, 0, 1.41],
    [-6, 6, \lin, 0, 6],
    [1000, 1500, \exp, 0, 1200, 'Hz'], // sharpFreq
    [1, 2, \lin, 0, 1.41],
    [-6, 6, \lin, 0, -5],
    [2600, 3400, \exp, 0, 3000, 'Hz'], // formantFreq
    [1, 2, \lin, 0, 1],
    [0, 10, \lin, 0, 6]
],
outSpec: AudioSpec(1)
);

Instr("viola.reverb", {
    arg in, mix = 0.5, room = 0.5, damp = 0.5;
    FreeVerb.ar(in, mix, room, damp);
}, [
    \monoEffect,
    [0, 1, \lin, 0, 0.5], // mix
    [0, 1, \lin, 0, 0.5], // room
    [0, 1, \lin, 0, 0.5]  // damp
],
outSpec: AudioSpec(1)
);

Instr("viola.pan2", {
    arg in, pos = 0;
    Pan2.ar(in, pos);
}, [
    \stereoEffect,
    \pan
],
outSpec: AudioSpec(2)
);
