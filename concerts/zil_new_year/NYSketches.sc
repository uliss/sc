NYSketches {
    var person1, person2;
    var <outOsc;
    var <gadanie;
    var <bubbles;
    var <stol;
    var <seledka;
    var <stol_out;
    var <dvoinik;
    var <metel;
    var <drazhe;
    var <final;
    var <mixer;
    var <kuranty;

    var dump_routine;

    *new {
        arg outOsc = NetAddr("localhost", 10001);
        ^super.new.initScenes(outOsc);
    }

    dumpKinect {
        arg run = true;
        if(run) {
            dump_routine.reset;
            dump_routine.play;
        }
        {
            dump_routine.stop;
        };
    }

    initScenes {
        arg out_osc;

        outOsc = out_osc;
        person1 = Kinect.new(1);
        person2 = Kinect.new(2);

        gadanie = GadanieScene.new(person1, person2);

        bubbles = BubblesScene.new;

        stol = StolInOutScene.new;

        seledka = SeledkaScene.new(outOsc: outOsc,
            kinectPerson1: person1,
            kinectPerson2: person2,
            tempo: 120);

        seledka.outOsc = outOsc;

        dvoinik = DvoinikScene.new([\delay, 2, \amp, 1.2], kinectPerson1: person1, kinectPerson2: person2);

        kuranty = KurantyScene.new;

        drazhe = DrazheScene.new(person1, person2,  outOsc);
        // drazhe?.out = outOsc;

        metel = WindScene.new([\amp, 0.4, \fadeIn, 10], person1, person2);

        mixer = MixerScene.new(person1, person2);

        final = FinalScene.new;
        final.synthParam[\fadeIn] = 3;
        final.synthParam[\amp] = 0.25;
        final.fileName = "/Users/serj/work/music/sounds/flow_my_tears.wav";
        // final.fileName = "/Users/serj/work/music/sounds/gombert.wav"

        this.init_dump_routine;
    }

    init_dump_routine {
        dump_routine = Routine {
            inf.do {
                format("P1: head:x=% P2: head:x:%", person1.headX, person2.headX).postln;
                0.1.wait;
            }
        };
    }


}