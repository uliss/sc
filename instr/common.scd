Instr("common.in", {
    arg in = 0, in_amp = 1;
    SoundIn.ar(in) * in_amp;
}, [
    \bus,
    [0, 2, \amp, 0]
],
outSpec: AudioSpec(1)
);

Instr("common.gain", {
    arg in, gain = 1;
    in * gain;
}, [
    AudioSpec(),
    [0, 4, \amp, 0, 1]
],
outSpec: AudioSpec());

Instr("common.env", {
    arg in, env, gate;
    in * EnvGen.kr(env, gate, doneAction:2)
}, [
    AudioSpec(),
    EnvSpec(Env.perc),
    [0, 1, \lin, 1, 1]
],
outSpec: AudioSpec());

Instr("common.pan2", {
    arg in, pan = 0;
    Pan2.ar(in, pan);
}, [
    AudioSpec(1),
    [-1, 1, \lin, 0, 0]
],
outSpec: AudioSpec(2)
);

Instr("common.autopan2", {
    arg in, autopan2_period = 0;
    Pan2.ar(in, LFTri.kr(autopan2_period.reciprocal));
}, [
    AudioSpec(1),
    [0.1, 30, \lin, 0, 1]
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

Instr("common.freeverb", {
    arg in, freeverb_mix = 0.53, freeverb_room = 0.75, freeverb_damp = 0.5;
    FreeVerb.ar(in, freeverb_mix, freeverb_room, freeverb_damp);
}, [
    AudioSpec(1),
    [0, 1, \lin, 0, 0.55],
    [0, 1, \lin, 0, 0.75],
    [0, 1, \lin, 0, 0.5]
],
outSpec: AudioSpec(1)
);

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

Instr("common.recBuf", {
    arg in, recbuf_on = 0, recbuf_buf;
    RecordBuf.ar(in * Linen.kr(recbuf_on, releaseTime: 0.1), recbuf_buf.bufnumIr, 0, 1, loop: 0, trigger: recbuf_on);
    in;
}, [
    AudioSpec(),
    [0, 1, \lin, 1, 0],
    BufferProxySpec()
],
outSpec: AudioSpec());

Instr("common.playBuf", {
    arg playbuf_buf, playbuf_rate, playbuf_trig, playbuf_start, playbuf_loop;
    PlayBuf.ar(playbuf_buf.numChannels,
        playbuf_buf.bufnumIr,
        BufRateScale.kr(playbuf_buf.bufnumIr) * playbuf_rate,
        playbuf_trig,
        playbuf_start * SampleRate.ir,
        playbuf_loop);
}, [
    BufferProxySpec(),
    [0, 4, \lin, 0, 1],
    [0, 1, \lin, 1, 0],
    [0, 600, \lin, 0, 0],
    [0, 1, \lin, 1, 0]
],
outSpec: AudioSpec());

Instr("common.playCueBuf", {
    arg playcuebuf_buf, playcuebuf_rate, playcuebuf_loop;
    VDiskIn.ar(playcuebuf_buf.numChannels, playcuebuf_buf.bufnumIr, playcuebuf_rate, playcuebuf_loop);
}, [
    BufferProxySpec(),
    [0, 4, \lin, 0, 1],
    [0, 1, \lin, 1, 0]
],
outSpec: AudioSpec());

Instr("common.test_voice", {
    arg test_loop = 1;
    PlayBuf.ar(1, Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav"), loop: test_loop);
}, [
    [0, 1, \lin, 1, 1]
],
outSpec: AudioSpec(1));

Instr("common.simpleloop", {

});
