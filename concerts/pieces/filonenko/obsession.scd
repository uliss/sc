(
SynthDef(\click1, {
    Out.ar([0, 1], SinOsc.ar(1600) * EnvGen.ar(Env.perc(0.01, 0.2), doneAction: 2) * 20);
}).send;

SynthDef(\click2, {
    Out.ar([0, 1], SinOsc.ar(1200) * EnvGen.ar(Env.perc(0.01, 0.2), doneAction: 2) * 10);
    // Out.ar([0, 1], Impulse.ar(0) * 20);
}).send;
)

Synth(\click1)
Synth(\click2)


NodeJS.start
NodeJS.stop

n = NetAddr("localhost", 5000);
n.sendMsg("/sc/vmetro/bar", 1);
n.sendMsg("/sc/vmetro/numBeats", 4);
n.sendMsg("/sc/vmetro/beat", 1);
n.sendMsg("/sc/vmetro/css", "background-color", "green");
n.sendMsg("/sc/vmetro/mark", "");

(
var m_colors = ["green", "blue", "red", "orange", "gray"];
x = FileReader.read("/Users/serj/work/music/sc/pieces/filonenko/seq.txt", true, startRow: 0);
TempoClock.default.tempo = 66/60.0;
n = NetAddr("localhost", 5000);
n.sendMsg("/sc/vmetro/bar", 0);
n.sendMsg("/sc/vmetro/css", "background-color", "white");

t = Routine {
    x.size.postln;
    x.size.do { |i|
        var mark, t, b, bn;
        t = x[i][2].asFloat;
        b = x[i][4].asInt;
        bn = x[i][0][1..].asInt;

        "Bar %".format(bn).postln;
        n.sendMsg("/sc/vmetro/bar", bn);
        n.sendMsg("/sc/vmetro/numBeats", b);

        if(x[i][6].notNil) {
            var idx = x[i][6].asInt;
            "[MARK: %]".format(idx).postln;
            n.sendMsg("/sc/vmetro/mark", idx);
            n.sendMsg("/sc/vmetro/css", "background-color", m_colors[idx]);
        };

        b.do { |i|
            if(i == 0) {
                Synth(\click1);
            }
            {
                Synth(\click2);
            };

            ".".post;
            n.sendMsg("/sc/vmetro/beat", i);
            t.wait;
        };

        "".postln;


    }
};
)

t.play
t.reset
t.stop
