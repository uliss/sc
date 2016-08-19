Instr("common.pan2", {
    arg in, pan = 0;
    Pan2.ar(in, pan);
}, [
    AudioSpec(1),
    [-1, 1, \lin, 0, 0]
],
outSpec: AudioSpec(2)
);
