Server.default = s = Server.new("MBOX Output", NetAddr("127.0.0.1", 58009), ServerOptions.new.device_("Mbox Pro")).makeWindow.alwaysOnTop_(true);


s.makeWindow.boot;

(
// PIANO
SynthDef("piano", {
    arg out = 0, in = 0, pan = 0;
    var ain, snd;

    ain = SoundIn.ar(in);
    ain = ain * Line.ar(0, 1, 10);
    snd = Pan2.ar(ain, pan);

    // OscVU.kr(snd, '/piano_vu');
    // Out.ar(out,  snd);
}).add;


// AUDIO TRACK PLAYER
SynthDef("playbuf", {
    arg out = 0, buf, pos = 0, trigger = 1, mul = 1;
    var asrc, t = 1, snd, imp, delimp;
    var r_level = 0, l_level = 0, aR, aL;

    // t = Changed.kr(trigger);
    snd = PlayBuf.ar(2, buf, trigger: trigger, startPos: pos);
    snd = mul * snd;

    // send VU via OSC
    OscVU.kr(snd, '/playbuf_vu');
    Out.ar(out, snd);
}).add;
)

(
var w, composer, g_title, g_levels, w_width, w_height, play_btns, timer;
var snd_player, snd_piano;
var gui_osc_port = 10001, gui_levels_osc, gui_piano_osc;
var stop_trigger = 1;
var g_timer;
var g_sections;
var g_btn_next, g_btn_prev;
var buf_normandeau = Buffer.read(s, "/Users/serj/work/music/sounds/Figures de rhétorique 1644, Tape.aif");
var osc_levels;
// NODE.JS VISUAL TIMER FOR IPAD
var osc_node_timer = NetAddr.new("127.0.0.1", 3333);
// in seconds
var section_list = [0, 30, 52, 68, 90, 102, 110, 118, 122, 126, 130, 144, 150, 162,
    180, 185, 210, 219, 225, 240, 251, 264, 270, 275, 281, 286, 292, 295, 300, 310,
    318, 333, 347, 359, 369, 372, 390, 396, 420, 429, 433, 437, 441, 445, 449,
    450, 459, 471, 483, 494, 505, 515, 523, 536, 540, 547, 558, 568, 588, 599,
    609, 620, 630, 641, 650, 657, 660, 674, 690, 700, 707, 711, 720, 741, 748,
    763, 780, 810
];
var section_list_txt = section_list.collect({|i| i.asTimeString[3..7]});
var play_position = 0;

w_width = 480;
w_height = 340;

w = Window.new("Robert Normandeau", Rect(200, 200, w_width, w_height)).alwaysOnTop_(true).background_(Color.gray).front;

// SOUND
snd_player = Synth.newPaused(\playbuf, [\buf, buf_normandeau]);
snd_piano = Synth.new(\piano, []);

// TIMER
timer = Task({
     var i = play_position, oscNetAddr = NetAddr("127.0.0.1", 10001);
     while(true, {
        oscNetAddr.sendMsg('/gOscTimer', i.asTimeString[0..7]);

        // send seconds
        osc_node_timer.sendMsg('/time', i);

        if(section_list.indexOf(i).notNil) {
            osc_node_timer.sendMsg('/figure', i);
        };

        i = i + 1;
        1.wait;
    });
});

// TITLE
g_title = GTitle(w, composer: "Robert Normandeau", piece: "Figures de rhétorique");

// GUI TIMER
g_timer = GOscTimer(w, oscPort: gui_osc_port).color_(Color.fromHexString("f1c40f"));

// SECTIONS
g_sections = PopUpMenu(w);
g_sections.items = section_list_txt;
g_sections.action_({ |item|
    var idx = item.value, time, trig = 1;
    play_position = section_list[idx];
    play_btns.btn_stop.valueAction_(1);
    play_btns.btn_play.valueAction_(1);
});

// LEVELS
g_levels = GMixN(w, number: 4, init: 0.6);
g_levels.title_(0, 'IN');
g_levels.title_(1, '');
g_levels.title_(2, 'L');
g_levels.title_(3, 'R');
g_levels.value_(0, 0);
g_levels.value_(1, 0);

// get levels via OSC
osc_levels = OSCFunc({ |msg|
    if(msg[1] == 2) {
        msg[2].postln;
        snd_player.set(\mul, msg[2]);
        {g_levels.value_(3, msg[2])}.defer;
    };
    }, '/GMixN', nil, 10001);

// PLAYER BUTTONS
play_btns = GPlayerButtons(w);
play_btns.playButton({
    snd_player.free;
    snd_player = Synth.new(\playbuf, [\buf, buf_normandeau, \pos, (play_position * 44100), \trigger, 1]);
    timer.start;
    osc_node_timer.sendMsg("/timer", "start");

    ("play at " + play_position.asTimeString[3..7]).postln;
});

play_btns.stopButton({
    stop_trigger = stop_trigger * (-1);
    "stop".postln;
    snd_player.set(\pos, 0, \trigger, stop_trigger);
    snd_player.run(false);
    timer.stop;
    osc_node_timer.sendMsg("/timer", "stop");
    g_levels.vu_(2, 0, 0);
    g_levels.vu_(3, 0, 0);
});

play_btns.pauseButton({|value|
    postln("pause" + value);
    snd_player.run(value.not);
    if(value,
        {
            timer.pause;
            osc_node_timer.sendMsg("/timer", "pause");
        },
        {
            timer.resume;
            osc_node_timer.sendMsg("/timer", "resume");
        }
    );
});

play_btns.setInitState;

gui_levels_osc = OSCFunc({ |msg|
    {
        g_levels.vu_(2, msg[3], msg[5]);
        g_levels.vu_(3, msg[4], msg[6]);
    }.defer;
}, '/playbuf_vu');

gui_piano_osc = OSCFunc({ |msg|
    { g_levels.vu_(0, msg[3], msg[4]) }.defer;
}, '/piano_vu');

g_btn_prev = Button(w, Rect(0, 0, 60, 20)).states_([["Prev"]]).action_({|btn|
    if(g_sections.value > 0) {
        g_sections.valueAction_(g_sections.value - 1);
    }
});

g_btn_next = Button(w, Rect(0, 0, 60, 20)).states_([["Next"]]).action_({|btn|
    if(g_sections.value < (g_sections.items.size - 1)) {
        g_sections.valueAction_(g_sections.value + 1);
    }
});


// LAYOUT
g_title.moveTo(20, 20);
g_levels.moveTo(20, 120);
g_timer.moveTo(200, 120);
g_sections.moveTo(200, 180);
play_btns.moveTo(20, 300);
g_btn_prev.moveTo(320, 300);
g_btn_next.moveTo(400, 300);

w.view.keyDownAction = { |view, char, mod, unicode, keycode, key|
        [view, char, mod, unicode, keycode, key].postln;
    switch(unicode,
        112, {play_btns.btn_play.valueAction_(1)},
        32, {play_btns.btn_stop.valueAction_(1)}
    );
};


w.onClose = {
    timer.stop;
    gui_levels_osc.free;
    gui_piano_osc.free;
    osc_levels.free;
    snd_player.free;
    snd_piano.free;
    buf_normandeau.free;
    s.freeAll;
};
)