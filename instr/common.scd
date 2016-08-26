Instr("common.pan2", {
    arg in, pan = 0;
    Pan2.ar(in, pan);
}, [
    AudioSpec(1),
    [-1, 1, \lin, 0, 0]
],
outSpec: AudioSpec(2)
);

Instr("common.mute", {
    arg in, mute = 0;
    in * Linen.kr((1 - mute), 0.005, 1, 0.005);
}, [
    AudioSpec(),
    [0, 1, \lin, 1, 0];
], AudioSpec(1));

Instr("common.sfplay", {
    arg path, numChannels = 2, pos = 0, amp = 1, attackTime = 0.1, fadeTime = 0.1;
    DiskIn.ar(numChannels,
        Buffer.cueSoundFile(Server.default,
            path,
            pos * Server.default.sampleRate,
            numChannels),
        0) * amp * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
}, [
    PathSpec(),
    StaticIntegerSpec(1, 8, 1, "channels"),
    StaticIntegerSpec(0, 3600, 2, "sec"),
    [0, 10, \amp, 0, 1],
    StaticIntegerSpec(0, 20, 0.1, "sec"),
    StaticIntegerSpec(0, 20, 0.1, "sec")
]
);

Instr("common.sfplayCh", {
    arg path, channel = 0, srcChannels = 2, pos = 0, amp = 1, attackTime = 0.1, fadeTime = 0.1;
    (DiskIn.ar(srcChannels,
        Buffer.cueSoundFile(Server.default,
            path,
            pos * Server.default.sampleRate,
            srcChannels),
        0) * amp * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2)).at(channel);
}, [
    PathSpec(),
    StaticIntegerSpec(0, 4, 0, "channel"),
    StaticIntegerSpec(1, 8, 1, "source channels"),
    StaticIntegerSpec(0, 3600, 2, "sec"),
    [0, 10, \amp, 0, 1],
    StaticIntegerSpec(0, 20, 0.1, "sec"),
    StaticIntegerSpec(0, 20, 0.1, "sec")
],
outSpec: AudioSpec(1)
);


Instr("common.reverb", {
    arg in, mix = 0.53, room = 0.75, damp = 0.5;
    FreeVerb.ar(in, mix, room, damp);
}, [
    AudioSpec(1),
    [0, 1, \lin, 0, 0.55],
    [0, 1, \lin, 0, 0.75],
    [0, 1, \lin, 0, 0.5]
]);

Instr("common.freeverb2", {
    arg in, freeverb2_mix = 0.53, freeverb2_room = 0.75, freeverb2_damp = 0.5;
    FreeVerb2.ar(in[0], in[1], freeverb2_mix, freeverb2_room, freeverb2_damp);
}, [
    AudioSpec(2),
    [0, 1, \lin, 0, 0.55],
    [0, 1, \lin, 0, 0.75],
    [0, 1, \lin, 0, 0.5]
],
outSpec: AudioSpec(2)
);

Instr("common.pseudoStereo", {
    arg in;
    [
        BPeakEQ.ar(BPeakEQ.ar(in, 200, 2, -3), 1600, 2, 3),
        BPeakEQ.ar(BPeakEQ.ar(DelayL.ar(in, 0.1, 0.02), 200, 2, 3), 1600, 2, -3)
    ]
}, [
    AudioSpec(1)
],
outSpec: AudioSpec(2)
);
