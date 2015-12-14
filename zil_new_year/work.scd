// ENTRY POINT
(
BuiltIn.gui;
s = Server.default;
s.waitForBoot({
    //////////////////////////
    //  SCHUBERT SYNTH
    //////////////////////////
    SynthDef(\dvoinik, {
        arg bus = 0, buffer = nil, delay = 23, startPos = 0, pos1 = 0.5, pos2 = -0.5;
        var a1, a2, ao1, ao2;
        a1 = PlayBuf.ar(2, buffer, BufRateScale.kr(buffer), 1, startPos);
        a2 = DelayN.ar(a1, 30, delay);
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

    ////////////////////////////////////
    //     INIT & LOAD SOUND LIBRARY
    ////////////////////////////////////
    l = SoundLib.new;
    l.load("schubert");
    l.load("paper1");
    l.load("paper2");
    l.load("paper3");


    "#################".postln;
    "##    LOADED     ".postln;
    "#################".postln;
});
)



(
var scenes;
n = NetAddr("127.0.0.1", 7000);
scenes = Scenes.new(l);
scenes.scene0(
    Synth.newPaused(\paper, [\sndbuf, l.buffer("paper1"), \fr, 4, \bus, 0]),
    Synth.newPaused(\paper, [\sndbuf, l.buffer("paper2"), \fr, 4, \bus, 1])
);

scenes.scene_dvoinik(
    Synth.newPaused(\dvoinik, ["buffer", l.buffer("schubert"), "delay", 23]);
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

