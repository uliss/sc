Instr("gen.seq1", {
    arg tempo = 12;
    var freq, trig, reset, seq1, seq2;
    trig = Impulse.kr(tempo);
    seq1 = Drand([72, 75, 79, 82] - 12, inf).midicps;
    seq2 = Dseq([72, 75, 79, Drand([82,84,86])], inf).midicps;
    freq = Demand.kr(trig, 0, [seq1, seq2]);
    SinOsc.ar(freq + [0,0.7]).cubed.cubed * 0.1;
}, [
    [2, 20, \lin, 1, 12]
],
outSpec: AudioSpec(2)
);