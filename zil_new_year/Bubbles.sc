BubblesScene : AbstractScene {
    var bus;
    var ndef;
    var <>fadeIn, <>fadeOut;
    var vol1, vol2;

    *new {
        arg fadeIn = 30, fadeOut = 5;
        ^super.new.initBubbles(fadeIn, fadeOut);
    }

    initBubbles {
        arg fade_in, fade_out;

        fadeIn = fade_in;
        fadeOut = fade_out;
        bus = Bus.control(Server.default, 1);
        vol1 = 0.2;
        vol2 = 0.35;

        routine = Routine{
            var cnt = 1;
            inf.do{ |i|
                var syn;
                cnt = cnt%8+1;

                if((i > 10) && (i % 4) == 0) {
                    // this.change_bass;
                };

                syn = {
                    var del = DelayN.ar(InFeedback.ar(0, 2) + (InFeedback.ar(100, 2)), 1, 1);
                    var freq = cnt * bus.kr + [0,2];
                    SinOsc.ar(freq, del[1..0])/4;
                }.play(outbus: 64);
                { syn.release(16) }.defer(9 - cnt);

                wait(9 - cnt);
            };
        };
    }


    start {
        var a = Pseq((1..10).sputter);
        bus.value = 99;

        Ndef(\bubbles, Pspawn(
            Pbind(\method, \par, \delta, 1/8, \pattern, {
                Pbind(\instrument, \bubbles,
                    \amp, 0.1,
                    \dur, a,
                    \sustain, 1/8/a,
                    \degree, a,
                    \detune, a)}))).play(vol: vol1);
        Ndef(\bubbles).fadeTime = fadeIn;

        Ndef(\drones).play(vol: vol2);
        Ndef(\drones).fadeTime = fadeIn * 4;
        Ndef(\drones, { InFeedback.ar(64, 2)} );

        routine.reset;
        routine.play;
    }

    release {
        Ndef.clear(fadeOut);
        routine.stop;
    }

    stop {
        Ndef.clear(0);
        routine.stop;
    }

    change_bass {
        var a = 100;
        bus.value = [99, 100 * 2 / 3 - 1, 50 * 3 / 4 - 1, 50 * 5 / 6 - 1].choose;
    }
}
