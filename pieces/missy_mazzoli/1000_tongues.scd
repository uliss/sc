SoundCard.builtin

{SinOsc.ar}.play

MIDIIn.connectAll;
MKtl.find;

(

~track_path_click = "/Users/serj/work/music/sc/pieces/missy_mazzoli/A Thousand Tongues - tape part with click.aiff";
~track_path = "/Users/serj/work/music/sc/pieces/missy_mazzoli/AThousandTongues-Tape Part.aiff";

Spec.add(\gain, [0.5, 4, \lin]);

Ndef(\tape).clear;
Ndef(\tape, {
    arg buf, rate = 1;
    VDiskIn.ar(2, buf, rate) * \gain.kr(1, 0.5) * \amp.kr(1, 0.5) * 20;
});

Ndef(\viola).clear;
Ndef(\viola, {
    var in = SPU_ViolaInTest.ar(0);
    in = SPU_ViolaReverb.ar(SPU_ViolaCompress.ar(SPU_ViolaFilter.ar(in)), \mix.kr(0.5, 0.5), \room.kr(0.65));
    Pan2.ar(in, \pan.kr(0, 0.5)) * \gain.kr(1, 0.5) * \amp.kr(1, 0.5);
});

~midik = MKtl(\nano, 'midi_1_nanokontrol2');

// LED blink
Routine{
    11.do { |i|
        {[\play, \stop, \mset].do{arg e; ~midik.elAt(\tr, e).value = i % 2}}.defer;
        {[\R, \S, \M].do{arg e; ~midik.elAt(\bt, e, 0).value = i % 2}}.defer;
        0.2.wait;
    };
}.play;

~midik.elAt(\tr, \mset).action = {|el| if(el.value == 1) {{~n = NdefMixer(s)}.defer(0)}};

~midik.elAt(\tr, \play).action = {|el| if(el.value == 1) {
    "PLAY".postln;
    ~midik.elAt(\tr, \stop).value = 0;
    ~cue_buf = Buffer.cueSoundFile(s, ~track_path_click, 0);
    Ndef(\tape).set(\buf, ~cue_buf.bufnum);
    Ndef(\tape).play();
} {
    el.value = 1;
}};

~midik.elAt(\tr, \stop).action = {|el| if(el.value == 1) {
    "STOP".postln;
    ~midik.elAt(\tr, \play).value = 0;
    Ndef(\tape).stop();
} {
    el.value = 1;
}};



// LED init state
{~midik.elAt(\tr, \stop).valueAction = 1}.defer(3);
Ndef(\tape).set(\rate, 1);
Ndef(\viola).play();

// Tape amp at slider 0
~midik.elAt(\sl, 0).action = {|el| Ndef(\tape).set(\amp, el.value)};
// Viola amp at slider 1
~midik.elAt(\sl, 1).action = {|el| Ndef(\viola).set(\amp, el.value)};
// Viola pan at knob 1
~midik.elAt(\kn, 1).action = {|el| Ndef(\viola).set(\pan, el.value.range(-1, 1))};

// setting time jumps
[83, 124, 171, 198, 251, 278, 304, 351].do { |i, v|
    Ndef(\tape).set(\buf, ~cue_buf.bufnum);
    ~midik.elAt(\bt, \R, v).action = { |el|
        if(el.value == 1) {
            el.value = 1;
            "jump to %".format(i.asTimeString.drop(-4).drop(4)).postln;
            ~cue_buf = Buffer.cueSoundFile(s, ~track_path_click, 44100 * i);
            Ndef(\tape).set(\buf, ~cue_buf.bufnum);
        }
        {
            el.value = 0;
        };
    };
};

)


// clean up
~midik.elAt.value = 1;















