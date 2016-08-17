Instr("utils.tone", {
    arg freq, amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    SinOsc.ar(freq, 0, amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
}, [
    [380, 460, \lin, 0, 440]
]);

Instr("utils.white-noise", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    WhiteNoise.ar(amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
});

Instr("utils.pink-noise", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    PinkNoise.ar(amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);
});

Instr("utils.sweep-tone", {
    arg amp = 0.2, attackTime = 0.1, fadeTime = 0.1;
    /*PinkNoise.ar(amp) * Linen.kr(\gate.kr(1), attackTime, 1, fadeTime, doneAction: 2);*/
    SinOsc.ar(
        XLine.kr(20, 20000, 4, amp, doneAction: 2);
    );
});

Instr("utils.ding", {
    arg freq = 1000, amp = 0.2;
    SinOsc.ar(freq, 0, amp) * EnvGen.kr(Env.perc, doneAction: 2);
});



