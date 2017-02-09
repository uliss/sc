Piece_Davidson_Spiral : GuidoPdfMusicPiece {
    *new {
        ^super.new(this.scoresDir +/+ "Robert Davidson Spiral.pdf", "Spiral", "Robert Davidson", "/spiral").loadParams.loadTasks;
    }

    resetPatch {
        arg tempo = 140;
        this.addPatch(\viola, ["viola.in", "viola.compress", "davidson.spiral_viola_canon", "common.freeverb2"], (tempo: tempo));
        this.addPatch(\click, ["utils.click", "mix.1<2", "route.->phones|"],
            (channel: 0, bpm: tempo));
        this.syncPatchesParams;
    }

    initPatches {
        this.resetPatch;
        onPlay = {
            this.resetPatch;
            this.playPatches;
            this.startMonitor(1);
        };
        onPause = {
            this.stopPatches;
            this.stopMonitor
        };
        onStop = {
            {this.set(\viola, \freeze, 1)}.defer(5);
            patches[\click].release(5);
            this.set(\viola, \echo_times, 40);
            this.releasePatches(40);
            { this.stopMonitor }.defer(40);
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

        {
            var viola_input;
            viola_input = NodeJS_Slider.new(1, 0, 2).label_("viola in").labelSize_(20).hidden_(true);
            this.addWidget(\viola_amp, viola_input);
            this.bindW2P(\viola_amp, \viola, \amp);
        }.value;


        // VIOLA STAFF
        {
            var v1_box, v1_amp, v1_pan, v2_box, v2_amp, v2_pan, v3_box, v3_amp, v3_pan, rev_box, rev_mix, rev_room;

            // VIOLA1 BOX
            v1_box = NodeJS_VBox.new.title_("viola I").hidden_(true).borderColor_("#AAA").align_("left");
            this.addWidget(\viola1_box, v1_box);

            // VIOLA AMP
            v1_amp = NodeJS_Knob.new(1, 0, 2).size_(70).label_("amp").labelSize_(20).hidden_(true).layout_(v1_box);
            this.addWidget(\viola1_amp, v1_amp);
            this.bindW2P(\viola1_amp, \viola, \viola1_amp);

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

            // VIOLA3 BOX
            v3_box = NodeJS_VBox.new.title_("viola III").hidden_(true).borderColor_("#AAA").align_("left");
            this.addWidget(\viola3Box, v3_box);

            // VIOLA3 AMP
            v3_amp = NodeJS_Knob.new(0.9, 0, 1).size_(70).label_("amp").labelSize_(20).hidden_(true).layout_(v3_box);
            this.addWidget(\viola3_amp, v3_amp);
            this.bindW2P(\viola3_amp, \viola, \viola3_amp);

            // VIOLA3 PAN
            v3_pan = NodeJS_Pan.new(-0.3).size_(70).label_("pan").labelSize_(20).hidden_(true).layout_(v3_box);
            this.addWidget(\viola3_pan, v3_pan);
            this.bindW2P(\viola3_pan, \viola, \viola3_pan);


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

        // FEEDBACK
        {
            var fb = NodeJS_Slider.new(5, 5, 25).label_("feedback").labelSize_(20).hidden_(true);
            this.addWidget(\echo_times, fb);
            this.bindW2P(\echo_times, \viola, \echo_times);
        }.value;

        // CLICK WIDGETS
        {
            var amp;
            amp = NodeJS_Slider.new(1, 0, 1).label_("click").labelSize_(20).hidden_(true);
            this.addWidget(\clickAmp, amp);
            this.bindW2P(\clickAmp, \click, \amp);
        }.value;

        // FEEDBACK
        {
            var freeze = NodeJS_Toggle.new(0).size_(100).label_("freeze").labelSize_(20).cssStyle_((position:"fixed",left:0,bottom:"50px"));
            freeze.onValue = { |v|
                if(v == 1) {
                    patches[\click].stop;
                    this.set(\viola, \freeze, 1);
                    {this.set(\viola, \echo_times, 40)}.defer(3);
                    taskRunner.pause;
                } {
                    patches[\click].play;
                    taskRunner.resume;
                    this.set(\viola, \echo_times, 5);
                    this.set(\viola, \freeze, 0);
                };
            };
            this.addWidget(\freeze, freeze);
        }.value;

        this.addMonitorWidget;
    }
}
