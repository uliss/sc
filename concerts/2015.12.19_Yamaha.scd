(
// START
Saffire.gui;
s = Server.default;
s.waitForBoot({
    SynthDef(\roai, {
        arg bufT, bufM, ampT = 1, ampM = 2, pos;
        var sample_pos = pos * 44100;
        var sigT = PlayBuf.ar(2, bufT, startPos: sample_pos) * ampT * EnvGate.new;
        var sigM = PlayBuf.ar(1, bufM, startPos: sample_pos) * ampM * EnvGate.new;
        Out.ar(0, sigT);
        Out.ar(4, sigM);
    }).add;

    SynthDef(\broadway, {
        arg buf, amp = 1, rate = 1;
        var sig = PlayBuf.ar(2, buf, BufRateScale.kr(buf) * rate) * EnvGate.new * amp;
        // to phones
        // Out.ar(0, sig);
        Out.ar(2, sig[1]);
        Out.ar(3, sig[0]);
    }).add;

    SynthDef(\daoust, {
        arg buf, amp = 1;
        var sig = PlayBuf.ar(2, buf) * EnvGate.new * amp;
        Out.ar(0, sig);
    }).add;
});

)

// START
(
var broadway = EastBroadway.new;
var roai = ROAI_III.new;
var daoust = DaoustPetite.new;

c = ConcertControl.new("Yamaha", Date.new(2015, 12, 19, 0, 0, 0, 0));

// pos 2 - 145
c.add("Minas Borbudakis", "ROAI III", \roai,
    {Synth.new(\roai, [\bufT, roai.bufferT, \bufM, roai.bufferM, \ampT, 1, \ampM, 3, \pos, 0])});


c.add("Julia Wulfe", "East Broadway", \broadway,
    {Synth.new(\broadway, [\buf, broadway.buffer, \rate, 0.96])});


c.add("Yves Daoust", "Petite Musicque Sentimentale", \daoust,
    {Synth.new(\daoust, [\buf, daoust.buffer])});

c.list;
c.sendInfo;
)

// manual

c.play(\roai);
c.pause(\roai);
c.stop(\roai);

c.play(\broadway);
c.pause(\broadway);
c.stop(\broadway);

c.play(\daoust);
c.pause(\daoust);
c.stop(\daoust);
