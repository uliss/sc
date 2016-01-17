(
// INIT
SoundCard.defaultCard = \builtin;
k = SoundCard.default;

f = {
    Sp_SynthViolaIn.loadSynths;
};

ServerBoot.add(f, \default);
k.server.boot;
)

(
// RUN
var osc_remote_app, osc_control, viola_synth;

osc_remote_app = Sp_AppTouchOSC.ipad;
osc_remote_app.appendFloatIndicator("/viola/level");

osc_control = Sp_OscViolaIn.new.bindOscApp(osc_remote_app);
osc_control.connected;
osc_control.debug(true);

~viola = Sp_SynthViolaIn.new;
~viola.run(true);

osc_control.mapSynthControls(~viola);

~viola.monitor;
~viola.vu(true);
)

~viola.play(true)
~viola.play(true, 60);
~viola.play(false)

~viola.reverb(true)
~viola.reverb(false)

~viola.filter(true)
~viola.filter(false)

~viola.compress(true)
~viola.compress(false)

~viola.mute(true)
~viola.mute(false)