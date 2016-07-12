MixerScene : SynthScene {
    var <person1, <person2;

    *new {
        arg p1, p2;
        ^super.new("Mixer", "/mixer",
            synthName: "mixer_synth",
            synthParam: [\buf1, ~l.buffer("kuranty1"), \buf2, ~l.buffer("jingle4")]).initMixer(p1, p2);
    }

    initMixer {
        arg p1, p2;
        person1 = p1;
        person2 = p2;

        routine = Routine {
            var prev_kin_pos1 = 5;
            var prev_kin_pos2 = 5;

            inf.do {
                // spine?
                var kin_pos1 = p1.headZ;
                var kin_pos2 = p2.headZ;

                if(kin_pos1 == 0) { // если сигнал потерян
                    if(prev_kin_pos1 < 1.1) { // вышли за пределы кинекта вперед
                        "P1 TOO CLOSE".postln;
                    }
                    {
                        if(prev_kin_pos1 > 4) { // если вышли за пределы кинекта назад
                            this.synthSet(\amp2, 0); // выключаем фейдер
                        } { // если кинект потерял в середине
                            "P1 LOST IN THE MIDDLE".postln;
                        };
                    };
                }
                { // нормальный сигнал кинекта
                    this.synthSet(\amp1, kin_pos1.linlin(1, 4, 1, 0));
                    // сохраняем предыдущую позицию
                    prev_kin_pos1 = kin_pos1;
                };

                if(kin_pos2 == 0) { // если сигнал потерян
                    if(prev_kin_pos2 < 1.1) { // вышли за пределы кинекта вперед
                        "P2 TOO CLOSE".postln;
                    }
                    {
                        if(prev_kin_pos2 > 4) { // если вышли за пределы кинекта назад
                            this.synthSet(\amp2, 0); // выключаем фейдер
                        } { // если кинект потерял в середине
                            "P2 LOST IN THE MIDDLE".postln;
                        };
                    };
                }
                { // нормальный сигнал кинекта
                    this.synthSet(\amp2, kin_pos2.linlin(1, 4, 1, 0));
                    // сохраняем предыдущую позицию
                    prev_kin_pos2 = kin_pos2;
                };

                0.1.wait;
            }
        };
    }

    play1 {
        routine.stop;
    }

    release {
        arg time = 6;
        super.release(time);
        {routine.stop;}.defer(time);
    }

    play2 {
        routine.stop;
        this.start;
    }

    play3 {
        // начинаем с выключенными звуками
        this.start(\amp1, 0, \amp2, 0);
        // запускаем реакцию на кинект
        routine.reset;
        routine.play;
    }

    stop {
        routine.stop;
        super.stop;
    }


}