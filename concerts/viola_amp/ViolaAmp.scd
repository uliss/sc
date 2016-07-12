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
~vla.restore;
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

~vla.pass(true)
~vla.pass(false)

~vla.compress(true)
~vla.compress(false)

~vla.pan(-1)
~vla.pan(1)
~vla.pan

~vla.level(0.5)
~vla.level(0.1)

~vla.save;
~vla.restore
