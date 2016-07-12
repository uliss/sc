/*
1. boot server
2. connect MIDI
3. load SYNTHS
4. run MIDI
5. [S]1 - viola on
6. [PLAY] - start
7. [STOP] - stop
*/

SoundCard.saffire;
MIDIIn.connectAll;
MKtl.find;


{SinOsc.ar * 0.01}.play

( // START HERE
var snd = "/Users/serj/concerts/2016.05.21 - ZIL Ночь Музыки/missy_mazzoli.wav";
~buffer = Buffer.read(s, snd, action: { "buffer loaded".postln; });
~midik = MKtl(\nano, 'korg-nanokontrol2');
~n = nil;

// LED blink
Routine{
    7.do { |i|
        {~midik.elAt.value = i % 2}.defer;
        0.2.wait;
    };
}.play;

Spec.add(\time, [0.1,3]);
Spec.add(\clickAmp, [0.5, 5]);

Ndef(\player).clear;
Ndef(\player, {
  |out = 0, click_out = 2, bufnum = 0, gate = 1, speed = 1, pos = 0, pan = 0, amp = 1, loop = 0|
    var audio, rate, env, click, aout;
    env = EnvGen.ar(Env.asr(0.1,1,0.1), gate, doneAction:2);
    rate = BufRateScale.kr(bufnum)*speed*(PulseCount.kr(gate) >= 1);
    audio = PlayBuf.ar(numChannels: 2, bufnum: bufnum, rate: rate, trigger: gate, startPos: pos  + 4000, loop: loop, doneAction:2);
    aout = Pan2.ar(audio[0]* env) * amp.lag(0.2);
    click = audio[1] * env * \clickAmp.kr(2);
    Out.ar(out, aout);
    Out.ar(click_out, [click, aout[0] * 0.5 + Ndef(\viola).ar]);
});
Ndef(\player).free;

Ndef(\viola).clear;
Ndef(\viola, {
    var in, sig;
    // sig = SoundIn.ar(1);
    sig = SPU_ViolaIn.ar(0);
    // sig = JPverb.ar(in, \time.ar(0.8), 0.4);
    NumChannels.ar(sig, 2) * \amp.kr(0.2, 0.3);
});

~midik.gui

~midik.elAt(\bt, \S, 0).updateDescription((trigger: true));

~midik.elAt(\bt, \S, 0).action = { |el|
    el.desc.postln;
/*    if(el.value == 1) {
        if(el.elemDesc.at(\trigger).isNil) {
            el.elemDesc[\trigger] = false;
        };

        if(el.elemDesc[\trigger]) {
            el.value = 1;
            "Viola ON".postln;
            Ndef(\viola).play;
        }
        {
            el.value = 0;
            "Viola OFF".postln;
            Ndef(\viola).fadeTime = 2;
            Ndef(\viola).stop;
        };

        el.elemDesc[\trigger] = el.elemDesc[\trigger].not;
    };*/
};

)




// MIDI
(

Ndef(\player).set(\bufnum, ~buffer);
Ndef(\player).set(\speed,1);

MIDIFunc.cc({|m| // [S] 1
    if(m == 127) {
        "Viola ON".postln;
        Ndef(\viola).play;
    }
    {
        "Viola OFF".postln;
        Ndef(\viola).fadeTime = 2;
        Ndef(\viola).stop;
    };
}, 33);

MIDIFunc.cc({|m|
    var amp = m.linlin(0, 127, 0, 14);
    "Click AMP = %".format(amp).postln;
    Ndef(\player).set(\clickAmp, amp);
}, 2);

MIDIFunc.cc({|m|
    var amp = m.linlin(0, 127, 0, 0.4).scurve;
    "Viola AMP = %".format(amp).postln;
    Ndef(\viola).set(\amp, amp);
}, 1);

MIDIFunc.cc({|m|
    var tm = m.linlin(0, 127, 0, 10);
    "Viola reverb time = %".format(tm).postln;
    Ndef(\viola).set(\time, tm);
}, 17);

MIDIFunc.cc({|m|
    if(m == 127) {
        "PLAY".postln;
        Ndef(\player).play;
    };
}, 41);

MIDIFunc.cc({|m|
    if(m == 127) {
        "STOP".postln;
        Ndef(\player).free;
    };
}, 42);

MIDIFunc.cc({|m|
    var amp = m.linlin(0, 127, 0, 1).scurve;
    "AMP = %".format(amp).postln;
    Ndef(\player).set(\amp, amp);
}, 0);

MIDIFunc.cc({|m|
    if(m == 127) {{
            ~n = NdefMixer.new(s);

        // ~n.parent().front;
    }.defer(0)}
}, 60);

)
