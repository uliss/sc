NYSketches {
    var person1, person2;
    var out_osc;
    var <gadanie;
    var <bubbles;
    var <stol;
    var <seledka;
    var <stol_out;
    var <dvoinik;
    var <metel;
    var <drazhe;
    var <final;

    *new {
        arg outOsc = NetAddr("localhost", 10000);
        ^super.new.initScenes(outOsc);
    }

    initScenes {
        arg outOsc;

        out_osc = outOsc;
        person1 = Kinect.new(1);
        person2 = Kinect.new(2);

        gadanie = GadanieScene.new(person1, person2);

        bubbles = BubblesScene.new;

        stol = StolInOutScene.new;

        seledka = SeledkaScene.new(outOsc: out_osc,
            kinectPerson1: person1,
            kinectPerson2: person2,
            tempo: 120);

        dvoinik = DvoinikScene.new([], kinectPerson1: person1, kinectPerson2: person2);
    }


}