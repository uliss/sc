SoundCard.saffire;

{SinOsc.ar}.play

MIDIIn.connectAll;


// {SoundIn.ar(0)}.play

(

var osc_f1, osc_f2, osc_f3;
~track_path_click = "/Users/serj/work/music/sc/pieces/missy_mazzoli/A Thousand Tongues - tape part with click.aiff";
~track_path = "/Users/serj/work/music/sc/pieces/missy_mazzoli/AThousandTongues-Tape Part.aiff";
m = NetAddr("127.0.0.1", NetAddr.langPort);

Spec.add(\gain, [0.5, 4, \lin]);

Ndef(\tape).clear;
Ndef(\tape, {
    arg buf, buf_click, aout, aclick;
    aclick = VDiskIn.ar(2, buf_click, BufRateScale.kr(buf)) * \gain.kr(1, 0.5) * \amp.kr(1, 0.5);
    aout = VDiskIn.ar(2, buf, BufRateScale.kr(buf_click)) * \gain.kr(1, 0.5) * \amp.kr(1, 0.5);
    Out.ar(3, aclick);
    Out.ar(0, aout * 1.5);
});

Ndef(\viola).clear;
Ndef(\viola, {
    |src = 0|
    var in;
    in = SPU_ViolaIn.ar(0);
    // in = in.fold2(0.23);
    in = SPU_ViolaReverb.ar(SPU_ViolaCompress.ar(SPU_ViolaFilter.ar(in)), \mix.kr(0.2, 0.5), \room.kr(0.8));
    Pan2.ar(in, \pan.kr(0, 0.5)) * \gain.kr(1, 0.5) * \amp.kr(1, 0.5);
});

Ndef(\flanger, {
    arg out=0, in=0, depth=0.08, rate=0.06, fdbk=0.0, decay=0;
    var input, maxdelay, maxrate, dsig, mixed, local;
    maxdelay = 0.013;
    maxrate = 10.0;
    input = Ndef(\viola).ar;
    local = LocalIn.ar(1);
    dsig = AllpassL.ar( // the delay (you could use AllpassC (put 0 in decay))
        input + (local * fdbk),
        maxdelay * 2,
        LFPar.kr( // very similar to SinOsc (try to replace it) - Even use LFTri
            rate * maxrate,
            0,
            depth * maxdelay,
            \delay.kr(0.1) * maxdelay),
        decay);
    mixed = input + dsig;
    LocalOut.ar(mixed);
    Out.ar([out, out+1], mixed * \amp.kr(1, 0.4));
});

Ndef(\distort).clear;
Ndef(\distort, {
    |amp = 0.5|
    var sig, in2, in, rfreq = 100.midicps, in_dist, distort = 1, in3, in1, outL, outR, gate = 0;
    in = Ndef(\flanger).ar[0];
    in = BRF.ar(in, \violaReject1.kr(250), 0.1);

    in_dist = (in * \dist.kr(13)).tanh.softclip * distort.lag(0.5);
    in = in_dist * 0.1 * amp.lag(2);

    in3 = MoogLadder.ar(in, LFTri.ar(\moogFreq.kr(10).reciprocal).range(250, 3000), 0.9);
    in = XFade2.ar(in, in3, \moogLevel.kr(0.6));

    in1 = PitchShift.ar(in, 0.1, pitchRatio: \detune.kr(6) / 10000.0 +  1.0);
    [in, in1] * \gain.kr(3);

});


osc_f1 = OSCFunc({
    |m|
    m.postln;
    if(m[1] == 1) {
        "PLAY".postln;
        ~cue_buf = Buffer.cueSoundFile(s, ~track_path_click, 0);
        ~cue_buf_normal = Buffer.cueSoundFile(s, ~track_path, 0);
        Ndef(\tape).set(\buf, ~cue_buf_normal.bufnum);
        Ndef(\tape).set(\buf_click, ~cue_buf.bufnum);
        Ndef(\tape).play();
    }
    {
        "STOP".postln;
        Ndef(\tape).free();
    };
}, "/button/1");

osc_f2 = OSCFunc({
    |m|
    "GO TO %".format(m[1].asTimeString.drop(-4)).postln;
    ~cue_buf = Buffer.cueSoundFile(s, ~track_path_click, 44100 * m[1].asInt);
    ~cue_buf_normal = Buffer.cueSoundFile(s, ~track_path, 44100 * m[1].asInt);
    Ndef(\tape).set(\buf, ~cue_buf_normal.bufnum);
    Ndef(\tape).set(\buf_click, ~cue_buf.bufnum);
    Ndef(\tape).play();

}, "/mazzoli/time");

osc_f3 = OSCFunc({
    |m|
    if(m[1] == 1) {
        "DIST ON".postln;
        Ndef(\viola).set(\amp, 0.7);
        Ndef(\distort).play();
        // Ndef(\flanger).play();
        Ndef(\distort).set(\gate, 1);
    }
    {
        "DIST OFF".postln;
        // Ndef(\distort).set(\gate, 0);
        Ndef(\viola).set(\amp, 1);
        // Ndef(\flanger).play();
        // Ndef(\distort).release;
        Ndef(\distort).stop;
    };
}, "/button/2");

// Ndef(\tape).set(\rate, 1);
 Ndef(\viola).play;
)


~n = NdefMixer(s);

Ndef(\viola).stop
Ndef(\viola).set(\src, 1);
Ndef(\distort).set(\dist, 2.002);
Ndef(\distort).set(\gate, 1);
Ndef(\distort).play;
Ndef(\distort).stop
Ndef(\viola).play;
Ndef(\viola).stop;


m.sendMsg("/button/1", 1);
m.sendMsg("/button/1", 0);
m.sendMsg("/button/2", 1);
m.sendMsg("/button/2", 0);
m.sendMsg("/mazzoli/time", 171);// 2:51
m.sendMsg("/mazzoli/time", 198);












