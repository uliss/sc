(
// START
SoundCard.mbox;
MIDIIn.connectAll;

{s.boot}.defer(2);
)


(
var cc;
var ground;
var isPlaying = false;

cc = MIDIFunc.cc({
    arg msg;


    if(msg == 127) {
        isPlaying = isPlaying.not;
        if(isPlaying) {
            "START".postln;
            ground = Sp_SynthMessageGround.new(violaIn: 1, gameboyIn: 2, fx_mix: 0.2, tempo: 140);
            ground.gameboy.run(true);
            ground.out_mix.set(\gameboyAmp, 6);
            ground.out_mix.set(\violaAmp, 0.8);
            ground.viola_delay.set(\v1_amp, 2);
            ground.viola_delay.set(\v2_amp, 0.8);
            // ground.viola_in.compress(true);
            // ground.viola_in.synth_play.run(false);

        } {
            "STOP".postln;
            ground.gameboy.run(false);
            ground.run(false, off_amp: 1.2, off_room: 0.85);
            {ground.free; ground = nil}.defer(5);
        };
    };

}, 102, 0);

)

{Pulse.ar(220, mul: 0.01)}.play

(
a = Sp_SynthViolaIn.new(in: 1);
a.run;
// a.compress(false);
// a.filter(false);
a.monitor;
)

{SoundIn.ar(3)}.scope


