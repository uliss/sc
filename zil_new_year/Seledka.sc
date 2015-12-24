SeledkaScene : AbstractScene {
    var <kinectPerson1, <kinectPerson2;
    var <>threshold1, <>timeout1;
    var <>tempo;
    var routine1, pattern2, pattern3;
    var <>outOsc;
    var pattern_clock;

    *new {
        arg oscPort = 7000, outOsc, kinectPerson1, kinectPerson2, threshold1 = 0.07, timeout1 = 0.3, tempo = 120; //NetAddr("10.1.1.96", 10000)
        ^super.new("Seledka", "/seledka", oscPort).initSeledka(outOsc, kinectPerson1, kinectPerson2, threshold1, timeout1, tempo);
    }

    initSeledka {
        arg out_osc, person1, person2, th1, tm1, t;
        var bass = Bjorklund(4, 11).clump(1).collect({|v| v ++ [0, 0, 0] }).flatten;
        var onion1 = Pseq(#[1, 0.2, 0.75, 0, 0.175, 0, 1, 0.5] * 1.3);
        var onion1a = Pseq(#[0.9, 0.5] * 0.2, 2);
        var metal2 = Pseq(#[1, 1, 0, 0.1, 0.5, 0, 0, 1, 0.2] * 0.7, inf),

        outOsc = out_osc;
        kinectPerson1 = person1;
        kinectPerson2 = person2;
        threshold1 = th1;
        timeout1 = tm1;
        tempo = t;


        pattern_clock = TempoClock(tempo/60 * 4);
        pattern_clock.permanent = true;

        routine1 = Routine {
            loop {
                var acc1 = kinectPerson1.accAll;
                var acc2 = kinectPerson2.accAll;

                if(debug) { this.dbg([acc1, acc2]) };

                if([acc1, acc2].any({|v| v > threshold1})) {
                    switch((0..3).choose,
                        0, {this.beat_metal1},
                        1, {this.beat_metal2},
                        2, {this.beat_onion1},
                        3, {this.beat_onion2}
                    );
                };

                timeout1.wait;
            }
        };

        pattern2 = Pdef(\bass, Pbind(
            \instrument, \bass,
            \dur,     0.5,
            \midinote, Prand((28..33), inf),
            \amp,      Pseq(bass * 0.7, inf),
        ));

        pattern3 = Pdef(\stomp, Ppar([
            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("onion1"),
                \pos, 0.2,
                \dur, 2,
                \startPos, 1000,
                \amp,
                Pseq([
                    // init rest
                    Pseq([Rest], 16 * 2), // 32 (32)
                    Pseq([onion1], 3),    // 24 (56)
                    Pseq([[0.9, 0.5], [0.9, 0.5]] * 0.2, 2),   // 8  (64)
                    Pseq([Rest], 8),
                    Pseq([Rest], 8),      // 8  (72)
                    Pseq([onion1], inf)
                ],
                inf),
            ),


            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("onion1"),
                \pos, -0.5,
                \dur, 2,
                \startPos, 1000,
                \amp, Pseq(
                    [
                        // init rest
                        Pseq([Rest], 72), // 72
                        Pseq([onion1a], inf),
                ]),
            ),

            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("metal1"),
                \dur, 2,
                \pos, -0.25,
                \amp, Pseq([
                    // init rest
                    Pseq([Rest], 136), // 72 + 16 * 4
                    Pseq([
                        Pseq([1], 1),
                        Pseq([Rest], 40)
                    ], inf),
                ]),
            ),

            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("metal2"),
                \dur, 2,
                \pos, -0.25,
                \amp, Pseq([
                    // init rest
                    Pseq([Rest], 136 + 16),
                    metal2
                ]),
            ),

            Pbind(
                \instrument, \bass,
                \dur,      0.5,
                \midinote, Prand((28..33), inf),
                \amp,      Pseq([
                    Pseq([Rest], (136 + 16 + 36) * 4),
                    Pseq(bass, inf),
                ], inf),
            ),

            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("microwave1"),
                \dur, 8,
                \fadeOut, 3,
                \pos, Prand([-0.8, 0.8], inf),
                \startPos, 1000,
                \amp, Pseq([
                    // init offset
                    // Pseq([Rest], 0),
                    1,
                    Pseq([Rest], 4 * 40),
                ], inf),
            ),
        ]));
    }

    beat_metal1 {
        arg dur = 0.5;
        Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, dur]);
        outOsc.sendMsg("/beat1");
    }

    beat_metal2 {
        arg amp = 1, dur = 0.5, pos = -0.25, startPos = 0;
        Synth(\sample_beat, [\amp, amp, \buf, ~l.buffer("metal2"), \pos, pos, startPos: startPos, \dur, dur]);
        outOsc.sendMsg("/beat2");
    }

    beat_onion1 {
        arg amp = 1, dur = 0.5, pos = 0.2, startPos = 1000;
        Synth(\sample_beat, [\amp, amp, \buf, ~l.buffer("onion1"), \pos, pos, startPos: startPos, \dur, dur]);
        outOsc.sendMsg("/beat3");
    }

    beat_onion2 {
        arg dur = 0.5;
        Synth(\sample_beat, [\amp, 5, \buf, ~l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, dur]);
        outOsc.sendMsg("/beat4");
    }

    beat_microwave {
        arg dur = 5;
        Synth(\sample_beat, [\amp, 1, \buf, ~l.buffer("microwave1"), \dur, dur, \pos: [-0.8, 0.8].choose, \startPos, 1000]);
        outOsc.sendMsg("/beat6");
    }

    beat_bass {
        arg amp = 1, dur = 0.1;
        Synth(\bass, [\amp, amp, \freq, 25.rand + 42, \dur, dur]);
        outOsc.sendMsg("/beat5");
    }

    stop {
        routine1.stop;
        pattern2.stop;
        pattern3.stop;
    }

    start {
        arg msg = [];
        var part = 1;
        if(msg.isEmpty.not) {
            switch(msg[0],
                1, {this.play1},
                2, {this.play2},
                3, {this.play3},
                {this.play1}
            );
        }
        { this.play1 };
    }

    play1 {
        this.stop;
        routine1.reset;
        routine1.play;
    }

    play2 {
        this.stop;
        this.play1;
        pattern_clock.play;
        pattern2.play(pattern_clock);
    }

    play3 {
        this.stop;
        pattern_clock.play;
        pattern3.play(pattern_clock);
    }

    reload {
        kinectPerson1.enable;
        kinectPerson2.enable;
    }
}

