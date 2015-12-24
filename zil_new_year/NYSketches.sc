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
    var <kuranty;

    *new {
        arg outOsc = NetAddr("localhost", 10001);
        ^super.new.initScenes(outOsc);
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

        metel = WindScene.new([\amp, 0.1], ~scenes.person1, ~scenes.person2);
    }


}