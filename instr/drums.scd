Instr("drums.gen_snare1", {
    arg tempo = 12;
    var trig, seq;
    trig = Impulse.kr(tempo);
    seq = Drand([
        Dseq([4,0,0,1,2,1,0,1]),
        Dseq([4,0,2,0,1,0,1,1]),
        Dseq([4,0,0,2,0,0,1,1]),
        Dseq([4,0,1,2,0,1,2,0]),
        Dseq([4,1,1,1,2,2,3,3]),
        Dseq([4,1,0,1,0,1,0,1])
    ], inf);
    trig = Demand.kr(trig, 0, seq * 0.4) * trig;
    {LPF.ar(PinkNoise.ar, 5000)}.dup * Decay.kr(trig, 0.5);
}, [ [2, 20, \lin, 1, 12] ],
outSpec: AudioSpec(1)
);