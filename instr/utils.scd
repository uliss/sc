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


