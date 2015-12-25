SeledkaScene : AbstractScene {
    var <kinectPerson1, <kinectPerson2;
    var <>threshold1, <>timeout1;
    var <>tempo;
    var routine1, pattern2, pattern3, pattern4;
    var <>outOsc;
    var pattern_clock;

    var p_intro, p_part1, p_part2, p_part3;
    var p_part1_onion1, p_part1_onion2;
    var p_part1_metal1, p_part1_metal2;
    var p_inter1, p_inter2;
    var p_part2_instr, p_part3_instr1;
    var p_part3_onion1, p_part3_onion2, p_part3_metal;
    var p_final1, p_final2;

    *new {
        arg oscPort = 7000, outOsc, kinectPerson1, kinectPerson2, threshold1 = 0.07, timeout1 = 0.3, tempo = 120; //NetAddr("10.1.1.96", 10000)
        ^super.new("Seledka", "/seledka", oscPort).initSeledka(outOsc, kinectPerson1, kinectPerson2, threshold1, timeout1, tempo);
    }

    initSeledka {
        arg out_osc, person1, person2, th1, tm1, t;
        var bass = Bjorklund(4, 11).clump(1).collect({|v| v ++ [0, 0, 0] }).flatten;
        var onion1 = Pseq(#[1, 0.2, 0.75, 0, 0.175, 0, 1, 0.5] * 1.3);
        var onion1a = Pseq(#[0.9, 0.5] * 0.2, 2);
        var metal2 = Pseq(#[1, 1, 0, 0.1, 0.5, 0, 0, 1, 0.2] * 0.7, inf);
        var acc_factor = 1;

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

        p_intro = Pseq([
            Pseq([Rest], 8),
            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("microwave1"),
                \dur, 8,
                \fadeOut, 3,
                \pos, Pseq([-0.8]),
                \startPos, 1000,
                \amp, Pseq([1])),
            Pseq([Rest], 8 * 6)]
        );

        p_part1_onion1 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("onion1"),
            \pos, 0.2,
            \dur, 2,
            \startPos, 1000,
            \amp,
            Pseq([
                Pseq([onion1], 3), // 3 sec
                Pseq([[0.9, 0.5], [0.9, 0.5]] * 0.2, 2), // 1 sec
                // Pseq([Rest], 8 * 2), // 2 sec
                Pseq([onion1] * 0.8, inf)
            ]),
        );

        p_part1_onion2 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("onion1"),
            \pos, -0.5,
            \dur, 2,
            \startPos, 1000,
            \amp, Pseq(
                [
                    Pseq([Rest], 8 * 5), // 5 seconds
                    Pseq([onion1a], inf),
            ]),
        );

        p_part1_metal1 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("metal1"),
            \dur, 2,
            \pos, -0.25,
            \amp, Pseq([
                Pseq([Rest], 8 * (4 + 8) - 1), // 13 seconds
                Pseq([
                    Pseq([1]),
                    Pseq([Rest], 8 * 8)
                ], inf),
            ]),
        );

        p_part1_metal2 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("metal2"),
            \dur, 2,
            \pos, -0.25,
            \amp, Pseq([
                // init rest
                Pseq([Rest], 8 * (5 + 8)),
                metal2 * 0.8
            ]),
        );

        p_part1 = Pfindur(8 * 96, // 64 seconds
            Ppar([
                p_part1_onion1,
                p_part1_onion2,
                p_part1_metal1,
                p_part1_metal2
            ])
        );

        p_inter1 = Pfin(1, Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("water1"),
            \amp, 1,
            \dur, 18
        ));

        p_part2_instr = Pbind(
            \dur, 1,
            \instrument, \sample_beat,
            \buf, Prand([~l.buffer("metal3"),
                ~l.buffer("dish1"),
                ~l.buffer("glass2"),
                ~l.buffer("beat1")], inf),
            \amp, Prand([0.6, 0.7, 0.8, 0.9] * 1.4, inf),
            \pan, Prand([-0.3, -0.1, 0, 0.1, 0.3], inf),
        );

        p_part3_instr1 = Pbind(
            \dur, Prand([0.1, 0.12, 0.05], inf),
            \instrument, \sample_beat,
            \buf, Prand([~l.buffer("metal3"),
                ~l.buffer("dish1"),
                ~l.buffer("glass2"),
                ~l.buffer("beat1")], inf),
            \amp, Prand([0.6, 0.7, 0.8, 0.9] * 0.6, inf)
        );

        p_part2 = Pfindur(8 * 32, Ppar([
            p_part2_instr
        ]));

        p_part3_onion1 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("onion1"),
            \pos, 0.2,
            \dur, 1 * acc_factor,
            \startPos, 1000,
            \amp,
            Pseq([onion1], inf),
        );

        p_part3_onion2 = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("onion1"),
            \pos, -0.5,
            \dur, 0.5 * acc_factor,
            \startPos, 1000,
            \amp, 2,
            \amp, Pseq([onion1a], inf),
        );

        p_part3_metal = Pbind(
            \instrument, \sample_beat,
            \buf, ~l.buffer("metal2"),
            \dur, 1 * acc_factor,
            \pos, -0.25,
            \amp, Pseq([metal2 * 0.8], inf),
        );


        p_part3 =  Pfindur(8 * 64, // 64 seconds
            Ppar([
                // p_part2_instr,
                p_part3_onion1,
                p_part3_onion2,
                p_part3_metal,
                Pbind(
                    \instrument, \bass,
                    \dur,      0.5 * acc_factor,
                    \midinote, Prand((28..33), inf),
                    \amp,      Pseq(bass, inf),
                )
            ])
        );

        p_inter2 = Pfin(1, Pbind(
            \dur, 8 * 4,
            \amp, 2,
            \instrument, \sample_beat,
            \buf, ~l.buffer("tick1")));

        p_final1 = Pfindur(8 * 8, Ppar([
            p_part3_instr1,
            Pbind(
                \instrument, \bass,
                \dur,      Prand([0.1, 0.15, 0.05], inf),
                \midinote, Prand((28..33), inf),
                \amp,      Pseq(bass, inf),
            ),
            Pbind(
                \instrument, \sample_beat,
                \buf, ~l.buffer("metal2"),
                \dur, Prand([0.11, 0.13, 0.07], inf) ,
                \pos, 0.25,
                \amp, 0.5
            );

        ]));

        p_final2 = Pfin(1, Pbind(
            \amp, 2,
            \dur, 16,
            \instrument, \sample_beat,
            \buf, ~l.buffer("friture1")));

        pattern3 = Pdef(\stomp, Pseq([
            p_intro,
            p_part1,
            // p_inter1,
            // p_part2,
            p_inter2,
            p_part3,
            p_final1,
            p_final2,
            p_intro,
        ]));
    }

    beat_metal1 {
        arg dur = 0.5;
        Synth(\sample_beat, [\amp, 1.3, \buf, ~l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, dur]);
        outOsc.sendMsg("/beat1", 1);
        {outOsc.sendMsg("/beat1", 0)}.defer(0.1);
    }

    beat_metal2 {
        arg amp = 1, dur = 0.5, pos = -0.25, startPos = 0;
        Synth(\sample_beat, [\amp, amp, \buf, ~l.buffer("metal2"), \pos, pos, startPos: startPos, \dur, dur]);
        outOsc.sendMsg("/beat2", 1);
        {outOsc.sendMsg("/beat2", 0)}.defer(0.1);
    }

    beat_onion1 {
        arg amp = 1, dur = 0.5, pos = 0.2, startPos = 1000;
        Synth(\sample_beat, [\amp, amp, \buf, ~l.buffer("onion1"), \pos, pos, startPos: startPos, \dur, dur]);
        outOsc.sendMsg("/beat3", 1);
        {outOsc.sendMsg("/beat3", 0)}.defer(0.1);
    }

    beat_onion2 {
        arg dur = 0.5;
        Synth(\sample_beat, [\amp, 5, \buf, ~l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, dur]);
        outOsc.sendMsg("/beat4", 1);
        {outOsc.sendMsg("/beat4", 0)}.defer(0.1);
    }

    beat_microwave {
        arg dur = 5;
        Synth(\sample_beat, [\amp, 1, \buf, ~l.buffer("microwave1"), \dur, dur, \pos: [-0.8, 0.8].choose, \startPos, 1000]);
        outOsc.sendMsg("/beat6", 1);
        {outOsc.sendMsg("/beat6", 0)}.defer(0.1);
    }

    beat_bass {
        arg amp = 1, dur = 0.1;
        Synth(\bass, [\amp, amp, \freq, 25.rand + 42, \dur, dur]);
        outOsc.sendMsg("/beat5", 1);
        {outOsc.sendMsg("/beat5", 0)}.defer(0.1);
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

