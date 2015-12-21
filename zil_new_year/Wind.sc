WindScene : SynthScene {
    *new {
        arg param = [];
        ^super.new("Wind", "/wind", synthName: "wind1", synthParam: param);
    }
}