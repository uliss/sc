(
// INIT
SoundCard.defaultCard = \builtin;
k = SoundCard.default;
k.server.boot;
)

// RUN
(
~vla = Local_ViolaAmp1.new();
~vla.connectIpad;
)

// MANUAL CONTROL
~vla.testPlay(0)
~vla.testPlay(106.7)
~vla.testPlay(130);
~vla.testStop

~vla.mute(true)
~vla.mute(false)

~vla.reverb(0.9, 0.9, 0.9)
~vla.reverb

~vla.pan(-1)
~vla.pan(1)
~vla.pan
