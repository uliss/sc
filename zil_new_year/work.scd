// ENTRY POINT
(
BuiltIn.gui;
//Saffire.gui;
s = Server.default;
s.waitForBoot({
    //////////////////////////
    //  SCHUBERT SYNTH
    //////////////////////////
    SynthDef(\dvoinik, {
        arg bus = 0, buffer = nil, delay = 23, startPos = 0, pos1 = 0.5, pos2 = -0.5;
        var a1, a2, ao1, ao2;
        a1 = PlayBuf.ar(2, buffer, BufRateScale.kr(buffer), 1, startPos);
        // a2 = DelayN.ar(a1, 30, delay);
        a2 = Silent.ar(2);

        ao1 = Balance2.ar(a1[0], a1[1], pos1.lag(1), 0.65);
        ao2 = Balance2.ar(a2[0], a2[1], pos2.lag(1), 0.8);
        Out.ar(bus, Mix.new([ao1, ao2]));
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
            levelScale: amp,
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

        // asig1 = PlayBuf.ar(2, buf1);
        // asig1 = Pan2.ar(Crackle.ar(1.95, 0.5));
        asig1 = Pan2.ar(CuspN.ar(120, 1.0, 1.99) * 0.3);
        asig2 = PlayBuf.ar(2, buf2);
        out = XFade2.ar(asig1, asig2, pan);
        Out.ar(0, out);
    }).add;

    ////////////////////////////////////
    //     INIT & LOAD SOUND LIBRARY
    ////////////////////////////////////
    l = SoundLib.new;
    l.load("schubert");
    l.load("paper1");
    l.load("paper2");
    l.load("paper3");
    l.load("onion1");
    l.load("onion2");
    l.load("metal1");
    l.load("metal2");
    l.load("microwave1");
    l.load("microwave2");
    l.load("rice1");
    l.load("jingle4");


    "#################".postln;
    "##    LOADED     ".postln;
    "#################".postln;
});
)


(
var scenes;
n = NetAddr("127.0.0.1", 7000);
k = Kinect.new;
scenes = Scenes.new(l);
scenes.scene0(
    Synth.newPaused(\paper, [\amp, 0.3, \sndbuf, l.buffer("paper1"), \fr, 4, \bus, 0]),
    Synth.newPaused(\paper, [\amp, 2, \sndbuf, l.buffer("rice1"), \fr, 4, \bus, 1])
);

scenes.scene_dvoinik(
    Synth.newPaused(\dvoinik, ["buffer", l.buffer("schubert"), "delay", 23]);
);

scenes.scene_xfader(
    Synth.newPaused(\xfader_synth, [\buf1, l.buffer("schubert"),
        \buf2, l.buffer("jingle4")]);

);
)

/////////////////////////////////
/// GADANIE  MANUAL CONTROL
/////////////////////////////////

// PERSON 1
n.sendMsg("/gadanie/0", 1);
n.sendMsg("/gadanie/0", 0);
// PERSON 2
n.sendMsg("/gadanie/1", 1);
n.sendMsg("/gadanie/1", 0);

/////////////////////////////////
/// DVOINIK  MANUAL CONTROL
/////////////////////////////////
n.sendMsg("/dvoinik", \start);
n.sendMsg("/dvoinik", \stop);
n.sendMsg("/dvoinik", \pos1, -1);
n.sendMsg("/dvoinik", \pos1, 1);
n.sendMsg("/dvoinik", \pos2, 1);
n.sendMsg("/dvoinik", \pos2, -1);

/////////////////////////////////
/// FADER  MANUAL CONTROL
/////////////////////////////////
n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
n.sendMsg("/xfader", \pos, -1);
n.sendMsg("/xfader", \pos, -0.9);
n.sendMsg("/xfader", \pos, 0);
n.sendMsg("/xfader", \pos, 0.7);

(
// Bjorklund(6, 11);
var bass = Pseq(Bjorklund(4, 11), inf).asStream;
// var onion = Pseq(Bjorklund(4, 7), inf).asStream;
// var metal1 = Pseq(Bjorklund(4, 9).shift(3), inf).asStream;
// var metal2 = Pseq(Bjorklund(3, 13).shift(2), inf).asStream;
// var bass = Pseq(Bjorklund(3, 8), inf).asStream;
var onion = Pseq(#[1, 0.1, 0.75, 0, 0.175, 0, 1, 0.5] * 1.3, inf).asStream;
var metal1 = Pseq(#[1, 1, 0, 0.1, 0.5, 0, 0, 1, 0.2], inf).asStream;
var metal2 = Pseq(#[1, 0.1, 0.1, 1, 0.25, 0.1, 0.75], inf).asStream;
var microwave = Pseq(#[1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0] * 4, inf).asStream;

var swing = 0.01; // swing amount
var times = [1 + swing, 1 - swing, 1 + swing, 1 - swing]; // add swing
r = Routine({
    var i = 0;
    var perc = 210; // 210, 50
    loop({
        var tm = times.wrapAt(i) * (0.1 + (0.1 * (perc/ 100.0)));

        // if((i < 64) || (i > 90)) {

            // if(i > 160) {
            // Synth(\bass, [\amp, bass.next * 0.5, \freq, 25.rand + 42, \dur, tm * 4]);
            // };

            // if(i > 16) {
            // Synth(\sample_beat, [\amp, onion.next, \buf, l.buffer("onion1"), \pos: 0.2, \dur, tm * 10, \startPos: 1000]);
            // };

            // if(i > 100) {
                Synth(\sample_beat, [\amp, metal1.next, \buf, l.buffer("metal2"), \dur, tm * [2, 3, 1, 10].choose, \pos: -0.25]);
            // };

            // if(i > 130) {
            // Synth(\sample_beat, [\amp, metal2.next, \buf, l.buffer("metal1"), \dur, tm * [2, 3, 1].choose, \pos: -0.25]);
        // };

            // if(i % 28 == 0) {
                // Synth(\sample_beat, [\amp, microwave.next, \buf, l.buffer("microwave1"), \dur, tm * 16, \pos: [-0.8, 0.8].choose, \startPos, 1000]);
            // };

        // };

        // if((i > 64) && (i < 90)) {
        // Synth(\sample_beat, [\amp, microwave.next, \buf, l.buffer("microwave1"), \dur, tm * 64, \pos: [-0.8, 0.8].choose, \startPos, 1000]);
    // };

        i.postln;

        /*       if(i % 8 == 0) {
        if(perc > 0) {
        perc = perc - 10;
        };
        };*/



        // Synth(\metal2, [\amp, metal2.next * 0.3]);
        // Synth(\onion3, [\amp, onion3.next]);

        tm.wait;
        i = i + 1;
    })
}).play;
)


Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("metal2"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("onion1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
Synth(\sample_beat, [\amp, 5, \buf, l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, 1]);



