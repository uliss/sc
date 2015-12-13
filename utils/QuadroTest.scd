Server.default = s = Server.new("MBOX Output", NetAddr("127.0.0.1", 58009), ServerOptions.new.device_("Mbox Pro").numOutputBusChannels_(4)).makeWindow;

s.boot;

(
var g_title, g_btn1, g_btn2, g_btn3, g_btn4, g_btn_cycle;
var w_width = 700, w_height = 500;
var s_cycle;

w = Window.new("Quadro test",
    Rect(200, 200, w_width, w_height)).alwaysOnTop_(true).background_(Color.gray).front;


SynthDef("test2", {
    arg bus = 0, freq = 60.midicps;
    var out, env;
    env = Env.perc(0.001, 1, 1, -4);
    out = SinOsc.ar(freq, mul: EnvGen.kr(env, doneAction: 2));
    Out.ar(bus, out);
}).add;

// TITLE
g_title = GTitle(composer: "SOUND TESTS", piece: "Quadro test");

// BUTTONS
g_btn1 = Button().states_([["Front left", Color.black, Color.white]]);
g_btn2 = Button().states_([["Front right", Color.black, Color.white]]);
g_btn3 = Button().states_([["Back left", Color.black, Color.white]]);
g_btn4 = Button().states_([["Back right", Color.black, Color.white]]);
g_btn_cycle = Button().states_([["Cycle", Color.black, Color.white],["Cycle", Color.black, Color.gray]]);


s_cycle = Task({
    var i = 0, btns = [g_btn1, g_btn2, g_btn3, g_btn4];
    while(true, {
        p = (i % 4);
        i = i + 1;
        {btns[p].valueAction_(0)}.defer;
        0.2.wait;
    });
});

g_btn_cycle.action_({
    arg btn;
    if((btn.value == 1), {
        s_cycle.start;
    }, {
        s_cycle.pause;
    });
});


g_btn1.action_({ |btn|
    Synth("test2", [\bus, 0, \freq, 72.midicps]);
});

g_btn2.action_({ |btn|
    Synth("test2", [\bus, 1, \freq, 74.midicps]);
});

g_btn3.action_({ |btn|
    Synth("test2", [\bus, 2, \freq, 76.midicps]);
});

g_btn4.action_({ |btn|
    Synth("test2", [\bus, 3, \freq, 77.midicps]);
});\


w.layout_(
    VLayout(
        g_title,
        VLayout(
            HLayout(
                g_btn1, g_btn2
            ),
            HLayout(
                g_btn3, g_btn4
            ),
            g_btn_cycle
        )
    )
);

w.onClose = {
    s_cycle.stop;
};

)