Piece_Filonenko_JacksonG : GuidoPieceApp {
    var bproxy;

    *new {
        arg time = 2;
        ^super.new("Jackson(G)", "Alexandra Filonenko", "/jacksong", params: (fadeTime: time)).loadParams;
    }

    clearLoop {
        bproxy.buffer.zero;
    }

    initPatches {
        arg params;

        bproxy = BufferProxy.new(44100 * 10);

        this.addPatch(\voice, ["common.in", "common.env", "common.pan2", "common.freeverb2"], (in: 0, env: Env.asr(0.5, 1, params[\fadeTime])));
        this.addPatch(\bass,  ["common.in", "common.pan2", "common.freeverb2"], (in: 1));
        this.addPatch(\loop, [ "filonenko.note_repeat", "common.freeverb", "common.autopan2"]);

        onPlay = {
            // this.playPatches;
            this.patch(\voice).play;
            this.patch(\bass).play;
        };

        onPause = {
            this.stopPatches;
        };

        onStop = {
            this.releasePatches(params[\fadeTime]);
        };
    }

    initUI {
        {
            var start = NodeJS_Toggle.new(0).size_(150).label_("START");
            start.onValue = { |v|
                if(v == 1) { this.play } { this.stop };
            };
            this.addWidget(\start, start);
        }.value;


        {
            var voice_box;

            voice_box = this.addHBox("voice");

            {
                var voice_amp;
                var voice_pan;
                var box = NodeJS_VBox.new.layout_(voice_box).align_(\center);
                this.addWidget(\box1, box);

                voice_pan = NodeJS_Pan.new.label_("").layout_(box);
                this.addWidget(\voicePan, voice_pan);
                this.bindW2P(\voicePan, \voice, \pan);

                voice_amp = NodeJS_Slider.new(1).label_("amp").layout_(box);
                this.addWidget(\voiceAmp, voice_amp);
                this.bindW2P(\voiceAmp, \voice, \in_amp);
            }.value;

            {
                var box2;
                var voice_room;
                var voice_mix;

                box2 = NodeJS_VBox.new.layout_(voice_box).align_(\center);
                this.addWidget(\box2, box2);

                voice_room = NodeJS_Knob.new(0.5, 0, 0.9).size_(100).label_("room").layout_(box2);
                this.addWidget(\voiceRoom, voice_room);
                this.bindW2P(\voiceRoom, \voice, \freeverb2_room);

                voice_mix = NodeJS_Knob.new(0.5, 0, 0.9).size_(100).label_("mix").layout_(box2);
                this.addWidget(\voiceMix, voice_mix);
                this.bindW2P(\voiceMix, \voice, \freeverb2_mix);
            }.value;


        }.value;

        /*  {
        var rec_box;
        var rec_toggle;
        var loop_clear;

        rec_box = this.addVBox("record");

        rec_toggle = NodeJS_Toggle.new.label_("start").size_(100).layout_(rec_box);
        this.addWidget(\recToggle, rec_toggle);
        rec_toggle.onValue = { |v|
        // v.postln;
        if(v == 1) {
        this.patch(\loopRec).play;
        this.patch(\loopRec).set(\recbuf_on, 1);
        } {
        this.patch(\loopRec).stop;
        };
        };

        loop_clear = NodeJS_Toggle.new.label_("clear loop").layout_(rec_box);
        this.addWidget(\loopClear, loop_clear);
        loop_clear.onValue = { |v|
        if(v == 1) {
        "LOOP CLEAR!".postln;
        this.clearLoop;
        }
        };

        }.value;

        {
        var play_box;
        var play_toggle;
        var play_loop;
        var play_amp;

        play_box = this.addHBox("play");
        this.addWidget(\playBox, play_box);

        play_toggle = NodeJS_Toggle.new.label_("play").size_(100).layout_(play_box);
        this.addWidget(\playToggle, play_toggle);

        play_toggle.onValue = { |v|
        // v.postln;
        if(v == 1) {
        this.patch(\loopPlay).play;
        this.patch(\loopPlay).set(\playbuf_trig, 1);
        } {
        this.patch(\loopPlay).release(2);
        }
        };

        play_loop = NodeJS_Toggle.new.label_("loop").size_(50).layout_(play_box);
        this.addWidget(\playLoop, play_loop);
        this.bindW2P(\playLoop, \loopPlay, \playbuf_loop);

        play_amp = NodeJS_Slider.new(1, 0, 2).label_("amp").layout_(play_box);
        this.addWidget(\playAmp, play_amp);
        this.bindW2P(\playAmp, \loopPlay, \gain);
        }.value;*/



        // this.addWidget(\celloReverbBox, cello_reverb_box);
        //
        // cello_reverb_mix = NodeJS_Knob.new(0.5).label_("reverb mix").size_(85).labelSize_(20).layout_(cello_reverb_box);
        // this.addWidget(\celloReverbMix, cello_reverb_mix);
        // this.bindW2P(\celloReverbMix, \cello, \freeverb2_mix);
        // cello_reverb_room = NodeJS_Knob.new(0.7).label_("reverb room").size_(85).labelSize_(20).layout_(cello_reverb_box);
        // this.addWidget(\celloReverbRoom, cello_reverb_room);
        // this.bindW2P(\celloReverbRoom, \cello, \freeverb2_room);


        this.addParams;
    }
}
