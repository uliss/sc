WindScene : SynthScene {
    var person1, person2;

    *new {
        arg param = [], kinectPerson1, kinectPerson2;
        ^super.new("Wind", "/wind", synthName: "wind1", synthParam: param).initWind(kinectPerson1, kinectPerson2);
    }

    initWind {
        arg p1, p2;
        person1 = p1;
        person2 = p2;
    }

    quiter {
        arg dest, time = 5;
        var steps = time * 10;
        var l = synth.get(\amp);
        var diff = (l - dest) / steps;
        var r;
        diff.postln;

        r = Routine {
            steps.do {
                l = l - diff;
                synth.set(\amp, l);
                0.1.wait;
            }
        };

        r.play;
    }
}