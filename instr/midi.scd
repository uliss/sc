Instr("midi.piano1", {
    arg note = 60, amp = 1, attackTime = 0.01, fadeTime = 0.1;
    Pulse.ar(note.midicps, 0.5, amp) * EnvGen.kr(Env.adsr(), \gate.kr(1), doneAction: 2)
}, [
    [21, 108, \lin, 0, 60]
],
outSpec: AudioSpec(1)
);
