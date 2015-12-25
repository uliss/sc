DrazheScene : SynthScene {
    var <person1, <person2;
    var <>osc;
    var <>thresholdStart, <>thresholdStop;

    *new {
        arg person1, person2, osc;
        ^super.new.initDrazhe(person1, person2, osc);
    }

    initDrazhe {
        arg kinectPerson1, kinectPerson2, oscOut;
        // var n = NetAddr("alex", 10000);
        person1 = kinectPerson1;
        person2 = kinectPerson2;
        osc = oscOut;
        thresholdStart = 0.09;
        thresholdStop = 0.07;

        super.initSynth(\grains, [
            \sndbuf, ~l.buffer("drazhe"),
            \amp, 0.6,
            \freq, 20,
            \dur, 0.1
        ]);

        routine = Routine {
            var thr_up = 0.05;
            var thr_down = 0.05;
            var time_on = 0;
            var time_off = 0;

            inf.do { |i|
                var acc1 = person1.accAll;
                acc1.postln;

                if(acc1 > thr_up || (acc1 == 0)) {
                    if((i - time_off) > 4) {
                        this.freeze(false);
                    };
                    // time_on = i;
                }
                {
                    if(acc1 < thr_down) {
                        if((i - time_off) > 4) {
                            "FREEZE".postln;
                            this.freeze(true);
                        };

                        time_off = i;


                    };
                };

                0.1.wait;
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

    release {
        arg time;
        super.release(time);
        {routine.stop}.defer(time);
    }

    freeze {
        arg v = true;
        var run;

        if(v) { run = 0 } { run = 1 };
        synth.set(\run, run);
        osc.sendMsg("/freeze", v);
    }
}