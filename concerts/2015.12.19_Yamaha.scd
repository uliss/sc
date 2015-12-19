BuiltIn.gui

(

SynthDef(\roai, {
    arg bufT, bufM, ampT = 1, ampM = 2;
    var sigT = PlayBuf.ar(2, bufT) * ampT * EnvGate.new;
    var sigM = PlayBuf.ar(1, bufM) * ampM * EnvGate.new;
    Out.ar(0, sigT);
    Out.ar(4, sigM);
}).add;

SynthDef(\broadway, {
    arg buf, amp = 1, rate = 1;
    var sig = PlayBuf.ar(2, buf, BufRateScale.kr(buf) * rate) * EnvGate.new * amp;
    // to phones
    Out.ar(0, sig);
    // Out.ar(5, sig[1]);
}).add;

SynthDef(\daoust, {
    arg buf, amp = 1;
    var sig = PlayBuf.ar(2, buf) * EnvGate.new * amp;
    Out.ar(0, sig);
}).add;

)

(
var broadway = EastBroadway.new;
var roai = ROAI_III.new;
var daoust = DaoustPetite.new;

c = ConcertControl.new("Yamaha", Date.new(2015, 12, 19, 0, 0, 0, 0));

c.add("Minas Borbudakis", "ROAI III", \roai, Synth.newPaused(\roai,
    [\bufT, roai.bufferT, \bufM, roai.bufferM]));


c.add("Julia Wulfe", "East Broadway", \broadway, Synth.newPaused(\broadway,
    [\buf, broadway.buffer, \rate, 0.96]));


c.add("Yves Daoust", "Petite Musicque Sentimentale", \daoust, Synth.newPaused(\daoust,
    [\buf, daoust.buffer]));

c.list;
c.sendInfo;
)

c.play(\broadway);
c.release(\broadway, 4);
c.pause(\broadway);
