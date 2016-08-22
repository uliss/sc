Instr("8bit.1", {
    arg amp = 0.2;
    var t = PulseCount.ar(Impulse.ar(8e3));
    HPF.ar(
        (
            ((t * 15) & (t >> 5)) |
            ((t * 5)  & (t >> [3, 4])) |
            ((t * 2)  & (t >> 9)) |
            ((t * 8)  & (t >> 11))
            - 3 % 256
        ) / 127-1 * 3
        , 20
    ).tanh * amp * EnvGate.new;
}, [
    \amp,
    [3000, 10000, \lin, 1, 8000],
    [0, 63, \lin, 1, 15]
]);

Instr("8bit.2", {
    arg amp = 0.2, tempo = 8000,
    p1a = 15, p1b = 5, p2a = 5, p2b = 3, p2c = 4, p3a = 2, p3b = 9,
    p4a = 8, p4b = 11, p5 = 256;
    var t = PulseCount.ar(Impulse.ar(tempo));
    HPF.ar(
        (
            ((t * p1a) & (t >> p1b)) |
            ((t * p2a)  & (t >> [p2b, p2c])) |
            ((t * p3a)  & (t >> p3b)) |
            ((t * p4a)  & (t >> p4b))
            - 3 % p5
        ) / 127-1 * 3
        , 20
    ).tanh * amp;
}, [
    \amp,
    [3000, 10000, \lin, 1, 8000],
    [0, 63, \lin, 1, 15],
    [0, 16, \lin, 1, 5],
    [0, 16, \lin, 1, 5],
    [0, 16, \lin, 1, 3],
    [0, 16, \lin, 1, 4],
    [0, 16, \lin, 1, 2],
    [0, 16, \lin, 1, 9],
    [0, 16, \lin, 1, 8],
    [0, 16, \lin, 1, 11],
    [128, 512, \lin, 1, 256]
]);

Instr("8bit.ay1", {
    arg freqa=440, freqb=550, freqc=660, vola=15, volb=0, volc=0, chink=1, wobbly=1, amp=0.1;
    var chinkenv, wobblyenv;

    chinkenv = if(chink>0, EnvGen.kr(Env.new([2, 2, 1, 1], [0.05, 0, 0.1], 0, 4, 4)), 1);
    wobblyenv = LFPulse.kr(10, 0.5, mul:wobbly).range(0.5, 1);

    # freqa, freqb, freqc = [freqa, freqb, freqc] * [1, wobblyenv, wobblyenv] * chinkenv;
    AY.ar(AY.freqtotone(freqa), AY.freqtotone(freqb), AY.freqtotone(freqc),
                0, 3, vola, volb, volc, mul: amp) * EnvGen.kr(Env.asr(0.01, 1, 0.05), doneAction:2);
}, [
    \freq, \freq, \freq,
    [0, 31, \lin, 1, 15],
    [0, 31, \lin, 1, 0],
    [0, 31, \lin, 1, 0]
]
)
