Server.default = s = Server.new("MBOX Output", NetAddr("127.0.0.1", 58009), ServerOptions.new.device_("Mbox Pro").numOutputBusChannels_(6)).makeWindow;

s.boot;

(
SynthDef("playbuf", {
    arg buf, pos = 0, trigger = 1, trackVol = 1, clickVol = 1;
    var asrc, t = 1, snd, imp, delimp, apiano, aclick;
    var r_level = 0, l_level = 0, aR, aL;

    t = Changed.kr(trigger);
    snd = PlayBuf.ar(2, buf, trigger: t, startPos: pos);

    apiano = Pan2.ar(snd[0] * trackVol);
    aclick = Pan2.ar(snd[1] * clickVol);

    // send VU via OSC
    OscVU.kr([apiano[0], aclick[0]], '/playbuf_vu');
    Out.ar(0, apiano);
    Out.ar(3, aclick);
}).add;
);

(

var w, composer, g_title, g_levels, w_width, w_height, play_btns, timer;
var snd_player, snd_piano;
var gui_osc_port = 10001, gui_levels_osc, gui_piano_osc;
var stop_trigger = 1;
var g_timer;
var buf_broadway = Buffer.read(s, "/Users/serj/work/music/sounds/Julia Wolfe East Broadway track.wav");

// NODE.JS VISUAL TIMER FOR IPAD
var osc_node_timer = NetAddr.new("127.0.0.1", 3333);
w_width = 400;
w_height = 400;

w = Window.new("Julia Wolfe", Rect(200, 200, w_width, w_height)).alwaysOnTop_(true).background_(Color.gray).front;

// SOUND
snd_player = Synth.newPaused(\playbuf, [\buf, buf_broadway]);

// TIMER
timer = OscTimerClock.new;

// TITLE
g_title = GTitle(w, composer: "Julia Wolfe", piece: "East Broadway");

// GUI TIMER
g_timer = GOscTimer(w, oscPort: gui_osc_port);

// LEVELS
g_levels = GMixN(w, number: 2, init: 0.7);
g_levels.title_(0, 'TRK');
g_levels.title_(1, 'CLIK');

// get levels via OSC
o = OSCFunc({ |msg|
    if(msg[1] == 0) { snd_player.set(\trackVol, msg[2])};
    if(msg[1] == 1) { snd_player.set(\clickVol, msg[2])};
    }, '/GMixN', nil, 10001);

// PLAYER BUTTONS
play_btns = GPlayerButtons(w);
play_btns.playButton({
    "play".postln;
    snd_player.run(true);
    timer.start;
    // send to node.js
    osc_node_timer.sendMsg("/timer", "start");
});

play_btns.stopButton({
    stop_trigger = stop_trigger * (-1);
    "stop".postln;
    snd_player.set(\pos, 0, \trigger, stop_trigger);
    snd_player.run(false);
    timer.stop;
    // send to node.js
    osc_node_timer.sendMsg("/timer", "stop");
});

play_btns.pauseButton({|value|
    postln("pause" + value);
    snd_player.run(value.not);
    if(value,
        {
            timer.pause;
            // send to node.js
            osc_node_timer.sendMsg("/timer", "pause");
        },
        {
            timer.resume;
            // send to node.js
            osc_node_timer.sendMsg("/timer", "resume");
        }
    );
});

play_btns.setInitState;

gui_levels_osc = OSCFunc({ |msg|
    {
        g_levels.vu_(0, msg[3], msg[5]);
        g_levels.vu_(1, msg[4], msg[6]);
    }.defer;
}, '/playbuf_vu');

gui_piano_osc = OSCFunc({ |msg|
    { g_levels.vu_(0, msg[3], msg[4]) }.defer;
}, '/piano_vu');


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
    buf_broadway.free;
};
)