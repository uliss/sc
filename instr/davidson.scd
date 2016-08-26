Instr("davidson.msg_ground_viola", {
    arg in, viola1_pan = 0.3, viola2_pan = -0.3, viola2_amp = 1.0, tempo = 144;
    Pan2.ar(in, viola1_pan) + Pan2.ar(DelayL.ar(in, 4, (60.0 / tempo) * 8, viola2_amp), viola2_pan);
}, [
    AudioSpec(1),
    [-1, 1, \lin, 0, 0.3],
    [-1, 1, \lin, 0, -0.3],
    [0, 1, \amp, 0, 1],
    BpmTempoSpec(144)
],
outSpec: AudioSpec(2));

Instr("davidson.spiral_viola", {
    arg in;
    Pan2.ar(in);
}, [
    AudioSpec(1)
],
outSpec: AudioSpec(2));