(
~yves_daoust = Buffer.read(s, "/Users/serj/work/music/sounds/Daoust_Petite_musique_sentimentale.aiff");


SynthDef("playbuf", {
    arg out = 0, buf, pos = 0, trigger = 1;
    var asrc, t = 1, snd, imp, delimp;
    t = Changed.kr(trigger);
    snd = PlayBuf.ar(2, buf, trigger: t, startPos: pos);
    Out.ar(out, snd);

    OscVU.kr(snd, '/playbuf_vu');
}).add;
)


(
var w, composer, g_title, g_levels, w_width, w_height, play_btns, snd_player, timer;
var gui_osc_port = 10001, gui_levels_osc;
var stop_trigger = 1;
var g_timer;
w_width = 700;
w_height = 500;


w = Window.new("Yves Daoust", Rect(0, 0, w_width, w_height)).alwaysOnTop_(true).background_(Color.gray).front;

// SOUND
snd_player = Synth.newPaused(\playbuf, [\buf, ~yves_daoust]);

// TIMER
timer = Task({
    var i = 0, b;
    b = NetAddr.new("127.0.0.1", gui_osc_port);
    while(true, {
        i = i + 1;
        b.sendMsg("/gOscTimer", i.asTimeString[0..7]);
        1.wait;
    });
});

// TITLE
g_title = GTitle(composer: "Yves Daoust", piece: "Petite Musique Sentimentale");

// GUI TIMER
g_timer = GOscTimer(oscPort: gui_osc_port);

// LEVELS
g_levels = GLevelN(number: 4);
g_levels.title(0, 'IN');
g_levels.title(1, '');
g_levels.title(2, 'L');
g_levels.title(3, 'R');

// PLAYER BUTTONS
play_btns = GPlayerButtons();
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
    if(value,
        {timer.pause},
        {timer.resume}
    );
});

play_btns.setInitState;

gui_levels_osc = OSCFunc({ |msg|
    {
        g_levels.value(2, msg[3]);
        g_levels.value(3, msg[4]);
        g_levels.peakLevel(2, msg[5]);
        g_levels.peakLevel(3, msg[6]);
    }.defer;
}, '/playbuf_vu');


// LAYOUT
w.layout_ (
    VLayout(g_title, HLayout(g_levels, g_timer), play_btns).spacing_(0).margins_(20);
);

w.onClose = {
    snd_player.free;
    timer.stop;
    gui_levels_osc.free;
};
)