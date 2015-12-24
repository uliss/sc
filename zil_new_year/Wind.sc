WindScene : SynthScene {
    var person1, person2;

    *new {
        arg param = [], kinectPerson1, kinectPerson2;
        ^super.new("Wind", "/wind", synthName: "wind1", synthParam: param).initWind(kinectPerson1, kinectPerson2);
    }

    initWind {
        arg p1, p2;
        person1 = p1;
        person2 = p2;
    }
}