Server.default = s = Server.new("MBOX Output", NetAddr("127.0.0.1", 58009), ServerOptions.new.device_("Mbox Pro")).makeWindow.alwaysOnTop_(true);

s.boot;
(
var w, composer, g_title, g_levels, w_width, w_height, play_btns, timer;
var snd_player, snd_piano;
var gui_osc_port = 10001, gui_levels_osc, gui_piano_osc;
var stop_trigger = 1;
var g_timer;
var snd_bus_player = Bus.control(s, 1);
var snd_buffer = Buffer.read(s, "/Users/serj/work/music/sounds/Daoust_Petite_musique_sentimentale.aiff");

w_width = 450;
w_height = 350;


w = Window.new("Yves Daoust", Rect(200, 200, w_width, w_height)).alwaysOnTop_(true).background_(Color.gray).front;

// SOUND
// AUDIO TRACK PLAYER
SynthDef("playbuf", {
    arg out = 0, buf, pos = 0, trigger = 1, mul = 1;
    var asrc, t = 1, snd, imp, delimp;
    var r_level = 0, l_level = 0, aR, aL;

    t = Changed.kr(trigger);
    snd = PlayBuf.ar(2, buf, trigger: t, startPos: pos);
    snd = mul * snd;

    OscVU.kr(snd, '/playbuf_vu');
    Out.ar(out, snd);
}).add;

snd_player = Synth.newPaused(\playbuf, [\buf, snd_buffer]);
snd_player.map(\mul, snd_bus_player);

// TIMER
timer = OscTimerClock.new;

// TITLE
g_title = GTitle(w, composer: "Yves Daoust", piece: "Petite Musique Sentimentale");

// GUI TIMER
g_timer = GOscTimer(w, oscPort: gui_osc_port);

// LEVELS
g_levels = GMixN(w, number: 2, init: 0.5);
g_levels.title_(0, 'L');
g_levels.title_(1, 'R');

// get levels via OSC
o = OSCFunc({ |msg|
    if(msg[1] == 0) {
        {g_levels.at(1).valueAction_(msg[2])}.defer;
        snd_bus_player.set(msg[2]);
    };
    }, '/GMixN', nil, 10001);

// PLAYER BUTTONS
play_btns = GPlayerButtons(w);
play_btns.playButton({
    "play".postln;
    snd_player.run(true);
    timer.start;
});

play_btns.stopButton({
    stop_trigger = stop_trigger * (-1);
    "stop".postln;
    snd_player.set(\pos, 0, \trigger, stop_trigger);
    snd_player.run(false);
    timer.stop;
});

play_btns.pauseButton({|value|
    postln("pause" + value);
    snd_player.run(value.not);
    if(value, {timer.pause}, {timer.resume});
});

play_btns.setInitState;

gui_levels_osc = OSCFunc({ |msg|
    {
        g_levels.vu_(0, msg[3], msg[5]);
        g_levels.vu_(1, msg[4], msg[6]);
    }.defer;
}, '/playbuf_vu');


// LAYOUT
g_title.moveTo(20, 20);
g_levels.moveTo(20, 120);
g_timer.moveTo(140, 120);
play_btns.moveTo(20, 300);

w.onClose = {
    snd_player.free;
    if(snd_piano.notNil) {
        snd_piano.free;
    };
    timer.stop;
    gui_levels_osc.free;
    gui_piano_osc.free;
    snd_buffer.free;
    s.freeAll;
};
)