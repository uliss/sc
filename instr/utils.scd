Instr("utils.tone", {
    arg freq, amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    SinOsc.ar(freq, 0, amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
}, [
    [380, 460, \lin, 0, 440]
],
outSpec: AudioSpec(1)
);

Instr("utils.white-noise", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    WhiteNoise.ar(amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
},
outSpec: AudioSpec(1)
);

Instr("utils.pink-noise", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    PinkNoise.ar(amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
},
outSpec: AudioSpec(1)
);

Instr("utils.sweep-tone", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    SinOsc.ar(
        XLine.kr(20, 20000, 4, amp, doneAction: 2);
    );
},
outSpec: AudioSpec(1)
);

Instr("utils.ding", {
    arg freq = 1000, amp = 0.2;
    SinOsc.ar(freq, 0, amp) * EnvGen.kr(Env.perc, doneAction: 2);
},
outSpec: AudioSpec(1)
);

Instr("utils.click", {
    arg bpm = 60, freq = 1500, amp = 1;
    LPF.ar(Impulse.ar(bpm / 60), freq) * 3 * amp * EnvGate.new(fadeTime: 0.5);
}, [
    [40, 400, \lin, 1, 60, "bpm"],
    \freq,
    [0, 4, \lin, 0, 1]
],
outSpec: AudioSpec(1)
);

Instr("utils.vu", {
    arg in, rate = 10, rmsPath = "/sc/utils/rms";
    SendPeakRMS.kr(in, rate, cmdName: rmsPath);
    in;
}, [
    AudioSpec(1),
    [2, 20, \lin, 1, 10],
    SymbolSpec()
],
outSpec: AudioSpec(1)
);
