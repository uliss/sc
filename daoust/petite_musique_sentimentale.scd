SynthDef("playbuf", {
    var aout = SinOsc.ar(440);
    Out.ar(0, aout);
}).add;

(
var w, composer, title, levels, w_width, w_height, play_btns, snd_player;
w_width = 600;
w_height = 400;

w = Window.new("Yves Daoust", Rect(200, 200, w_width, w_height)).background_(Color.gray).front;

// SOUND
snd_player = Synth.newPaused(\playbuf, [\freq, 1000]);

// TITLE
title = GTitle.new(w);
title.composer("Yves Daoust");
title.piece("Petite Musique Sentimentale");

// LEVELS
levels = GLevelN.new(w, number: 4);

// PLAYER BUTTONS
play_btns = GPlayerButtons.new(w);
play_btns.playButton({
    "play".postln;
    snd_player.run(true);
});

play_btns.stopButton({
    "stop".postln;
    snd_player.run(false);
});

play_btns.pauseButton({|value|
    postln("pause" + value);
    snd_player.run(value.not);
});

play_btns.setInitState;




// LAYOUT
w.layout_ (
    VLayout(title, levels, play_btns);
);

w.onClose = {
    snd_player.free;
};
)