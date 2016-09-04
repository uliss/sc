Piece_Davidson_Message_Ground : GuidoSheetMusicPiece {
    var mute_button;

    *new {
        arg options = [];
        ^super.new(this.scoresDir +/+ "Robert Davidson Message Ground.pdf", "Message Ground", "Robert Davidson", "/sc/msg_ground", options).loadParams;
    }

    resetPatch {
        this.addPatch(\viola, ["viola.in", "viola.compress", "davidson.msg_ground_viola", "common.mute", "common.freeverb2"], (tempo: 140));
        this.addPatch(\track, ["common.sfplayCh", "common.pan2", "common.mute"],
            (channel: 1, path: "/Users/serj/work/music/sounds/pieces/davidson_message_ground_nintendo_track.wav"));
        this.addPatch(\click, ["common.sfplayCh", "common.mute", "mix.1<2", "route.->phones|"],
            (channel: 0, path: "/Users/serj/work/music/sounds/pieces/davidson_message_ground_nintendo_track.wav"));
    }

    initPatches {
        this.resetPatch;
        onPlay = {
            this.resetPatch;
            this.syncPatchesParams;
            this.playPatches;
            this.startMonitor(0.2);
        };
        onPause = {
            this.stopPatches;
            this.stopMonitor;
            mute_button.remove;
        };
        onStop = {
            this.releasePatches(2);
            { mute_button.remove; this.stopMonitor }.defer(2);
        };
    }

    initUI {
        var control;

        // PLAY CONTROL
        control = NodeJS_Playcontrol.new(false, false, false, 10).parent_('ui-piece-toolbar');
        control.onPlay = { this.play };
        control.onStop = { this.stop };
        control.onPause = { this.pause };
        this.addWidget(\playControl, control);

        // TRACK STAFF
        {
            var amp;

            // TRACK AMP
            amp = NodeJS_Slider.new(0.3, 0, 1).label_("track").labelSize_(20).hidden_(true);
            this.addWidget(\trackAmp, amp);
            this.bindW2P(\trackAmp, \track, \amp);
        }.value;


        // VIOLA STAFF
        {
            var v1_box, v1_amp, v1_pan, v2_box, v2_amp, v2_pan, rev_box, rev_mix, rev_room;

            // VIOLA1 BOX
            v1_box = NodeJS_VBox.new.title_("viola I").hidden_(true).borderColor_("#AAA").align_("left");
            this.addWidget(\viola1Box, v1_box);

            // VIOLA AMP
            v1_amp = NodeJS_Knob.new(1, 0, 2).size_(70).label_("amp").labelSize_(20).hidden_(true).layout_(v1_box);
            this.addWidget(\violaAmp, v1_amp);
            this.bindW2P(\violaAmp, \viola, \amp);

            // VIOLA1 PAN
            v1_pan = NodeJS_Pan.new(0.3).size_(70).label_("pan").labelSize_(20).hidden_(true).layout_(v1_box);
            this.addWidget(\viola1_pan, v1_pan);
            this.bindW2P(\viola1_pan, \viola, \viola1_pan);

            // VIOLA2 BOX
            v2_box = NodeJS_VBox.new.title_("viola II").hidden_(true).borderColor_("#AAA").align_("left");
            this.addWidget(\viola2Box, v2_box);

            // VIOLA2 AMP
            v2_amp = NodeJS_Knob.new(0.9, 0, 1).size_(70).label_("amp").labelSize_(20).hidden_(true).layout_(v2_box);
            this.addWidget(\viola2_amp, v2_amp);
            this.bindW2P(\viola2_amp, \viola, \viola2_amp);

            // VIOLA2 PAN
            v2_pan = NodeJS_Pan.new(-0.3).size_(70).label_("pan").labelSize_(20).hidden_(true).layout_(v2_box);
            this.addWidget(\viola2_pan, v2_pan);
            this.bindW2P(\viola2_pan, \viola, \viola2_pan);

            // REVERB BOX
            rev_box = NodeJS_VBox.new.title_("viola reverb").hidden_(true).borderColor_("#AAA").align_("left");
            this.addWidget(\viola_reverb_box, rev_box);

            // REVERB MIX
            rev_mix = NodeJS_Knob.new(0.5, 0, 1).size_(70).label_("mix").labelSize_(20).hidden_(true).layout_(rev_box);
            this.addWidget(\viola_reverb_mix, rev_mix);
            this.bindW2P(\viola_reverb_mix, \viola, \freeverb2_mix);

            // REVERB ROOM
            rev_room = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(rev_box);
            this.addWidget(\viola_reverb_room, rev_room);
            this.bindW2P(\viola_reverb_room, \viola, \freeverb2_room);
        }.value;

        // CLICK WIDGETS
        {
            var amp;
            amp = NodeJS_Slider.new(0.2, 0, 0.5).label_("click").labelSize_(20).hidden_(true);
            this.addWidget(\clickAmp, amp);
            this.bindW2P(\clickAmp, \click, \amp);
        }.value;

        this.addMonitorWidget;

        {
            // ALL MUTE
            mute_button = NodeJS_Toggle.new(0)
                .size_(300)
                .label_("mute")
                .labelSize_(60)
                .labelColor_("#FF5550");

            mute_button.onValue = { |v|
                this.set(\viola, \mute, v);
                this.set(\track, \mute, v);
                this.set(\click, \mute, v);
            };

            this.addNamedTaskAction(\showMuteToggle, { mute_button.add });
        }.value;
    }

    syncTitle {
        NodeJS.sendMsg("/node/title", "");
    }

    initPageTurns {
        this.loadPageTurns(this.class.turnsDir +/+ "davidson_message_ground.txt");
    }
}
