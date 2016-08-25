Instr("mix.1<2", {
    arg in;
    [in, in];
}, [ AudioSpec(1) ], outSpec: AudioSpec(2));

Instr("mix.1<3", {
    arg in;
    [in, in, in];
}, [ AudioSpec(1) ], outSpec: AudioSpec(3));

Instr("mix.1<4", {
    arg in;
    [in, in, in, in];
}, [ AudioSpec(1) ], outSpec: AudioSpec(4));

Instr("mix.N>1", {
    arg in;
    Mix.ar(in);
}, [ AudioSpec() ], outSpec: AudioSpec(1));

Instr("mix.toMono", {
    arg in;
    Mix.ar(in);
}, [ AudioSpec() ], outSpec: AudioSpec(1));
