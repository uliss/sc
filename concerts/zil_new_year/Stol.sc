StolInOutScene : AbstractScene {
    var routine_in, routine_out;
    var <>stepsIn, <>stepsInOffset;
    var <>stepsOut, <>stepsOutOffset, <>stepsOutBack;

    *new {
        arg stepsIn = 20, stepsInOffset = 10, stepsOut = 18, stepsOutOffset = 10, stepsOutBack = 12;
        ^super.new.initStol(stepsIn, stepsInOffset, stepsOut, stepsOutOffset, stepsOutBack);
    }

    initStol {
        arg steps_in, steps_in_offset, steps_out, steps_out_offset, steps_out_back;
        // in
        stepsIn = steps_in;
        stepsInOffset = steps_in_offset;
        // out
        stepsOut = steps_out;
        stepsOutOffset = steps_out_offset;
        stepsOutBack = steps_out_back;

        routine_in = Routine {
            var steps_total = stepsIn + stepsInOffset;
            steps_total.do { |i|
                var tm = rrand(0.4, 0.6);
                if(i > steps_in_offset) {
                    var amp = rrand(0.4, 0.7) * i.linlin(stepsInOffset, steps_total, 0.5, 2);
                    var pan = i.linlin(stepsInOffset, steps_total, 1, 0);
                    this.step(amp, pan);
                };

                if(i == (steps_total-1)) { {this.metal}.defer(1) };

                tm.wait;
            }
        };


        routine_out = Routine {
            var steps_total = stepsOut + stepsOutOffset + stepsOutBack;
            steps_total.do { |i|
                var tm = rrand(0.4, 0.7);

                if(i > stepsOutOffset) {
                    var table_put_time = steps_total - stepsOutBack;

                    if(i < table_put_time) {
                        var from = stepsOutOffset;
                        var to = steps_total - stepsOutBack;

                        var amp = rrand(0.6, 0.8) * i.linlin(from, to, 0.9, 2);
                        var pan = i.linlin(from, to, 0, -1);
                        this.step(amp, pan);
                    }
                    // me returning back
                    {
                        var from = steps_total - stepsOutBack;
                        var to = steps_total;
                        var amp = rrand(0.4, 0.7) * i.linlin(from, to, 1.5, 0.5);
                        var pan = i.linlin(from, to, -1, 1);
                        this.step(amp, pan);
                        tm = rrand(0.3, 0.5);

                        if(i == from) { this.metal };
                    };
                };

                tm.wait;
            }
        };
    }

    start_in {
        routine_in.reset;
        routine_in.play;
    }

    start_out {
        routine_out.reset;
        routine_out.play;
    }

    stop {
        routine_in.stop;
        routine_out.stop;
    }

    step {
        arg amp, pan;
        Synth(\snowstep2, [\amp, amp, \pan, pan, \sndbuf, ~l.buffer("step-snow1")]);
    }

    metal {
        arg amp = 1, dur = 0.5, pos = -0.25, startPos = 0;
        Synth(\sample_beat, [\amp, amp, \buf, ~l.buffer("metal2"), \pos, pos, startPos: startPos, \dur, dur]);
    }
}