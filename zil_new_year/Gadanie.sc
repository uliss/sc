GadanieScene : AbstractScene {
    var <person1, <person2;
    var <synth1, <synth2;
    var <routine1, <routine2;
    var <>accThresholdOn, <>accThresholdOff;
    var <>fadeOut, timeout;
    var arr1, arr2;

    *new {
        arg kinectPerson1, kinectPerson2;
        ^super.new.initGadanie(kinectPerson1, kinectPerson2);
    }

    initGadanie{
        arg kinectPerson1, kinectPerson2;
        person1 = kinectPerson1;
        person2 = kinectPerson2;
        accThresholdOn = 0.070;
        accThresholdOff = 0.041;
        fadeOut = 0.5;
        timeout = 0.05;

        synth1 = Synth.basicNew(\paper, Server.default);
        synth2 = Synth.basicNew(\snowstep, Server.default);

        routine1 = Routine {
            var playing = false;

            inf.do { |i|
                var acc = person1.accTop.lag(0.2);

                if(i == 0) {playing = false};

                if(playing.not && (acc > accThresholdOn)) {
                    if(debug) {acc.postln};

                    this.play_synth1;
                    playing = true;
                }
                {
                    if(playing && (acc < accThresholdOff)) {
                        this.stop_synth1;
                        playing = false;
                    };
                };

                timeout.wait;
            }
        };

        routine2 = Routine {
            var playing = false;

            inf.do { |i|
                var acc = person2.accTop.lag(0.2);

                if(i == 0) {playing = false};

                if(playing.not && (acc > accThresholdOn)) {
                    if(debug) {acc.postln};

                    this.play_synth2;
                    playing = true;
                }
                {
                    if(playing && (acc < accThresholdOff)) {
                        this.stop_synth2;
                        playing = false;
                    };
                };

                timeout.wait;
            }
        };
    }

    reload {
        routine1.reset;
        routine2.reset;
        person1.enable;
        person2.enable;
    }

    start {
        routine1.reset;
        routine2.reset;

        {
            routine1.play;
            routine2.play;
        }.defer(0.5);
    }

    stop {
        arg fadeOut = 4;
        routine1.stop;
        routine2.stop;
        synth1.release(fadeOut);
        synth2.release(fadeOut);
    }

    resume {
        this.start;
    }

    pause {
        this.stop;
    }

    play_synth1 {
        if(debug) { this.dbg("synth1 PLAY") };

        Server.default.sendBundle(nil, synth1.newMsg(nil, [
            \amp, 0.45,
            \sndbuf, ~l.buffer("paper1"),
            \fr, 4,
            \bus, 0]));
    }

    stop_synth1 {
        if(debug) { this.dbg("synth1 OFF") };
        synth1.release(fadeOut);
    }

    play_synth2 {
        if(debug) { this.dbg("synth2 PLAY") };

        Server.default.sendBundle(nil, synth2.newMsg(nil, [
            \amp, 0.15,
            \sndbuf, ~l.buffer("step-snow1"),
            \bus, 1]));
    }

    stop_synth2 {
        if(debug) { this.dbg("synth2 OFF") };
        synth2.release(fadeOut);
    }

    calibrate {
        var r;
        arr1 = Array.fill(100, {0});
        arr2 = Array.fill(100, {0});
        r = Routine {
            100.do { |i|
                var acc1 = person1.accTop.lag(0.2);
                var acc2 = person2.accTop.lag(0.2);
                arr1[i] = acc1;
                arr2[i] = acc2;
                timeout.wait;
            }
        };

        r.play();

        {
            format("CALIBRATION: min1=%,max1=% min2=%,max2=%", arr1.minItem, arr1.maxItem, arr2.minItem, arr2.maxItem).postln
        }.defer(3);
    }
}