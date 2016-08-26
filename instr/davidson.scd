Instr("davidson.msg_ground_viola", {
    arg in, viola1_pan = 0.3, viola2_pan = -0.3, viola2_amp = 1.0, delay = 3.4;
    Pan2.ar(in, viola1_pan) + Pan2.ar(DelayL.ar(in, 4, delay, viola2_amp), viola2_pan);
}, [
    AudioSpec(1),
    \pan,
    \pan,
    [0, 1, \amp, 0, 1],
    [0.1, 4, \lin, 0, 3, "sec"]
],
outSpec: AudioSpec(2)
);

