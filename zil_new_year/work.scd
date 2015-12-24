// ENTRY POINT
(
// BuiltIn.gui;
Saffire.gui;
s = Server.default;
s.waitForBoot({
    //////////////////////////
    //  SCHUBERT SYNTH
    //////////////////////////
    SynthDef(\dvoinik, {
        arg bus = 0, amp = 1, buffer = nil, delay = 23, startPos = 0, pos1 = 0.5, pos2 = -0.5;
        var a1, a2, ao1, ao2, ares, amix;
        a1 = PlayBuf.ar(2, buffer, BufRateScale.kr(buffer), 1, startPos);
        a2 = DelayN.ar(a1, 30, delay);
        // a2 = Silent.ar(2);

        ao1 = Balance2.ar(a1[0], a1[1], pos1.lag(1), 0.65);
        ao2 = Balance2.ar(a2[0], a2[1], pos2.lag(1), 0.8);
        amix = Mix.new([ao1, ao2]);
        ares = Compander.ar(amix, amix, 0.5, slopeBelow: 0.8, slopeAbove: 0.6);

        Out.ar(bus, ares * EnvGate.new * amp);
    }).add;

    //////////////////////////
    //   PAPER SYNTH
    //////////////////////////
    SynthDef(\paper, {
        arg bus = 0, gate = 1, amp = 1, sndbuf, envbuf = -1, fr = 0;
        var b, osc, out, amp_env, dur, env, pan = 0, pos, freq, msg_prev;
        env = EnvGen.kr(
            Env([0, 1, 0], [1, 1] * 2, \sin, 1),
            gate,
            levelScale: amp.lag(0.5),
            doneAction: 2);
        // freq = MouseX.kr(0, 10);


        // freq = 0;
        msg_prev = 0;
        // pos = LFNoise2.kr(0.1).range(0, 10);
        pos = 2;

        Out.ar(bus,
            GrainBuf.ar(1, Impulse.kr(fr), 1, sndbuf, LFNoise1.kr.range(0.5, 2),
                pos, 2, pan, envbuf) * env);

    }).add;

    SynthDef(\snowstep, {
        arg bus = 0, amp = 1, sndbuf;
        var out = PlayBuf.ar(1, sndbuf, doneAction: 2) * EnvGate.new * amp;
        Out.ar(bus, out);
    }).add;


    SynthDef(\snowstep2, {
        arg bus = 0, amp = 1, pan = 0.5, sndbuf;
        var out = PlayBuf.ar(1, sndbuf, doneAction: 2) * EnvGate.new * amp;
        Out.ar(bus, Pan2.ar(out, pan));
    }).add;

    SynthDef(\bubbles, {
        arg out = 0, freq = 400, amp = 0.1, gate = 1, detune = 0;
        var env = EnvGen.ar(Env.perc(Rand(0.001, 0.01), LinRand(0.2, 0.4), amp, Rand(-9, -1)), doneAction: 2);
        var snd = Mix(SinOsc.ar(freq + [0, detune * 0.1], env * 2pi));
        Out.ar(out, Pan2.ar(snd * env, Rand(-1, 1)));
    }).add;

    SynthDef(\grains, {
        arg bus = 0, gate = 1, amp = 1, sndbuf, dur = 1, rate = 1, freq = 1, run = 1;
        var out, env, pos;
        env = EnvGen.kr(
            Env([0, 1, 0], [1, 1] * 2, \sin, 1),
            gate,
            levelScale: amp,
            doneAction: 2);
        pos = Sweep.ar(1, 1/BufDur.kr(sndbuf) * run);
        out = GrainBuf.ar(1, Impulse.kr(freq), dur, sndbuf, rate, pos, 2) * env;
        Out.ar(bus, Pan2.ar(out));
    }).add;

    // STOMP BASS
    SynthDef(\bass, {
        arg amp = 0.5, dur = 0.35, freq = 50.rand + 40;
        var amp_env, phase_env, phase;

        amp_env   = EnvGen.ar(Env.perc(1e-6,dur), doneAction:2);
        phase_env = EnvGen.ar(Env.perc(1e-6,0.325));

        phase = SinOsc.ar(20,0,pi) * phase_env;
        Out.ar(0, SinOsc.ar([freq, 1.01*freq],phase) * amp_env * amp);
    }).add;

    SynthDef(\sample_beat, {
        arg amp = 0.5, pos = 0, buf, startPos = 0, dur = [1.0625, 1.125, 1.25].choose;
        var amp_env, out;
        amp_env = EnvGen.ar(Env.perc(1e-6, dur), doneAction:2);

        out = PlayBuf.ar(1, buf, startPos: startPos) * amp_env * amp;
        Out.ar(0, Pan2.ar(out, pos));
    }).add;

    SynthDef(\xfader_synth, {
        arg amp = 0.5, pan = 0, buf1, buf2;
        var asig1, asig2, out;

        asig1 = Pan2.ar(PlayBuf.ar(1, buf1), -0.3);
        asig2 = PlayBuf.ar(1, buf2);
        out = XFade2.ar(asig1, asig2, pan) * EnvGate.new * amp;
        Out.ar(0, out);
    }).add;

    SynthDef(\mixer_synth, {
        arg amp1 = 0.5, amp2 = 0.5, pan = 0, buf1, buf2;
        var asig1, asig2, out;

        asig1 = Pan2.ar(PlayBuf.ar(1, buf1, loop: 1), -0.3) * amp1;
        asig2 = PlayBuf.ar(2, buf2) * amp2;
        out = Mix.ar([asig1, asig2]) * EnvGate.new;
        Out.ar(0, out);
    }).add;

    SynthDef(\glass, {
        arg bus = 0, amp = 1, pan = 0, sndbuf, rate = 1;
        var asig = Pan2.ar(PlayBuf.ar(1, sndbuf, rate) * EnvGate.new * amp, pan);
        Out.ar(bus, asig);
    }).add;

    SynthDef(\wind1, {
        arg amp = 0.5, pan = 0, rate1 = 3.9, min1 = 300, max1 = 3000, rate2 = 1.1, min2 = 0.7, max2 = 1;
        var out, freq, res, out2;
        freq = LFNoise1.kr(rate1.reciprocal).linexp(0, 1, min1, max1);
        res = LFNoise1.kr(rate2).linlin(0, 1, min2, max2);
        out = DFM1.ar(PinkNoise.ar(mul: 0.5), freq, res) * EnvGate.new * amp;
        Out.ar(0, Pan2.ar(out, pan));

    }).add;

    SynthDef(\mono_player, {
        arg amp = 0.5, pan = 0, buf = -1;
        var out = PlayBuf.ar(1, buf) * EnvGate.new * amp;
        Out.ar(0, Pan2.ar(out, pan));
    }).add;

    SynthDef(\glasses, {
        arg amp = 0.5, pan = 0, buf = -1;
        var out = PlayBuf.ar(1, buf) * EnvGate.new * amp;
        Out.ar(0, Pan2.ar(out, pan));
    }).add;

    ////////////////////////////////////
    //     INIT & LOAD SOUND LIBRARY
    ////////////////////////////////////
    ~l = SoundLib.new;
    ~l.load("schubert");
    ~l.load("paper1");
    ~l.load("paper2");
    ~l.load("paper3");
    ~l.load("onion1");
    ~l.load("onion2");
    ~l.load("metal1");
    ~l.load("metal2");
    ~l.load("microwave1");
    ~l.load("microwave2");
    ~l.load("rice1");
    ~l.load("jingle4");
    ~l.load("drazhe");
    ~l.load("kuranty1");
    ~l.load("kuranty2");
    ~l.load("step-snow1");
    ~l.load("glass1");


    "#################".postln;
    "##    LOADED     ".postln;
    ~l.listAll;
    "#################".postln;
});

n = NetAddr("127.0.0.1", 7000);

nil;
)


(
var k1 = Kinect.new(1);
b = Synth.new(\bubbles, [\sndbuf, ~l.buffer("jingle4"), \amp, 0.4]);
b.set(\freq, 10);
// b.set(\dur, 0.1);
b.set(\stop, 1);
b.set(\run, 1);
b.set(\stop, 0);
// b.set(\rate, 4);

r = Routine {
    inf.do {
        |i|
        var acc1 = k1.accAll;
        acc1.postln;

        if((acc1 > 0.07 || k1.noHands), {
            b.set(\freq, 20);
            b.set(\dur, 0.1);
            b.set(\run, 1);

        }, {
            "drazhe".postln;
            b.set(\freq, 10);
            b.set(\dur, 0.04);
            b.set(\run, 0);
        });
        0.2.wait;
    }
}.play;

)

r.play;
r.stop;




Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal2"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("onion1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 5, \buf, ~l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, 1]);



