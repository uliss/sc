{SPU_ViolaVU.ar(SPU_ViolaIn.ar)}.scope

(o = OSCFunc({|m| m.postln;}, '/viola/pitch'))

(
{SPU_ViolaFilter(SPU_ViolaInTest.ar(20))}.play;
)


{Pan2.ar(SPU_ViolaInCommonTest.ar(170))}.play

{Pan2.ar(SPU_ViolaReverb.ar(SPU_ViolaFilter.ar(SPU_ViolaInTest.ar(170))))}.play


{SPU_ViolaReverb.ar(SPU_ViolaDelay2.ar(SPU_ViolaInTest.ar(10), MouseX.kr(0.1, 1)))}.play


{Saw.ar(Tartini.kr(SPU_ViolaInTest.ar)[0],0.2)}.play

{SPU_ViolaPitch.ar(SPU_ViolaInTest.ar(4))}.play


(o = OSCFunc({
    |m|
    var mf = m[4].round;
    var hasFreq = m[5];

    if(hasFreq > 0) {
        if(mf > 47) {
            // mf.postln;

            if(mf == 55) {
                hasFreq.postln;
                 (instrument: \default, midinote: mf - 24, dur: 10).play;
            };
        }
        // Pbind(\dur, 1, \midinote, mf).play;
        // Synth.new(\default, [\freq, mf.midicps, ]);
        // if(m)
    };
}, '/viola/pitch'))