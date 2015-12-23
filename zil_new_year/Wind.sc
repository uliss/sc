WindScene : SynthScene {
    *new {
        arg param = [], kinectPerson1, kinectPerson2;
        ^super.new("Wind", "/wind", synthName: "wind1", synthParam: param);
    }
}