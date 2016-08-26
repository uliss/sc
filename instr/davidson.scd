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

Instr("davidson.spiral_viola_canon", {
    arg in,
        tempo = 140, echo_times = 5,
        viola1_amp = 1,     viola1_pan = 0,
        viola2_amp = 0.9,   viola2_pan = -0.4,
        viola3_amp = 0.7,   viola3_pan = 0.4,
        freeze = 0;

    var delay_time_short = (60.0/tempo) * 16;
    var delay_time_long = delay_time_short * 2;

    Pan2.ar((in * (1 - freeze)), viola1_pan, viola1_amp)
    + Pan2.ar(
        DelayL.ar(
            AllpassL.ar((in * (1 - freeze)), 16, delay_time_long, delay_time_long * echo_times),
            8, delay_time_short
        ),
        viola2_pan, viola2_amp
    )
    + Pan2.ar(
        AllpassL.ar((in * (1 - freeze)), 16, delay_time_long, delay_time_long * (echo_times + 1)),
        viola3_pan, viola3_amp
    );
}, [
    AudioSpec(1),
    BpmTempoSpec(140),
    [1, 50, \lin, 0, 5],
    AmpSpec(1),    // viola1_amp
    PanSpec(0),    // viola1_pan
    AmpSpec(0.9),  // viola2_amp
    PanSpec(-0.4), // viola2_pan
    AmpSpec(0.7),  // viola3_amp
    PanSpec(0.4),  // viola3_pan
    [0, 1, \lin, 1, 0] // freeze
],
outSpec: AudioSpec(2));