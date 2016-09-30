Piece_Filonenko_JacksonG : GuidoPieceApp {
    var bproxy;

    *new {
        arg time = 2, bassMic = 0, vocalMic = 1;
        ^super.new("Jackson(G)", "Alexandra Filonenko", "/jacksong",
            params: (fadeTime: time, vocalMic: vocalMic, bassMic: bassMic)
        ).loadParams;
    }

    clearLoop {
        bproxy.buffer.zero;
    }

    initPatches {
        arg params;
        params.postln;

        bproxy = BufferProxy.new(44100 * 10);

        this.addPatch(\voice, ["common.in", "common.compress", "common.env", "common.pan2", "common.freeverb2", "route.split"],
            (in: params[\vocalMic], env: Env.asr(0.5, 1, params[\fadeTime]), split_bus: 2));
        this.addPatch(\bass,  ["common.in", "common.pan2", "common.freeverb2"],
            (in: params[\bassMic]));
        this.addPatch(\loop, [ "filonenko.note_repeat", "filonenko.gain", "common.freeverb", "common.autopan2", "route.split"], (in: params[\bassMic], split_bus: 2));
        this.addPatch(\final, ["common.in", "filonenko.final", "common.gain", "common.env", "route.split"],
            (in: params[\bassMic],
                env: Env.asr(2, 1, 2),
                split_bus: 2));

        this.addPatch(\noise1, ["filonenko.lownoise", "common.env", "route.split"],
            (env: Env.asr(5, 1, 2), split_bus: 2));
        this.addPatch(\noise2, ["filonenko.gul", "common.env", "route.split"],
            (env: Env.asr(5, 1, 2), split_bus: 2));

        onPlay = {
            // this.playPatches;
            this.patch(\voice).play;
            this.widget(\voiceOn).value = 1;
            this.patch(\bass).play;
            this.widget(\bassOn).value = 1;
        };

        onPause = {
            this.stopPatches;
            this.widget(\voiceOn).value = 0;
            this.widget(\bassOn).value = 0;
        };

        onStop = {
            this.releasePatches(params[\fadeTime]);
        };
    }

    initUI {
        {
            var start = NodeJS_Toggle.new(0).size_(120).label_("START");
            start.onValue = { |v|
                if(v == 1) { this.play } { this.stop };
            };
            this.addWidget(\start, start);
        }.value;


        {
            var voice_box = this.addVBox("voice");
            var main_box;

            {
                var box, on;
                box = NodeJS_HBox.new.layout_(voice_box);
                this.addWidget(\voiceToggleBox, box);

                on = NodeJS_Toggle.new.size_(40).label_("on").labelSize_(20).layout_(box);
                this.addWidget(\voiceOn, on);
                on.onValue = { |v|
                    if(v == 1) { this.patch(\voice).play } { this.patch(\voice).release(0.1) }
                };
            }.value;


            main_box = NodeJS_HBox.new.layout_(voice_box);
            this.addWidget(\voiceMainBox, main_box);

            {

                var voice_amp;
                var voice_pan;
                var box;

                box = NodeJS_VBox.new.layout_(main_box).align_(\center);
                this.addWidget(\box1, box);

                voice_pan = NodeJS_Pan.new.label_("").layout_(box);
                this.addWidget(\voicePan, voice_pan);
                this.bindW2P(\voicePan, \voice, \pan);

                voice_amp = NodeJS_Slider.new(1, 0, 4).label_("amp").layout_(box);
                this.addWidget(\voiceAmp, voice_amp);
                this.bindW2P(\voiceAmp, \voice, \in_amp);
            }.value;

            {
                var box;
                var thresh;
                var slopeBelow;
                var slopeAbove;

                box = NodeJS_VBox.new.title_("compress").layout_(main_box).align_(\center);
                this.addWidget(\compressBox, box);

                thresh = NodeJS_Knob.new.label_("thresh").labelSize_(20).size_(70).layout_(box);
                this.addWidget(\voiceCompressThresh, thresh);
                this.bindW2P(\voiceCompressThresh, \voice, \compress_thresh);

                slopeBelow = NodeJS_Knob.new(1, 0.5, 1.5).label_("slope below").labelSize_(20).size_(70).layout_(box);
                this.addWidget(\voiceCompressBelow, slopeBelow);
                this.bindW2P(\voiceCompressBelow, \voice, \compress_slopeBelow);

                slopeAbove = NodeJS_Knob.new.label_("slope above").labelSize_(20).size_(70).layout_(box);
                this.addWidget(\voiceCompressAbove, slopeAbove);
                this.bindW2P(\voiceCompressAbove, \voice, \compress_slopeAbove);

            }.value;

            {
                var box2;
                var voice_room;
                var voice_mix;
                var voice_damp;

                box2 = NodeJS_VBox.new.layout_(main_box).align_(\center).title_("reverb");
                this.addWidget(\box2, box2);

                voice_room = NodeJS_Knob.new(0.5, 0, 0.9).size_(70).labelSize_(20).label_("room").layout_(box2);
                this.addWidget(\voiceRoom, voice_room);
                this.bindW2P(\voiceRoom, \voice, \freeverb2_room);

                voice_mix = NodeJS_Knob.new(0.5, 0, 0.5).size_(70).labelSize_(20).label_("mix").layout_(box2);
                this.addWidget(\voiceMix, voice_mix);
                this.bindW2P(\voiceMix, \voice, \freeverb2_mix);

                voice_damp = NodeJS_Knob.new(0.5, 0, 0.9).size_(70).labelSize_(20).label_("damp").layout_(box2);
                this.addWidget(\voiceDamp, voice_damp);
                this.bindW2P(\voiceDamp, \voice, \freeverb2_damp);
            }.value;
        }.value;

        {
            var bass_box = this.addHBox("bass");

            {
                var box, on, amp;

                box = NodeJS_VBox.new.align_(\center).layout_(bass_box);
                this.addWidget(\bassBoxToggle, box);

                on = NodeJS_Toggle.new.size_(40).label_("on").labelSize_(20).layout_(box);
                this.addWidget(\bassOn, on);
                on.onValue = { |v|
                    if(v == 1) { this.patch(\bass).play } { this.patch(\bass).stop }
                };

                amp = NodeJS_Slider.new(0.5).label_("amp").layout_(box);
                this.addWidget(\bassAmp, amp);
                this.bindW2P(\bassAmp, \bass, \in_amp);

            }.value;

            {
                var box;
                var bass_room;
                var bass_mix;
                var bass_damp;

                box = NodeJS_VBox.new.layout_(bass_box).align_(\center);
                this.addWidget(\bassBox3, box);

                bass_room = NodeJS_Knob.new(0.5, 0, 0.9).size_(70).labelSize_(20).label_("room").layout_(box);
                this.addWidget(\bassRoom, bass_room);
                this.bindW2P(\bassRoom, \bass, \freeverb2_room);

                bass_mix = NodeJS_Knob.new(0.5, 0, 0.9).size_(70).labelSize_(20).label_("mix").layout_(box);
                this.addWidget(\bassMix, bass_mix);
                this.bindW2P(\bassMix, \bass, \freeverb2_mix);

                bass_damp = NodeJS_Knob.new(0.5, 0, 0.9).size_(70).labelSize_(20).label_("damp").layout_(box);
                this.addWidget(\bassDamp, bass_damp);
                this.bindW2P(\bassDamp, \bass, \freeverb2_damp);
            }.value;
        }.value;

        {
            var loop_box = this.addHBox("loop");

            {
                var stop_btn;
                var rec_btn;
                var box;

                box = NodeJS_VBox.new.layout_(loop_box);
                this.addWidget(\loopBox1, box);

                rec_btn = NodeJS_Button.new.label_("rec").layout_(box);
                rec_btn.onPress = { this.patch(\loop).play };
                this.addWidget(\loopRec, rec_btn);

                stop_btn = NodeJS_Button.new.label_("stop").layout_(box);
                stop_btn.onPress = { this.patch(\loop).stop };
                this.addWidget(\loopStop, stop_btn);
            }.value;


            {
                var box, amp, autopan;

                box = NodeJS_VBox.new.layout_(loop_box).align_(\center);
                this.addWidget(\loopBox3, box);

                autopan = NodeJS_Knob.new(2, 0.1, 20).label_("pan freq(Hz)").labelSize_(20).size_(60).layout_(box);
                this.addWidget(\loopAutopan, autopan);
                this.bindW2P(\loopAutopan, \loop, \autopan2_period);

                amp = NodeJS_Slider.new(1, 0, 16).label_("gain").layout_(box);
                this.addWidget(\loopAmp, amp);
                this.bindW2P(\loopAmp, \loop, \gain);
            }.value;


        }.value;


        {
            var box, main_box, amp;
            var final;

            box = this.addVBox("final");
            final = NodeJS_Toggle.new(0).size_(60).label_("FINAL").layout_(box);
            final.onValue = { |v|
                if(v == 1) { this.patch(\final).play } {  this.patch(\final).release(2) };
            };
            this.addWidget(\final, final);

            main_box = NodeJS_HBox.new.layout_(box);
            this.addWidget(\finalMainBox, main_box);

            amp = NodeJS_Slider.new(0.1, 0, 4).layout_(main_box).label_("amp");
            this.addWidget(\finalAmp, amp);
            this.bindW2P(\finalAmp, \final, \gain);
        }.value;

        {
            var box, main_box, amp;
            var noise1;

            box = this.addVBox("noise1");
            noise1 = NodeJS_Toggle.new(0).size_(60).label_("NOISE1").layout_(box);
            noise1.onValue = { |v|
                if(v == 1) { this.patch(\noise1).play } {  this.patch(\noise1).release(2) };
            };
            this.addWidget(\noise1, noise1);

            main_box = NodeJS_HBox.new.layout_(box);
            this.addWidget(\noise1MainBox, main_box);

            amp = NodeJS_Slider.new(0.1, 0, 1).layout_(main_box).label_("amp");
            this.addWidget(\noise1Amp, amp);
            this.bindW2P(\noise1Amp, \noise1, \amp);
        }.value;

         {
            var box, main_box, amp;
            var noise2;

            box = this.addVBox("noise2");
            noise2 = NodeJS_Toggle.new(0).size_(60).label_("NOISE2").layout_(box);
            noise2.onValue = { |v|
                if(v == 1) { this.patch(\noise2).play } {  this.patch(\noise2).release(2) };
            };
            this.addWidget(\noise2, noise2);

            main_box = NodeJS_HBox.new.layout_(box);
            this.addWidget(\noise2MainBox, main_box);

            amp = NodeJS_Slider.new(0.1, 0, 1).layout_(main_box).label_("amp");
            this.addWidget(\noise2Amp, amp);
            this.bindW2P(\noise2Amp, \noise2, \amp);
        }.value;


        this.addParams;
    }
}

