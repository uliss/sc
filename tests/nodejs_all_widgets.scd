(
s.waitForBoot({
    NodeJS.restart;
    {NodeJS.reload}.defer(3);
})
)



(
NodeJS.reload;
Ndef(\sin, {SinOsc.ar(\freq.kr(0), 0, \amp.kr(0))}).play;
{
    var number, nfreq, sc, nmidi, knob, pan, hslider, vslider, tgl, btn;
    number = NodeJS_Number.new(100, 90, 110);
    number.add;
    number.onValue = {|v| v.postln};

    nfreq = NodeJS_NumberFreq.new(440);
    nfreq.add;
    nfreq.onValue = {|v| Ndef(\sin).set(\freq, v)};

    sc = NodeJS_NumberAmp.new(0.1);
    sc.add;
    sc.onValue = {|v| Ndef(\sin).set(\amp, v)};

    nmidi = NodeJS_NumberMidi.new(63);
    nmidi.add;
    nmidi.onValue = {|v| Ndef(\sin).set(\freq, v.midicps)};

    knob = NodeJS_Knob.new(0.5);
    knob.add;
    knob.onValue = {|v| v.postln};

    pan = NodeJS_Pan.new(-0.3);
    pan.add;
    pan.onValue = {|v| v.postln};

    hslider = NodeJS_Slider.new(0.1, horizontal: 1);
    hslider.add;
    hslider.onValue = {|v| v.postln };

    vslider = NodeJS_Slider.new(0.2);
    vslider.add;
    vslider.onValue = {|v| v.postln };

    tgl = NodeJS_Toggle.new(1);
    tgl.add;
    tgl.onValue = {|v| Ndef(\sin).set(\amp, v) };

    btn = NodeJS_Button.new(50, "btn");
    btn.add;
    btn.onClick = { Ndef(\sin).set(\freq, 880); {Ndef(\sin).set(\freq, 440);}.defer(0.2); }
}.defer(2);
)