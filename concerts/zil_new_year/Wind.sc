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

    volume {
        arg amp, time = 5;
        var steps = time * 10;
        var old_amp;
        var r;

        synth.get(\amp, { |x|
            var old_amp = x;
            var amp_diff = (old_amp - amp) / steps;
            var r;

            Routine {
                var current_amp = old_amp;
                steps.do {
                    current_amp = current_amp - amp_diff;
                    synth.set(\amp, current_amp);
                    0.1.wait;
                }
            }.play;
        }
        );
    }
}