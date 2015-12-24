KurantyScene : SynthScene {
    *new {
        arg param = [];
        ^super.new("Kuranty", "/kuranty", synthName: "mono_player", synthParam:
            [\buf, ~l.buffer("kuranty1")] ++ param);
    }
}