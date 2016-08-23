Instr("common.pan2", {
    arg in, pan = 0;
    Pan2.ar(in, pan);
}, [
    AudioSpec(1),
    [-1, 1, \lin, 0, 0]
],
outSpec: AudioSpec(2)
);

Instr("common.sfplay", {
    arg path, numChannels = 2, pos = 0, loop = 0, amp = 1, attackTime = 0.1, fadeTime = 0.1;
    DiskIn.ar(numChannels,
        Buffer.cueSoundFile(Server.default,
            path,
            pos * Server.default.sampleRate,
            numChannels),
        1) * amp * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
}, [
    PathSpec(),
    StaticIntegerSpec(1, 8, 1, "channels"),
    StaticIntegerSpec(0, 3600, 2, "sec"),
    \loop,
    [0, 10, \amp, 0, 1],
    StaticIntegerSpec(0, 20, 0.1, "sec"),
    StaticIntegerSpec(0, 20, 0.1, "sec")
]
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
