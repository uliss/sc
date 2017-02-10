(
var path, buf, winenv;
    path = "/Users/serj/Documents/prj/Viola is my life/2016/patches/numina.wav";
    buf = Buffer.readChannel(s, path, channels: [1],
    startFrame: 44100 * 1.5,
    numFrames: 44100 * 3,
    action: { "buffer loaded".postln; });
    winenv = Env([0, 1, 0], [0.5, 0.5], [8, -8]);

~cardew = Piece_BasicControl.new(nButtons: 8, nToggles: 6, nFaders: 5);

s.onBootAdd({
    SynthDef(\birds, {
    var freq, numparts, out;
    numparts = 50;
    freq = SinOsc.kr(Array.fill(numparts, {Rand(0.05, 0.2)}),
        Array.fill(numparts, {Rand(0, 1.0)})).range(1000, 3000);

    out = LeakDC.ar(
            Pan2.ar(
                Pluck.ar(
                    WhiteNoise.ar(0.1).dup(numparts),
                    Impulse.kr(Array.fill(numparts, {Rand(10, 12)})),
                    100.reciprocal, freq.reciprocal, 2, Rand(0.01, 0.2), mul: 1),
                Array.fill(numparts, {Rand.new(-1.0, 1.0)})).sum);
    out = LPF.ar(out, \lpf.kr(10000));
    Out.ar(0, out * \amp.kr(0.1, 1) * EnvGate.new);
}).add;

    SynthDef(\drone, {
    var inA, inB, inC, out;
    var rate, env;

    inC = GrainBuf.ar(1, Dust.kr([45, 20, 40]), [0.2, 0.5, 0.21], buf, LFNoise1.kr(0.4, 0.01, 0.97), LFNoise0.kr(2, 0.01, 0.1));
    inC = Splay.ar(inC);
    inC = FreeVerb2.ar(inC, inC, 0.4, \time.kr(0.86, 0.6));

    inA = Klank.ar(`[[800, 1071, 1353, 1723], nil, [1, 1, 1, 1]], PinkNoise.ar(0.007));
    inB = Klank.ar(`[[200, 671, 1153, 1723], nil, [1, 1, 1, 1]], PinkNoise.ar([0.007, 0.007])) * 0.4;

    out = XFade2.ar(inA, inB, LFNoise1.kr(10.reciprocal)) * XLine.kr(0.0001, 0.4, 15);

    // rezons
    inC = (inC * XLine.kr(0.0001, 0.1, 30) * 0.7);
    out = XFade2.ar(out, inC, LFNoise1.kr(10.reciprocal));
    out = out * 0.2 * \amp.kr(1, 0.1) * EnvGate.new;
    Out.ar(0, out);
}).add;

    SynthDef(\shep, {
    arg amp = 0.08, freq = 440;
    var sig;
    sig = SinOsc.ar(freq.dup);
    sig = sig * amp * EnvGate.new;
    Out.ar(0, sig);
}).add;

});

~playShep = {
    arg speed = 0.015, volume = 0.5, direction = 0, centerFreqShift = 0;
    var pat0, pat1, pat2, pat3, pat4, pat5, pat6, pat7, pat8, midiArray, ampArray;

    midiArray = (6,6.01..113.99);
    ampArray = Array.newClear(10800);
    (-5400..5399).do {
        arg val, index;
        ampArray[index] = 1.000001.pow(val.pow(2).neg);
    };

    if(direction > 0, {}, {midiArray = midiArray.reverse});

    midiArray = midiArray + centerFreqShift;
    midiArray.reverse;

    pat0 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray, inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray, inf)
    ]);

    pat1 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200), inf)
    ]);

    pat2 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 2), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 2), inf)
    ]);

    pat3 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 3), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 3), inf)
    ]);

    pat4 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 4), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 4), inf)
    ]);

    pat5 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 5), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 5), inf)
    ]);

    pat6 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 6), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 6), inf)
    ]);

    pat7 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 7), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 7), inf)
    ]);

    pat8 = Pmono(*[\shep,
        dur: speed,
        amp: Pseq(ampArray.rotate(-1200 * 8), inf) *
        Pseq([
            Pseg(Pseq([0, 1], 1), Pseq([1, 0], 1), \sine),
            Pseq([1], inf)
        ], 1) *
        volume,
        midinote: Pseq(midiArray.rotate(-1200 * 8), inf)
    ]);

    Pspawner({
        arg sp;
        sp.par(pat0);
        sp.par(pat1);
        sp.par(pat2);
        sp.par(pat3);
        sp.par(pat4);
        sp.par(pat5);
        sp.par(pat6);
        sp.par(pat7);
        sp.par(pat8);
    }).play;
};

~label = GuidoLabelApp.new;
~cardew.bindButton(0, { NodeJS.css("html", "background-color", "grey");    ~label.text = "..."; });
~cardew.bindButton(1, { NodeJS.css("html", "background-color", "#5f7821"); ~label.text = "A"; });
~cardew.bindButton(2, { NodeJS.css("html", "background-color", "#5c5f9d"); ~label.text = "B"; });
~cardew.bindButton(3, { NodeJS.css("html", "background-color", "#37627b"); ~label.text = "C"; });
~cardew.bindButton(4, { NodeJS.css("html", "background-color", "#eccb7a"); ~label.text = "D"; });
~cardew.bindButton(5, { NodeJS.css("html", "background-color", "#a2807e"); ~label.text = "E"; });
~cardew.bindButton(6, { NodeJS.css("html", "background-color", "#df8dc7"); ~label.text = "F"; });
~cardew.bindButton(7, { NodeJS.css("html", "background-color", "#ffaa2f"); ~label.text = "G"; });

~cardew.bindToggle(0, { arg v;
    if(v == 1)
    { ~synth0 = Synth(\birds, [\amp, ~cardew.faders[0].value, \lpf, 6000]); }
    { ~synth0.release(4); { ~synth0 = nil }.defer(4); }
});

~cardew.bindFader(0, { arg v;
    if(~synth0.notNil) {
        ~synth0.set(\amp, v);
    };
});

~cardew.bindToggle(1, { arg v;
    if(v == 1)
    { play({ LPF.ar(Klang.ar(`[ Array.rand(6, 500.0, 3000.0) ++ Array.rand(6, 60.0, 200.0), nil, nil ], 1, 0) * 0.15.dup, 500) * EnvGen.kr(Env.perc(releaseTime: rrand(10.0, 20)), doneAction: 2) * ~cardew.faders[1].value; }); }
});

~cardew.bindToggle(2, { arg v;
    if(v == 1)
    {
        ~part_a = {
            ~part_b = fork{
                loop{
                    h = [40,45,52].choose.midicps;
                    play {
                        Splay.ar({
                            SinOsc.ar(exprand(h,h+(h/64)),0,0.2)
                        } !16 ) * LFGauss.ar(9,1/4,0,0,2) * ~cardew.faders[2].value;
                    };
                    4.wait;
                }
            };

            ~part_d = {
                5.do{
                    [[72,69,64], [70,64,62], [67,60,70], [65,60,69], [64,60,67],[65,60,69]].do{ |i|
                        x = i.postln;
                        10.wait;
                    }
                };

                10.wait;
                ~part_c.stop;
                4.wait;
                ~part_b.stop;
            }.fork;

            0.1.wait;
            ~part_c = fork{
                loop{
                    h = x.choose.midicps;
                    play {
                        Splay.ar({
                            SinOsc.ar( exprand(h-(h/128),h+(h/128)), 0,0.1)
                        }!16) * LFGauss.ar(6,1/4,0,0,2) * ~cardew.faders[2].value;
                    };
                    0.5.wait;
                }
            };
        }.fork;
    }
    {
        ~part_a.stop;
        ~part_b.stop;
        ~part_c.stop;
        ~part_d.stop;
        ~part_a.free;
        ~part_b.free;
        ~part_c.free;
        ~part_d.free;
    }
});

~cardew.bindToggle(3, { arg v;
    if(v == 1)
    {
        ~drone = Synth(\drone, [\amp, ~cardew.faders[3].value])
    } {
        ~drone.release(10);
        { ~drone = nil }.defer(10);
    };
});

~cardew.bindFader(3, { arg v;
    if(~drone.notNil) {
        ~drone.set(\amp, v);
    };
});

{s.boot}.defer(4);
)

(
a = ~playShep.(0.02, 0.1, 0, 0);
b = ~playShep.(0.02, 0.1, 0, 3.7);
c = ~playShep.(0.02, 0.1, 0, 7);
)

a.release(1)
a.xstop(1)
b.xstop(2)
c.xstop(3)
a.stop
a.suspendAll


(
~playShep.(0.02, 0.1, 0, 0);
~playShep.(0.02, 0.1, 0, 2);
~playShep.(0.02, 0.1, 0, 7);
)

(
~playShep.(0.006, 0.1, 0, -5.1);
~playShep.(0.017, 0.1, 0, 2.5);
~playShep.(0.035, 0.1, 0, 5.9);
~playShep.(0.043, 0.1, 0, 9.7);
~playShep.(0.012, 0.1, 0, 14.1);
~playShep.(0.025, 0.1, 0, 17.25);
~playShep.(0.075, 0.1, 0, 21.6);
~playShep.(0.051, 0.1, 0, 23.7);
)

(
~playShep.(0.006, 0.1, 1, -5.1);
~playShep.(0.017, 0.1, 1, 2.5);
~playShep.(0.035, 0.1, 1, 5.9);
~playShep.(0.043, 0.1, 1, 9.7);
~playShep.(0.012, 0.1, 0, 14.1);
~playShep.(0.025, 0.1, 0, 17.25);
~playShep.(0.075, 0.1, 0, 21.6);
~playShep.(0.051, 0.1, 0, 23.7);
)