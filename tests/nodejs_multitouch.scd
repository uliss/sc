(
NodeJS.restart;
Ndef(\t0, {SinOsc.ar(\freq.kr(440, 0.5), 0, \amp.kr(0.1, 0.1))}).play;
Ndef(\t1, {SinOsc.ar(\freq.kr(440, 0.5), 0, \amp.kr(0.1, 0.1))}).play;
Ndef(\t2, {SinOsc.ar(\freq.kr(440, 0.5), 0, \amp.kr(0.1, 0.1))}).play;
Ndef(\t3, {SinOsc.ar(\freq.kr(440, 0.5), 0, \amp.kr(0.1, 0.1))}).play;
Ndef(\t4, {SinOsc.ar(\freq.kr(440, 0.5), 0, \amp.kr(0.1, 0.1))}).play;
)

(
m = NodeJS_Multitouch.new(600);
m.onTouch0 = { |x,y|
    Ndef(\t0).set(\freq, x.asFloat * 3000 + 300, \amp, y.asFloat);
};
m.onTouch1 = { |x,y|
    Ndef(\t1).set(\freq, x.asFloat * 3000 + 300, \amp, y.asFloat);
};
m.onTouch2 = { |x,y|
    Ndef(\t2).set(\freq, x.asFloat * 3000 + 300, \amp, y.asFloat);
};
m.onTouch3 = { |x,y|
    Ndef(\t3).set(\freq, x.asFloat * 3000 + 300, \amp, y.asFloat);
};
m.onTouch4 = { |x,y|
    Ndef(\t4).set(\freq, x.asFloat * 3000 + 300, \amp, y.asFloat);
};

)

NodeJS.verbose(false)

m.add

g = NodeJS_MultitouchGrid.new;
g.add