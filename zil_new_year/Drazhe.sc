DrazheScene : SynthScene {
    var <person1, <person2;

    *new {
        arg person1, person2;
        ^super.new.initDrazhe(person1, person2);
    }

    initDrazhe {
        arg kinectPerson1, kinectPerson2;
        person1 = kinectPerson1;
        person2 = kinectPerson2;

        super.initSynth(\grains, [
            \sndbuf, ~l.buffer("drazhe"),
            \amp, 0.4,
            \freq, 20,
            \dur, 0.1
        ]);

        routine = Routine {
            // var n = NetAddr("10.1.1.96", 10000);
            var thr_up = 0.07;
            var thr_down = 0.07;

            inf.do {
                var acc1 = person1.accAll;
                acc1.postln;

                if(acc1 > thr_up || person1.noHands) {
                    // "FREEZE OGFF".postln;
                    // synth.set(\freq, 20);
                    // synth.set(\dur, 0.1);
                    // synth.set(\run, 1);
                    this.freeze(false);
                    // n.sendMsg("/freeze", 0);
                }
                {
                    "FREEZE".postln;
                    if(acc1 < thr_down) {
                        // "FREEZE".postln;
                        // synth.set(\freq, 10);
                        // synth.set(\dur, 0.04);
                        // synth.set(\run, 0);
                        // synth.set(\run, 0);
                        this.freeze(true);
                    };
                };

                0.2.wait;
            };
        };

    }

    start {
        super.start;
        routine.reset;
        routine.play;
    }

    stop {
        super.stop;
        routine.stop;
    }

    freeze {
        arg v = true;
        var run;

        if(v) { run = 0 } { run = 1 };
        synth.set(\run, run);
    }
}