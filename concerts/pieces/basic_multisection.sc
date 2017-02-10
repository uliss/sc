Piece_BasicControl : GuidoPieceApp {
    var <>buttons;
    var <>toggles;
    var <>faders;

     *new {
        arg title = "Basic", oscPath = "/basic/control", nButtons = 6, nToggles = 6, nFaders = 6;

        ^super.new(title, "Free Improv", oscPath).loadParams.initBasicControl(params: (buttons: nButtons, toggles: nToggles, faders: nFaders));
    }

    initBasicControl {

        arg params;
        buttons = List.new;
        toggles = List.new;
        faders = List.new;

        this.initButtons(params[\buttons]);
        this.initToggles(params[\toggles]);
        this.initFaders(params[\faders]);
    }

    resetPatch {
    }

    initPatches {
        this.resetPatch;

        onPlay = {
            this.resetPatch;
            this.startMonitor(0.4);
            this.playPatches;
        };

        onPause = {
            this.stopMonitor;
            this.stopPatches;
        };

        onStop = {
            this.releasePatches(2);
            this.stopMonitor;
        };
    }

    bindButton {
        arg n, func;
        if((n >= 0) && (n < buttons.size)) {
            buttons[n].onClick = func;
        }
        {
            "[BasicControl] INVALID buttons index: %".format(n).postln;
        };
    }

    bindFader {
        arg n, func;
        if((n >= 0) && (n < faders.size)) {
            faders[n].onValue = func;
        }
        {
            "[BasicControl] INVALID fader index: %".format(n).postln;
        };
    }

    bindToggle {
        arg n, func;
        if((n >= 0) && (n < toggles.size)) {
            toggles[n].onValue = func;
        }
        {
            "[BasicControl] INVALID toggle index: %".format(n).postln;
        };
    }


    initButtons {
        arg nButtons;

        var button_box = this.addHBox("buttons");

        nButtons.do {
            arg n;
            var btn = NodeJS_Button.new.size_(100).layout_(button_box).cssStyle_((margin: "0 10px")).label_("Button %".format(n));
            this.addWidget("btn%".format(n).asSymbol, btn);
            this.buttons.add(btn);
        };
    }

    initToggles {
        arg nToggles;

        var toggle_box = this.addHBox("toggles");

        nToggles.do {
            arg n;
            var tgl = NodeJS_Toggle.new.size_(80).layout_(toggle_box).cssStyle_((\margin: "0 10px")).label_("Toggle %".format(n));
            this.addWidget("tgl%".format(n).asSymbol, tgl);
            this.toggles.add(tgl);
        };
    }

    initFaders {
        arg nSliders;

        var vsl_box = this.addHBox("sliders");

        nSliders.do {
            arg n;
            var vsl = NodeJS_Slider.new.vertical_(true).size_(200).layout_(vsl_box).cssStyle_((margin: "0 20px")).label_("Slider %".format(n));
            this.addWidget("vsl%".format(n).asSymbol, vsl);
            this.faders.add(vsl);
        };
    }

    initUI {
        // this.addParams;
    }
}